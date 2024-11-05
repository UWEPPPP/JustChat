package www.raven.jc.service.impl;

import static www.raven.jc.constant.TimelineFeedConstant.PREFIX;

import cn.hutool.core.lang.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.config.ScoredSortedSetProperty;
import www.raven.jc.constant.TimelineFeedConstant;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.TimelineFeedService;

/**
 * async service 朋友圈是feed
 *
 * @author 刘家辉
 * @date 2024/02/18
 */
@Service
@Slf4j
public class TimelineFeedServiceImpl implements TimelineFeedService {

	@Autowired
	private ScoredSortedSetProperty setProperty;
	@Autowired
	private UserRpcService userRpcService;
	@Autowired
	private MomentDAO momentDAO;
	@Autowired
	private RedissonClient redissonClient;

	/**
	 * add moment timeline feeding 存储用户时间线（仅id）
	 *
	 * @param userId  user id
	 * @param userIds user ids
	 */
	@Override
	public void buildMomentTimelineFeeding(Long capacity, List<Integer> userIds,
	                                       Integer userId) {
		List<Moment> moments = momentDAO.getBaseMapper().selectList(
				momentDAO.lambdaQuery().in(Moment::getUserId, userIds).orderByDesc(Moment::getTimestamp).
						last("limit " + calculateNeedCapacity(capacity)
						));
		RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(
				TimelineFeedConstant.PREFIX + userId);
		scoredSortedSet.expire(Duration.ofDays(setProperty.expireDays));
		Map<String, Double> scores = moments.stream()
				.collect(Collectors.toMap(Moment::getId, moment -> moment.getTimestamp().doubleValue()));
		scoredSortedSet.addAll(scores);
	}

	@Override
	public void insertMomentFeed(Integer userId, Moment moment) {
		List<RScoredSortedSet<Object>> list = getHisFriendMomentCache(userId);
		list.forEach(scoredSortedSet -> {
			if (scoredSortedSet.size() > setProperty.maxSize) {
				scoredSortedSet.pollFirst();
			}
			scoredSortedSet.add(moment.getTimestamp(), moment.getId());
		});
	}

	@Override
	public RScoredSortedSet<String> getMomentTimelineFeeding(Integer userId) {
		RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(
				TimelineFeedConstant.PREFIX + userId);
		if (scoredSortedSet.isExists()) {
			//refresh expire time
			scoredSortedSet.expire(Duration.ofDays(setProperty.expireDays));
			return scoredSortedSet;
		}
		return null;
	}

	private Long calculateNeedCapacity(Long capacity) {
		return (long) Math.ceil(capacity * 1.0 / setProperty.maxSize) * setProperty.maxSize;
	}

	/**
	 * get his friend moment cache 获取自己以及好友的朋友圈缓存
	 *
	 * @param userId user id
	 * @return {@link List}<{@link RScoredSortedSet}<{@link Object}>>
	 */
	private List<RScoredSortedSet<Object>> getHisFriendMomentCache(
			Integer userId) {
		RpcResult<List<UserInfoDTO>> friendAndMeInfos = userRpcService.getFriendAndMeInfos(userId);
		Assert.isTrue(friendAndMeInfos.isSuccess(), "获取好友信息失败");
		List<Integer> collect = friendAndMeInfos.getData().stream().map(UserInfoDTO::getUserId)
				.toList();
		return collect.stream().map(integer -> redissonClient.getScoredSortedSet(PREFIX + integer))
				.collect(Collectors.toList());
	}
}
