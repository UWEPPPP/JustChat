package www.raven.jc.consumer;

import jakarta.annotation.Resource;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import www.raven.jc.dao.CommentDAO;
import www.raven.jc.dao.LikeDAO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.template.AbstractMqListener;
import www.raven.jc.util.JsonUtil;

import java.util.List;

import static www.raven.jc.service.impl.FeedMQService.COMMENT;
import static www.raven.jc.service.impl.FeedMQService.LIKE;

/**
 * save consumer
 *
 * @author Rawven
 * @date 2024/11/06
 */
@Component
public class FeedSaveDataConsumer extends AbstractMqListener {

	private static final int LIKE_BATCH_LIMIT = 100;
	private static final int COMMENT_BATCH_LIMIT = 100;
	private static final String LIKE_REDIS_LIST_LOCK_KEY = "likeQueue";
	private static final String COMMENT_REDIS_LIST_LOCK_KEY = "commentQueue";

	@Resource
	private LikeDAO likeDAO;
	@Resource
	private CommentDAO commentDAO;
	@Resource
	private RedissonClient redissonClient;

	public FeedSaveDataConsumer(@Autowired RedissonClient redissonClient) {
		super(redissonClient);
	}

	@Override
	public void onMessage0(String jsonMessage, String tag) {
		switch (tag) {
			case LIKE:
				saveLike(jsonMessage);
				break;
			case COMMENT:
				saveComment(jsonMessage);
				break;
			default:
				break;
		}
	}

	/**
	 * 保存点赞数据
	 *
	 * @param jsonMessage 点赞消息内容
	 */
	private void saveLike(String jsonMessage) {
		Like like = JsonUtil.jsonToObj(jsonMessage, Like.class);

		// 将点赞对象添加到 Redis 的列表中
		RList<Like> likeList = redissonClient.getList(LIKE_REDIS_LIST_LOCK_KEY);
		likeList.add(like);

		// 尝试进行批量保存
		trySaveBatchLike();
	}

	@Scheduled(cron = "0 0/1 * * * ?")
	private void trySaveBatchLike() {
		// 获取 Redis 列表
		RList<Like> likeList = redissonClient.getList(LIKE_REDIS_LIST_LOCK_KEY);

		// 如果列表大小超过了限制值，进行批量保存
		if (likeList.size() >= LIKE_BATCH_LIMIT) {
			// 获取分布式锁，确保只有一个实例能执行批量保存
			RLock lock = redissonClient.getLock("likeSaveLock");
			try {
				// 尝试获取锁
				if (lock.tryLock()) {
					// 批量保存到数据库
					List<Like> likesToSave = likeList.readAll();
					likeDAO.saveBatch(likesToSave);

					// 根据已保存数据的下标删除 Redis 中的已保存元素
					for (int i = 0; i < likesToSave.size(); i++) {
						likeList.removeFirst();
					}
				}
			} finally {
				lock.unlock(); // 释放锁
			}
		}
	}

	/**
	 * 保存评论数据
	 *
	 * @param jsonMessage 评论消息内容
	 */
	private void saveComment(String jsonMessage) {
		Comment comment = JsonUtil.jsonToObj(jsonMessage, Comment.class);

		// 将评论对象添加到 Redis 的列表中
		RList<Comment> commentList = redissonClient.getList(COMMENT_REDIS_LIST_LOCK_KEY);
		commentList.add(comment);

		// 尝试进行批量保存
		trySaveBatchComment();
	}

	@Scheduled(cron = "0 0/1 * * * ?")
	private void trySaveBatchComment() {
		// 获取 Redis 列表
		RList<Comment> commentList = redissonClient.getList(COMMENT_REDIS_LIST_LOCK_KEY);

		// 如果列表大小超过了限制值，进行批量保存
		if (commentList.size() >= COMMENT_BATCH_LIMIT) {
			// 获取分布式锁，确保只有一个实例能执行批量保存
			RLock lock = redissonClient.getLock("commentSaveLock");
			try {
				// 尝试获取锁
				if (lock.tryLock()) {
					// 批量保存到数据库
					List<Comment> commentsToSave = commentList.readAll();
					commentDAO.saveBatch(commentsToSave);

					// 根据已保存数据的下标删除 Redis 中的已保存元素
					for (int i = 0; i < commentsToSave.size(); i++) {
						commentList.removeFirst();
					}
				}
			} finally {
				lock.unlock(); // 释放锁
			}
		}
	}
}
