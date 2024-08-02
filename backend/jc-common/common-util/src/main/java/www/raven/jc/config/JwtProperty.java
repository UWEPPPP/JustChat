package www.raven.jc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * jwt property
 *
 * @author 刘家辉
 * @date 2024/02/18
 */
@Configuration
public class JwtProperty {

  @Value("${jwt.key}")
  public String key;

  /**
   * 默认7天 expire time
   */
  @Value("${jwt.expire}")
  public Long expireTime;
}
