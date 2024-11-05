package www.raven.jc.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.entity.model.WsMsgModel;
import www.raven.jc.template.AbstractMqListener;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.ws.WsTools;

/**
 * ws listener
 *
 * @author 刘家辉
 * @date 2024/04/21
 */
@Component
@Slf4j
//使用API方式定义可以实现不用硬编码ws_topic
@RocketMQMessageListener(consumerGroup = "${mq.ws_consumer_group}", topic = "${mq.ws_topic}", messageModel = MessageModel.CLUSTERING, selectorExpression = ImImMqConstant.TAGS_SEND_MESSAGE)
public class WsListener extends AbstractMqListener {

	public WsListener(RedissonClient redissonClient) {
		super(redissonClient);
	}

	@Override
	public void onMessage0(String message, String tags) {
		WsMsgModel wsMsgModel = JsonUtil.jsonToObj(message, WsMsgModel.class);
		WsTools.sendBatchMessage(wsMsgModel.getMessage(), wsMsgModel.getTo());
	}

}
