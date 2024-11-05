package www.raven.jc.ws.impl;

import org.apache.dubbo.rpc.RpcContext;

import cn.hutool.core.util.HashUtil;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.WsMessageHandlerConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.ws.AbstractWsMessageHandler;

import java.util.List;

import static www.raven.jc.config.IMLoadBalanceConfig.ID_HASH;

/**
 * friend chat handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class PrivateHandler extends AbstractWsMessageHandler {


	@Override
	public void onMessage(MessageDTO message, Session session) {
		Integer friendId = message.getBelongId();
		Message realMessage = new Message().setId(getId(friendId, message.getUserId()))
				.setSenderId(message.getBelongId())
				.setContent(message.getText())
				.setReceiverId(String.valueOf(friendId))
				.setType(message.getType());
		List<Integer> ids = List.of(friendId);
		messageService.saveOfflineMsgAndReadAck(realMessage, ids);
		broadcast(redissonClient, ids, message, rocketMQTemplate);
		RMap<String, Message> map = redissonClient.getMap(Message.REDIS_KEY);
		map.put(realMessage.getId(), realMessage);
	}

	@Override
	public String getType() {
		return WsMessageHandlerConstant.FRIEND;
	}

	public String getId(Integer friendId, Integer userId) {
		int hash = HashUtil.intHash(friendId + userId);
		RpcContext.getClientAttachment().setAttachment(ID_HASH, hash);
		return idRpcService.getId().getData();
	}

}
