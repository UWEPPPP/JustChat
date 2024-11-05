package www.raven.jc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * scored sorted set property
 *
 * @author 刘家辉
 * @date 2024/02/21
 */
@Configuration
public class ScoredSortedSetProperty {

	@Value("${redis.sorted-set.max-size}")
	public Integer maxSize;
	@Value("${redis.sorted-set.expire-days}")
	public Integer expireDays;
}
