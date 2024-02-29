package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ApplyStatusConstant;
import www.raven.jc.constant.ChatUserMqConstant;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.FriendChat;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.event.model.FriendMsgEvent;
import www.raven.jc.event.model.RoomMsgEvent;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MongoUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.WebsocketService;

/**
 * chat service impl
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

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
    private StreamBridge streamBridge;
    @Autowired
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveRoomMsg(UserInfoDTO user, MessageDTO message, Integer roomId) {
        long timeStamp = message.getTime();
        String text = message.getText();
        Message realMsg = new Message()
            .setContent(text)
            .setTimestamp(new Date(timeStamp))
            .setSender(user)
            .setType(MessageConstant.ROOM)
            .setReceiverId(String.valueOf(roomId));
        List<Integer> userIds = userRoomDAO.getBaseMapper().selectList(
                new QueryWrapper<UserRoom>().eq("room_id", roomId).
                    eq("status", ApplyStatusConstant.APPLY_STATUS_AGREE)).
            stream().map(UserRoom::getUserId).collect(Collectors.toList());
        //TODO 离线消息

        Map<Integer, Session> sessionMap = WebsocketService.GROUP_SESSION_POOL.get(roomId);
        //对离线用户进行离线信息保存
        userIds.forEach(
            id -> {
                if (sessionMap.get(id) == null || !sessionMap.get(id).isOpen()) {
                    RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(OfflineMessagesConstant.PREFIX + id);
                    scoredSortedSet.add(timeStamp, JsonUtil.objToJson(message));
                }
            }
        );

        //保存进入历史消息db
        messageDAO.getBaseMapper().save(realMsg);
        //更新聊天室的最后一条消息
        Assert.isTrue(roomDAO.getBaseMapper().updateById(new Room().setRoomId(roomId).setLastMsgId(realMsg.getMessageId().toString())) > 0, "更新失败");
        RoomMsgEvent roomMsgEvent = new RoomMsgEvent(JsonUtil.objToJson(user), roomId, userIds, JsonUtil.objToJson(realMsg));
        //通知user模块有新消息
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(roomMsgEvent), ChatUserMqConstant.TAGS_CHAT_ROOM_MSG_RECORD));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveFriendMsg(MessageDTO message, UserInfoDTO user, Integer friendId) {
        String fixId = MongoUtil.concatenateIds(user.getUserId(), friendId);
        Message realMsg = new Message().setContent(message.getText())
            .setTimestamp(new Date(message.getTime()))
            .setSender(user)
            .setType(MessageConstant.FRIEND)
            .setReceiverId(fixId);
        //保存消息
        messageDAO.getBaseMapper().save(realMsg);
        //保存最后一条消息
        FriendChat friendChat = new FriendChat().setFixId(fixId)
            .setLastMsgId(realMsg.getMessageId().toString());
        Assert.isTrue(friendChatDAO.save(friendChat), "插入失败");
        FriendMsgEvent friendMsgEvent = new FriendMsgEvent(user.getUserId(), friendId, JsonUtil.objToJson(realMsg));
        //通知user模块有新消息
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(friendMsgEvent), ChatUserMqConstant.TAGS_CHAT_FRIEND_MSG_RECORD));
    }

}
