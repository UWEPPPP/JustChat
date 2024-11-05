package www.raven.jc.ws;

import static www.raven.jc.ws.WsConnectionsCenter.SESSION_POOL;
import static www.raven.jc.ws.WsConnectionsCenter.webSockets;

import jakarta.websocket.Session;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * ws tools
 *
 * @author Rawven
 * @date 2024/09/02
 */
@Slf4j
public class WsTools {

	public static void sendOneMessage(Integer id, String message) {
		Session session = SESSION_POOL.get(id);
		if (session != null && session.isOpen()) {
			try {
				log.info("-Websocket: 单点消息:{}", message);
				session.getAsyncRemote().sendText(message);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	public static void sendBatchMessage(String message, List<Integer> ids) {
		log.info("websocket:广播消息:{}", message);
		for (Integer id : ids) {
			Session session = SESSION_POOL.get(id);
			if (session != null && session.isOpen()) {
				try {
					session.getAsyncRemote().sendText(message);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	public static void sendAllMessage(String message) {
		log.info("--Websocket: 广播消息:{}", message);
		for (WsConnectionsCenter handler : webSockets) {
			if (handler.session.isOpen()) {
				handler.session.getAsyncRemote().sendText(message);
			}
		}
	}

}
