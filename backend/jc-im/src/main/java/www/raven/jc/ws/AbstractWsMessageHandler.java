package www.raven.jc.ws;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import www.raven.jc.api.IdRpcService;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.config.ImProperty;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.dao.MessageReadAckDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.model.WsMsgModel;
import www.raven.jc.service.MessageService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * abstract ws message handler
 *
 * @author Rawven
 * @date 2024/11/05
 */
public abstract class AbstractWsMessageHandler implements WsMessageHandler {
	@Resource
	protected IdRpcService idRpcService;
	@Resource
	protected MessageService messageService;
	@Resource
	protected UserRpcService userRpcService;
	@Resource
	protected RocketMQTemplate rocketMQTemplate;
	@Resource
	protected RedissonClient redissonClient;
	@Resource
	protected UserRoomDAO userRoomDAO;
	@Resource
	protected ImProperty imProperty;
	@Resource
	protected MessageReadAckDAO messageReadAckDAO;

	/**
	 * 广播消息(多实例间)
	 */
	public void broadcast(RedissonClient redissonClient, List<Integer> userIds,
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
					new WsMsgModel().setMessage(JsonUtil.objToJson(message)).setTo(theTopicIds));
		}
	}
}
