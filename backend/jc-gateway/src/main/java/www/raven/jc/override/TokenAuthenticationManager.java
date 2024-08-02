package www.raven.jc.override;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * token authentication manager token 认证处理
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@Component
@Primary
public class TokenAuthenticationManager implements ReactiveAuthenticationManager {

  @Override
  @SuppressWarnings("unchecked")
  public Mono<Authentication> authenticate(Authentication authentication) {
    return Mono.just(authentication);
  }
}
