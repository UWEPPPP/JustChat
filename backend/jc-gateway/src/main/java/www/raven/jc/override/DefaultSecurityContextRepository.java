package www.raven.jc.override;

import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.config.JwtProperty;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JwtUtil;

/**
 * default security context repository
 * <p>
 * 存储认证授权的相关信息
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@Component
@Slf4j
public class DefaultSecurityContextRepository implements ServerSecurityContextRepository {

	@Autowired
	private JwtProperty jwtProperty;
	@Resource
	private TokenAuthenticationManager tokenAuthenticationManager;
	@Autowired
	private RedissonClient redissonClient;

	@Override
	public Mono<Void> save(ServerWebExchange exchange,
	                       SecurityContext context) {
		return Mono.empty();
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		List<String> tokens = request.getHeaders().get(JwtConstant.TOKEN);
		if (tokens == null || tokens.isEmpty()) {
			return Mono.empty();
		}
		TokenDTO dto = JwtUtil.parseToken(tokens.getFirst(), jwtProperty.key);
		log.info("访问者信息 {} {} {}", dto.getUserId(), dto.getRole(), dto.getExpireTime());
		Assert.isTrue(Objects.equals(tokens.getFirst(),
				redissonClient.getBucket(JwtConstant.TOKEN + dto.getUserId()).get()), "未登录");
		request.mutate().header(JwtConstant.TIME, String.valueOf(dto.getExpireTime()))
				.header(JwtConstant.USER_ID, dto.getUserId().toString()).build();
		Authentication auth = new UsernamePasswordAuthenticationToken(dto.getUserId(), null,
				AuthorityUtils.createAuthorityList(dto.getRole().toArray(new String[0])));
		return tokenAuthenticationManager.authenticate(
				auth
		).map(SecurityContextImpl::new);
	}
}
