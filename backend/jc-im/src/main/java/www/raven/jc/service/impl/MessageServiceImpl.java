package www.raven.jc.service.impl;

import static www.raven.jc.entity.po.Message.REDIS_KEY;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RBatch;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScoredSortedSetAsync;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.config.ImProperty;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.MessageReadAckDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.event.RoomApplyEvent;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.MessageReadAck;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.MessageService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.util.RequestUtil;

/**
 * chat async service impl
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

  @Autowired
  private MessageDAO messageDAO;
  @Autowired
  private UserRoomDAO userRoomDAO;
  @Autowired
  private RoomDAO roomDAO;
  @Autowired
  private FriendChatDAO friendChatDAO;
  @Autowired
  private UserRpcService userRpcService;
  @Autowired
  private RocketMQTemplate rocketMQTemplate;
  @Autowired
  private RedissonClient redissonClient;
  @Autowired
  private HttpServletRequest request;
  @Autowired
  private ImProperty imProperty;
  @Autowired
  private MessageReadAckDAO messageReadAckDAO;

  @Override
  public void saveOfflineMsgAndReadAck(Message message, List<Integer> userIds,
      Integer metaId) {
    long time = message.getTimestamp().getTime();
    // 进行用户的离线信息保存
    CompletableFuture<Void> redisFuture = CompletableFuture.runAsync(() -> {
      RBatch batch = redissonClient.createBatch();
      userIds.parallelStream().forEach(id -> {
        RScoredSortedSetAsync<Object> scoredSortedSet = batch.getScoredSortedSet(
            OfflineMessagesConstant.PREFIX + id.toString());
        scoredSortedSet.addAsync(time, message.getId());
      });
      batch.execute();
    });

    // 进行消息回执的批量插入
    CompletableFuture<Void> dbFuture = CompletableFuture.runAsync(() -> {
      List<MessageReadAck> ackList = userIds.stream().map(id ->
              new MessageReadAck()
                  .setMessageId(message.getId())
                  .setSenderId(message.getSenderId())
                  .setReceiverId(id)
                  .setRoomId(metaId)
                  .setIfRead(false))
          .toList();
      Assert.isTrue(messageReadAckDAO.saveBatch(ackList), "save ack fail");
    });

    // 等待两个异步操作完成
    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(redisFuture, dbFuture);
    try {
      combinedFuture.get();  // 阻塞等待所有任务完成
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Failed to save offline message", e);
    }
  }

  @Override
  public List<MessageVO> getLatestOffline() {
    int userId = RequestUtil.getUserId(request);
    RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(
        OfflineMessagesConstant.PREFIX + userId);
    Collection<String> messageIds = scoredSortedSet.readAll();
    //根据messageIds先从缓存查数据
    Map<String, Message> messageMap = redissonClient.getMap(REDIS_KEY);
    List<Message> messages = new ArrayList<>();
    for (String messageId : messageIds) {
      if (messageMap.containsKey(messageId)) {
        messages.add(messageMap.get(messageId));
      }
    }
    //再从数据库查数据
    List<Message> messages0 = messageDAO.getBaseMapper().selectList(
        new QueryWrapper<Message>().in("id", messageIds));
    messages.addAll(messages0);
    List<Integer> ids = messages.stream().map(Message::getSenderId).toList();
    RpcResult<List<UserInfoDTO>> batchInfo = userRpcService.getBatchInfo(ids);
    Map<Integer, UserInfoDTO> map = batchInfo.getData().stream()
        .collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
    List<MessageVO> messageVos = new ArrayList<>();
    for (Message message : messages) {
      UserInfoDTO user = map.get(message.getSenderId());
      messageVos.add(new MessageVO(message, user));
    }
    return messageVos;
  }


  @Override
  public List<MessageReadAck> getReadMessageAck() {
    int userId = RequestUtil.getUserId(request);
    List<MessageReadAck> readAckList = messageReadAckDAO.getBaseMapper()
        .selectList(new QueryWrapper<MessageReadAck>().eq("sender_id", userId).eq("if_ack", true));
    //删除已经确认的Ack
    messageReadAckDAO.getBaseMapper()
        .delete(new QueryWrapper<MessageReadAck>().eq("sender_id", userId).eq("if_ack", true));
    return readAckList;
  }

  @Override
  public void sendNotice(Integer roomId, Integer userId) {
    Room room = roomDAO.getBaseMapper().selectById(roomId);
    Integer founderId = room.getFounderId();
    //通知user模块 插入一条申请记录
    MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_CHAT_ROOM_APPLY, imProperty.getInTopic(),
        JsonUtil.objToJson(new RoomApplyEvent(userId, founderId, roomId)));
  }

}
