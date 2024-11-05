package www.raven.jc.ws;

import jakarta.websocket.Session;
import www.raven.jc.entity.dto.MessageDTO;

/**
 * base handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */

public interface WsMessageHandler {

	/**
	 * on message
	 */
	void onMessage(MessageDTO message, Session session);

	String getType();

}
