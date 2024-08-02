package www.raven.jc.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis config
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Configuration
public class
RedissonConfiguration {

  @Value("${spring.redis.address}")
  private String address;

  @Value("${spring.redis.password:}")
  private String password;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    SingleServerConfig singleServerConfig = config.useSingleServer();
    singleServerConfig.setPassword(password).setAddress(address);
    return Redisson.create(config);
  }

}


