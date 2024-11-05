package www.raven.jc.ws.impl;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.constant.WsMessageHandlerConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.ws.AbstractWsMessageHandler;

/**
 * delivered ack handler
 *
 * @author 刘家辉
 * @date 2024/06/13
 */
@Slf4j
@Component
public class DeliveredAckHandler extends AbstractWsMessageHandler {


	@Override
	public void onMessage(MessageDTO message, Session session) {
		//从redis中删除所有已经送达的消息
		Integer userId = message.getUserId();
		boolean delete = redissonClient.getScoredSortedSet(
				OfflineMessagesConstant.PREFIX + userId.toString()).delete();
		if (delete) {
			log.info("delete offline message success");
		} else {
			log.error("delete offline message fail");
		}
	}

	@Override
	public String getType() {
		return WsMessageHandlerConstant.MSG_DELIVERED_ACK;
	}
}
