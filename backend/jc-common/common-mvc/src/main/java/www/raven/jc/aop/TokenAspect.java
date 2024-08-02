package www.raven.jc.aop;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import www.raven.jc.config.JwtProperty;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JwtUtil;

/**
 * token aspect
 *
 * @author 刘家辉
 * @date 2024/01/19
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class TokenAspect {

  @Autowired
  private HttpServletRequest request;
  @Autowired
  private JwtProperty jwtProperty;

  @Pointcut("@annotation(www.raven.jc.annotions.Auth)")
  public void pointcut() {
  }

  @Before("pointcut() && @annotation(auth)")
  public void before(www.raven.jc.annotions.Auth auth) {
    log.info("----Token收到访问级接口 级别:{}", auth.value());
    String token = request.getHeader(JwtConstant.TOKEN);
    if (StrUtil.isEmpty(token)) {
      throw new RuntimeException("权限不足");
    }
    TokenDTO dto = JwtUtil.parseToken(token, jwtProperty.key);
    List<String> role = dto.getRole();
    if (!role.contains(auth.value())) {
      throw new RuntimeException("权限不足");
    }
  }
}
