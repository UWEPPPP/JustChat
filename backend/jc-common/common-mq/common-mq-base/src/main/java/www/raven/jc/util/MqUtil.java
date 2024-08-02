package www.raven.jc.util;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * mq util
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Slf4j
public class MqUtil {

  public static <T> void sendMsg(RocketMQTemplate rocketMQTemplate,
      String topic,
      String tag,
      T data) {
    rocketMQTemplate.asyncSend(topic + ":" + tag, createMsg(data), new SendCallback() {
      @Override
      public void onSuccess(
          org.apache.rocketmq.client.producer.SendResult sendResult) {
        log.info("--rocketMq send notice success");
      }

      @Override
      public void onException(Throwable e) {
        log.error("--rocketMq send notice error", e);
      }
    });
  }

  public static <T> Message<String> createMsg(T data) {
    return MessageBuilder
        .withPayload(JsonUtil.objToJson(data))
        .setHeader(MessageConst.PROPERTY_KEYS, IdUtil.getSnowflakeNextIdStr()).build();
  }

}
