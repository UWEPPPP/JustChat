package www.raven.jc.consumer;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.ImUserMqConstant;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.event.MomentNoticeEvent;
import www.raven.jc.entity.event.RoomApplyEvent;
import www.raven.jc.entity.po.Notice;
import www.raven.jc.event.model.DeleteNoticeEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.template.AbstractMqListener;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.ws.WsTools;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * message consumer
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "${mq.in_consumer_group}", topic = "${mq.in_topic}", messageModel = MessageModel.CLUSTERING)
public class NoticeEventListener extends AbstractMqListener {

	@Autowired
	private RedissonClient redissonClient;
	@Autowired
	private UserRpcService userRpcService;
	@Autowired
	private NoticeDAO noticeDAO;
	@Autowired
	private MessageDAO messageDAO;
	@Autowired
	private RoomDAO roomDAO;
	@Autowired
	private FriendChatDAO friendChatDAO;

	@Autowired
	public NoticeEventListener(RedissonClient redissonClient) {
		super(redissonClient);
	}

	@Override
	public void onMessage0(String jsonMessage, String tag) {
		switch (tag) {
			case ImImMqConstant.TAGS_CHAT_ROOM_APPLY:
				eventUserJoinRoomApply(jsonMessage);
				break;
			case SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND:
				eventMomentNoticeFriendEvent(jsonMessage);
				break;
			case SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT:
				eventMomentNoticeLikeOrCommentEvent(jsonMessage);
				break;
			case ImUserMqConstant.TAGS_DELETE_NOTICE:
				eventDeleteNotice(jsonMessage);
				break;
			default:
				log.info("--RocketMq 非法的消息，不处理");
		}
	}

	private void eventMomentNoticeFriendEvent(String msg) {
		MomentNoticeEvent payload = parseMessage(msg, MomentNoticeEvent.class);
		Integer userId = payload.getUserId();
		RpcResult<List<UserInfoDTO>> friendInfos = userRpcService.getFriendInfos(userId);
		Assert.isTrue(friendInfos.isSuccess(), "获取好友列表失败");
		HashMap<Object, Object> map = new HashMap<>(3);
		map.put("momentId", payload.getMomentId());
		map.put("msg", payload.getMsg());
		map.put("type", SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND);
		List<Integer> idsFriend = friendInfos.getData().stream().map(UserInfoDTO::getUserId)
				.collect(Collectors.toList());
		WsTools.sendBatchMessage(JsonUtil.objToJson(map), idsFriend);
	}

	private void eventMomentNoticeLikeOrCommentEvent(String msg) {
		MomentNoticeEvent payload = parseMessage(msg, MomentNoticeEvent.class);
		Integer userId = payload.getUserId();
		HashMap<Object, Object> map = new HashMap<>(3);
		map.put("momentId", payload.getMomentId());
		map.put("msg", payload.getMsg());
		map.put("type", SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT);
		WsTools.sendOneMessage(userId, JsonUtil.objToJson(map));
	}

	/**
	 * 通知用户有人想要入群
	 */
	public void eventUserJoinRoomApply(String msg) {
		RoomApplyEvent payload = parseMessage(msg, RoomApplyEvent.class);
		Integer founderId = payload.getFounderId();
		Notice notice = new Notice().setUserId(founderId)
				.setData(String.valueOf(payload.getRoomId()))
				.setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
				.setTimestamp(System.currentTimeMillis())
				.setSenderId(payload.getApplyId());
		Assert.isTrue(noticeDAO.save(notice), "保存通知失败");
		RBucket<String> founderBucket = redissonClient.getBucket(JwtConstant.TOKEN + founderId);
		if (founderBucket.isExists()) {
			HashMap<Object, Object> map = new HashMap<>(1);
			map.put("type", ImImMqConstant.TAGS_CHAT_ROOM_APPLY);
			WsTools.sendOneMessage(founderId, JsonUtil.objToJson(map));
			log.info("--RocketMq 已推送通知给founder");
		} else {
			log.info("--RocketMq founder不在线");
		}
	}

	private void eventDeleteNotice(String msg) {
		DeleteNoticeEvent event = parseMessage(msg, DeleteNoticeEvent.class);
		Assert.isTrue(noticeDAO.removeById(event.getNoticeId()), "删除失败");
	}

}
