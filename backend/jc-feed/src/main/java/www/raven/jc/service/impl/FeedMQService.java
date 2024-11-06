package www.raven.jc.service.impl;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.event.model.MomentNoticeEvent;
import www.raven.jc.util.MqUtil;

/**
 * event service
 *
 * @author Rawven
 * @date 2024/11/06
 */
@Service
public class FeedMQService {
	public static final String LIKE = "like";
	public static final String COMMENT = "Comment";

	@Autowired
	private RocketMQTemplate rocketMQTemplate;
	@Value("${mq.out_topic}")
	private String outTopic;
	@Value("${mq.in_topic}")
	private String inTopic;

	public void handelAsyncSaveLikeEvent(Like like) {
		MqUtil.sendMsg(rocketMQTemplate, inTopic, LIKE, like);
	}

	public void handleAsyncSaveCommentEvent(Comment comment) {
		MqUtil.sendMsg(rocketMQTemplate, inTopic, COMMENT,
				comment);
	}

	public void handleNotifyEvent(String momentId, Integer userId, String msg,
	                              String tag) {
		MqUtil.sendMsg(rocketMQTemplate, outTopic, tag,
				new MomentNoticeEvent().setMomentId(momentId).setUserId(userId).setMsg(msg));
	}
}
