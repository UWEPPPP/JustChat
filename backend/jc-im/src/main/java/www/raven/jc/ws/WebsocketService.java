package www.raven.jc.ws;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.config.ImProperty;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

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
public class WebsocketService {

  /**
   * 在线连接数map
   */
  public static final Map<Integer, Session> SESSION_POOL = new HashMap<>();
  /**
   * 心跳辅助map
   */
  public static final Map<Session, Integer> HEARTBEAT_MAP = new HashMap<>();

  public static final String HEARTBEAT = "ping";
  /**
   * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
   * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
   */
  public static CopyOnWriteArraySet<WebsocketService> webSockets = new CopyOnWriteArraySet<>();

  private static RedissonClient redissonClient;
  private static ImProperty imProperty;
  private static PrivateHandler privateHandler;
  private static RoomHandler roomHandler;
  private static ReadAckHandler readAckHandler;
  private static DeliveredAckHandler deliveredAckHandler;

  public BaseHandler baseHandler;
  /**
   * 该连接的用户id
   */
  protected Integer userId;
  /**
   * 与客户端的连接会话
   **/
  protected Session session;

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
    if (Objects.equals(message, HEARTBEAT)) {
      HEARTBEAT_MAP.put(this.session, 0);
      return;
    }
    log.info("----WebSocket收到客户端发来的消息:{}", message);
    MessageDTO messageDTO = JsonUtil.jsonToObj(message, MessageDTO.class);
    switch (messageDTO.getType()) {
      case MessageConstant.FRIEND:
        setBaseHandler(privateHandler);
        log.info("收到好友消息");
        break;
      case MessageConstant.ROOM:
        setBaseHandler(roomHandler);
        log.info("收到群消息");
        break;
      case MessageConstant.MSG_DELIVERED_ACK:
        setBaseHandler(readAckHandler);
        log.info("收到送达回执");
        break;
      case MessageConstant.MSG_READ_ACK:
        setBaseHandler(readAckHandler);
        log.info("收到已读回执");
        break;
      default:
        log.error("未知信息");
    }
    this.baseHandler.onMessage(messageDTO, this.session);
  }

  @OnError
  public void onError(Session session, Throwable error) {
    SESSION_POOL.remove(this.userId);
    log.error("--Websocket:内部错误");
    log.error("Stack trace: {}", (Object) error.getStackTrace());
  }

  public void sendAllMessage(String message) {
    log.info("--Websocket: 广播消息:{}", message);
    for (WebsocketService handler : webSockets) {
      if (handler.session.isOpen()) {
        handler.session.getAsyncRemote().sendText(message);
      }
    }
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Autowired
  public void setPrivateHandler(PrivateHandler privateHandler) {
    WebsocketService.privateHandler = privateHandler;
  }

  @Autowired
  public void setRoomHandler(RoomHandler roomHandler) {
    WebsocketService.roomHandler = roomHandler;
  }

  @Autowired
  public void setReadAckHandler(ReadAckHandler readAckHandler) {
    WebsocketService.readAckHandler = readAckHandler;
  }

  @Autowired
  public void setDeliveredAckHandler(
      DeliveredAckHandler deliveredAckHandler) {
    WebsocketService.deliveredAckHandler = deliveredAckHandler;
  }

  @Autowired
  public void setRedissonClient(RedissonClient redissonClient) {
    WebsocketService.redissonClient = redissonClient;
  }

  @Autowired
  public void setImProperty(ImProperty imProperty) {
    WebsocketService.imProperty = imProperty;
  }
}