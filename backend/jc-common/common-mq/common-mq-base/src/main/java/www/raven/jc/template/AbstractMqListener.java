package www.raven.jc.template;

import static www.raven.jc.constant.MqConstant.HEAD;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RedissonClient;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.util.JsonUtil;

/**
 * 通用MQListener模板
 *
 * @author 刘家辉
 * @date 2024/06/01
 */
@Slf4j
public abstract class AbstractMqListener implements RocketMQListener<MessageExt> {

  protected final RedissonClient redissonClient;

  public AbstractMqListener(RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
  }

  @Override
  public void onMessage(MessageExt messageExt) {
    if (checkMsgValid(messageExt, redissonClient)) {
      return;
    }
    byte[] body = messageExt.getBody();
    String message = new String(body, StandardCharsets.UTF_8);
    log.info("--RocketMq receive event:{}", message);
    onMessage0(message, messageExt.getTags());
    protectMsg(messageExt, redissonClient);
  }

  public boolean checkMsgValid(MessageExt msg,
      RedissonClient redissonClient) {
    Object id = msg.getKeys();
    if (id == null || redissonClient.getBucket(HEAD + id).isExists()) {
      log.info("--RocketMq 重复或非法的消息，不处理");
      return true;
    }
    return false;
  }

  public void protectMsg(MessageExt msg,
      RedissonClient redissonClient) {
    Object id = msg.getKeys();
    redissonClient.getBucket(HEAD + id).set(id, MqConstant.EXPIRE_TIME, TimeUnit.MINUTES);
  }

  public <T> T parseMessage(String message, Class<T> clazz) {
    return JsonUtil.jsonToObj(message, clazz);
  }

  /**
   * 实际的消息处理逻辑
   */
  public abstract void onMessage0(String message1, String tags);
}
