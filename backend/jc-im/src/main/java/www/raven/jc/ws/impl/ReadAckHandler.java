package www.raven.jc.ws.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.WsMessageHandlerConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.MessageReadAck;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.ws.AbstractWsMessageHandler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ack handler
 *
 * @author 刘家辉
 * @date 2024/06/12
 */
@Slf4j
@Component
public class ReadAckHandler extends AbstractWsMessageHandler {


	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public void onMessage(MessageDTO message, Session session) {
		List<String> msgIds = Arrays.asList(JsonUtil.jsonToObj(message.getText(), String[].class));
		//更新最后ack时间
		UserRoom userRoom = userRoomDAO.getById(message.getBelongId());
		Date time = userRoom.getLastAckTime();
		Date now = new Date();
		if (time != null && time.getTime() > now.getTime()) {
			log.error("ack time error");
			session.getAsyncRemote().sendText("ack: time error");
			return;
		}
		userRoom.setLastAckTime(now);
		userRoomDAO.getBaseMapper().updateById(userRoom);
		//批量更新Ack
		List<MessageReadAck> readAckList = messageReadAckDAO.list(
				new QueryWrapper<MessageReadAck>().eq("receiver_id", message.getBelongId())
						.eq("room_id", message.getBelongId()).in("message_id", msgIds));
		readAckList.forEach(messageAck -> messageAck.setIfRead(true));
		if (messageReadAckDAO.updateBatchById(readAckList)) {
			session.getAsyncRemote().sendText("ack: success");
		} else {
			session.getAsyncRemote().sendText("ack: fail");
			log.error("ack fail");
		}
	}

	@Override
	public String getType() {
		return WsMessageHandlerConstant.MSG_READ_ACK;
	}
}
