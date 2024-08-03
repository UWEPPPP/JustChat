package www.raven.jc.ws;

import jakarta.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

/**
 * base handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */

public interface BaseHandler {

  /**
   * on message
   */
  void onMessage(MessageDTO message, Session session);

  /**
   * 广播消息(多实例间)
   */
  default void broadcast(RedissonClient redissonClient, List<Integer> userIds,
      MessageDTO message,
      RocketMQTemplate rocketMQTemplate) {
    Map<String, List<Integer>> map = new HashMap<>();
    // 按照topic给userId分组
    for (Integer id : userIds) {
      String wsTopic = redissonClient.getBucket("ws:" + id).get().toString();
      List<Integer> thisTopicNeedSendIdList = map.computeIfAbsent(wsTopic, k -> new ArrayList<>());
      thisTopicNeedSendIdList.add(id);
    }
    //根据分组发送消息
    for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
      String topic = entry.getKey();
      List<Integer> theTopicIds = entry.getValue();
      MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SEND_MESSAGE, topic,
          new WsMsg().setMessage(JsonUtil.objToJson(message)).setTo(theTopicIds));
    }
  }
}
