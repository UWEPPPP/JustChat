package www.raven.jc.ws;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.entity.dto.MessageDTO;

/**
 * delivered ack handler
 *
 * @author 刘家辉
 * @date 2024/06/13
 */
@Slf4j
@Component
public class DeliveredAckHandler implements BaseHandler {

  @Autowired
  private RedissonClient redissonClient;

  @Override
  public void onMessage(MessageDTO message, Session session) {
    //从redis中删除所有已经送达的消息
    Integer userId = message.getUserInfo().getUserId();
    boolean delete = redissonClient.getScoredSortedSet(
        OfflineMessagesConstant.PREFIX + userId.toString()).delete();
    if (delete) {
      log.info("delete offline message success");
    } else {
      log.error("delete offline message fail");
    }
  }
}
