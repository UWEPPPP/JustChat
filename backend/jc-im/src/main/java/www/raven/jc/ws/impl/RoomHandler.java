package www.raven.jc.ws.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.websocket.Session;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.config.ImProperty;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.WsMessageHandlerConstant;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.event.SaveMsgEvent;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.service.MessageService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.WsMessageHandler;

/**
 * web socket service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Slf4j
@Component
public class RoomHandler implements WsMessageHandler {

  @Autowired
  private MessageService messageService;
  @Autowired
  private UserRpcService userRpcService;
  @Autowired
  private RocketMQTemplate rocketMQTemplate;
  @Autowired
  private RedissonClient redissonClient;
  @Autowired
  private UserRoomDAO userRoomDAO;
  @Autowired
  private ImProperty imProperty;

  @Override
  public void onMessage(MessageDTO message, Session session) {
    //查找房间内的所有用户
    Integer roomId = message.getBelongId();
    Message realMessage = new Message().setSenderId(message.getBelongId())
        .setContent(message.getText())
        .setTimestamp(new Date(message.getTime()))
        .setReceiverId(String.valueOf(roomId))
        .setType(message.getType());
    List<Integer> userIds = userRoomDAO.getBaseMapper()
        .selectList(new QueryWrapper<UserRoom>().eq("room_id", message.getBelongId())).stream()
        .map(UserRoom::getUserId).collect(Collectors.toList());
    messageService.saveOfflineMsgAndReadAck(realMessage, userIds);
    broadcast(redissonClient, userIds, message, rocketMQTemplate);
    MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SAVE_HISTORY_MSG,
        imProperty.getInTopic(), JsonUtil.objToJson(new SaveMsgEvent().setMessage(realMessage)
            .setType(WsMessageHandlerConstant.ROOM)));
  }

  @Override
  public String getType() {
    return WsMessageHandlerConstant.ROOM;
  }

}

