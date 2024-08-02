package www.raven.jc.service;

import java.util.List;
import org.redisson.api.RScoredSortedSet;
import org.springframework.scheduling.annotation.Async;
import www.raven.jc.entity.po.Moment;

/**
 * async service
 *
 * @author 刘家辉
 * @date 2024/02/21
 */
public interface TimelineFeedService {

  /**
   * add moment cache
   *
   * @param userId   user id
   * @param capacity capacity
   * @param userIds  user ids
   */
  @Async
  void buildMomentTimelineFeeding(Long capacity, List<Integer> userIds,
      Integer userId);

  /**
   * handle moment event
   *
   * @param userId user id
   * @param moment moment
   */
  @Async
  void insertMomentFeed(Integer userId, Moment moment);

  /**
   * get moment timeline feeding
   *
   * @param userId user id
   * @return {@link RScoredSortedSet}<{@link String}>
   */
  RScoredSortedSet<String> getMomentTimelineFeeding(Integer userId);
}
