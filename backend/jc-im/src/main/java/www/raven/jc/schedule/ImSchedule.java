package www.raven.jc.schedule;

import static www.raven.jc.constant.MessageConstant.FRIEND;
import static www.raven.jc.constant.MessageConstant.ROOM;
import static www.raven.jc.entity.po.Message.REDIS_KEY;
import static www.raven.jc.ws.WebsocketService.HEARTBEAT;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.MessageReadAckDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.entity.po.FriendChat;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.MessageReadAck;
import www.raven.jc.entity.po.Room;
import www.raven.jc.ws.WebsocketService;

/**
 * ws schedule
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class ImSchedule {


  /**
   * 默认离线消息过期时间为7天
   */
  private static final long OFFLINE_MESSAGE_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
  @Autowired
  private RedissonClient redissonClient;
  @Autowired
  private MessageReadAckDAO messageReadAckDAO;
  @Autowired
  private MessageDAO messageDAO;
  @Autowired
  private RoomDAO roomDAO;
  @Autowired
  private FriendChatDAO friendChatDAO;

  /**
   * Websocket心跳机制
   */
  @Scheduled(cron = "0/10 * * * * ?")
  public void checkRoomWs() throws IOException {
    log.info(">>>>>>>>>>> xxl-job--心跳机制运作中");
    CopyOnWriteArraySet<WebsocketService> sockets = WebsocketService.webSockets;
    Map<Session, Integer> map = WebsocketService.HEARTBEAT_MAP;
    //遍历所有的WebSocket连接
    for (WebsocketService socket : sockets) {
      Session session = socket.getSession();
      if (session == null) {
        continue;
      }
      //断开心跳数超过3次的连接
      if (map.get(session) >= 3) {
        session.close();
        WebsocketService.webSockets.remove(socket);
        WebsocketService.HEARTBEAT_MAP.remove(session);
      }
    }
    //发出心跳
    for (WebsocketService socket : sockets) {
      Session session = socket.getSession();
      if (session == null) {
        continue;
      }
      session.getAsyncRemote().sendText(HEARTBEAT);
      map.put(session, map.get(session) + 1);
    }
  }

  @XxlJob(value = "saveBatchMessageHandler")
  public void saveBatchMessage() {
    log.info(">>>>>>>>>>> xxl-job--批量保存消息");
    try {
      // 获取Redis中的消息
      RMap<String, Message> map = redissonClient.getMap(REDIS_KEY);
      List<Message> messages = new ArrayList<>(map.values());

      if (messages.isEmpty()) {
        log.info("没有需要处理的消息");
        return;
      }
      // 清空Redis中的消息
      map.clear();

      // 批量保存消息
      messageDAO.saveBatch(messages);
      log.info("成功保存 {} 条消息", messages.size());

      // 批量更新最后一条消息
      updateLastMessages(messages);
    } catch (Exception e) {
      log.error("批量保存消息失败", e);
    }
  }

  private void updateLastMessages(List<Message> messages) {
    messages.forEach(message -> {
      if (ROOM.equals(message.getType())) {
        //更新群聊的最后一条消息
        Assert.isTrue(roomDAO.updateById(
            new Room().setRoomId(Integer.valueOf(message.getReceiverId()))
                .setLastMsgId(message.getId())), "更新失败");
      } else if (FRIEND.equals(message.getType())) {
        //更新好友的最后一条消息id
        FriendChat friendChat = friendChatDAO.getBaseMapper().selectOne(
            new QueryWrapper<FriendChat>().eq("fix_id", message.getReceiverId()));
        Assert.notNull(friendChat, "好友不存在");
        int i = friendChatDAO.getBaseMapper()
            .updateById(friendChat.setLastMsgId(message.getId()));
        Assert.isTrue(i > 0, "更新失败");
      }
    });
  }

  /**
   * 定期清理过期的离线消息
   */
  @XxlJob(value = "deleteOfflineMessageHandler")
  public void checkOfflineMessage() {
    log.info(">>>>>>>>>>> xxl-job--清理过期的离线消息");
    long currentTime = System.currentTimeMillis();
    //遍历删除所有用户的过期离线消息
    for (String key : redissonClient.getKeys()
        .getKeysByPattern(OfflineMessagesConstant.PREFIX_MATCH)) {
      //获取用户id
      RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(key);
      //删除七天前的离线消息
      set.removeRangeByScore(0, true, currentTime - OFFLINE_MESSAGE_EXPIRATION_TIME, true);
    }
  }

  @XxlJob(value = "deleteMessageAckHandler")
  public void checkOfflineMessageAck() {
    log.info(">>>>>>>>>>> xxl-job--清理过期的已读回执");
    long currentTime = System.currentTimeMillis();
    //删除七天前的已读回执
    messageReadAckDAO.getBaseMapper().delete(new QueryWrapper<MessageReadAck>().
        lt("create_time", currentTime - OFFLINE_MESSAGE_EXPIRATION_TIME));
  }
}
