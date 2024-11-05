package www.raven.jc.ws;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.config.ImProperty;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;
import www.raven.jc.ws.impl.DeliveredAckHandler;
import www.raven.jc.ws.impl.PrivateHandler;
import www.raven.jc.ws.impl.ReadAckHandler;
import www.raven.jc.ws.impl.RoomHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * notification handler
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{token}")
@Data
public class WsConnectionsCenter {

	/**
	 * 在线连接数map
	 */
	public static final Map<Integer, Session> SESSION_POOL = new HashMap<>();
	/**
	 * 心跳辅助map
	 */
	public static final Map<Session, Integer> HEARTBEAT_MAP = new HashMap<>();
	public static final String HEARTBEAT = "ping";
	public static CopyOnWriteArraySet<WsConnectionsCenter> webSockets = new CopyOnWriteArraySet<>();
	private static RedissonClient redissonClient;
	private static ImProperty imProperty;
	private static PrivateHandler privateHandler;
	private static RoomHandler roomHandler;
	private static ReadAckHandler readAckHandler;
	private static DeliveredAckHandler deliveredAckHandler;
	private static WsMessageHandlerFactory wsMessageHandlerFactory;
	/**
	 * 该连接的用户id
	 */
	protected Integer userId;
	/**
	 * 与客户端的连接会话
	 **/
	protected Session session;

	@Autowired
	public void setRedissonClient(RedissonClient redissonClient) {
		WsConnectionsCenter.redissonClient = redissonClient;
	}

	@Autowired
	public void setImProperty(ImProperty imProperty) {
		WsConnectionsCenter.imProperty = imProperty;
	}

	@Autowired
	public void setWsMessageHandlerFactory(WsMessageHandlerFactory wsMessageHandlerFactory) {
		WsConnectionsCenter.wsMessageHandlerFactory = wsMessageHandlerFactory;
	}


	@OnOpen
	public void onOpen(Session session,
	                   @PathParam(value = "token") String token) {
		TokenDTO verify = JwtUtil.parseToken(token, "爱你老妈");
		session.getUserProperties().put("userDto", verify);
		this.userId = verify.getUserId();
		this.session = session;
		Session sessionExisted = SESSION_POOL.get(verify.getUserId());
		if (sessionExisted != null) {
			try {
				sessionExisted.close();
			} catch (Exception e) {
				log.error("关闭已存在的session失败");
			}
		}
		webSockets.add(this);
		SESSION_POOL.put(this.userId, session);
		redissonClient.getBucket("ws:" + this.userId).set(imProperty.getWsTopic());
		log.info("ws: 有新的连接,用户id为{},总数为:{}", this.userId, webSockets.size());
	}

	@OnClose
	public void onClose() {
		webSockets.remove(this);
		SESSION_POOL.remove(this.userId);
		log.info("ws:用户id {} 连接断开，总数为:{}", this.userId, webSockets.size());
	}

	@OnMessage
	public void onMessage(String message) {
		//心跳检测
		if (Objects.equals(message, HEARTBEAT)) {
			HEARTBEAT_MAP.put(this.session, 0);
			return;
		}
		log.info("----WebSocket收到客户端发来的消息:{}", message);
		MessageDTO messageDTO = JsonUtil.jsonToObj(message, MessageDTO.class);
		WsMessageHandler handler = wsMessageHandlerFactory.getHandler(messageDTO.getType());
		if (handler != null) {
			handler.onMessage(messageDTO, this.session);
		} else {
			log.error("未找到对应的消息处理器");
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		SESSION_POOL.remove(this.userId);
		log.error("--Websocket:内部错误");
		log.error("Stack trace: {}", (Object) error.getStackTrace());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}