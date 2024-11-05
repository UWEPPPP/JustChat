package www.raven.jc.filter;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.config.SecurityProperty;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.result.HttpResult;
import www.raven.jc.result.ResultCode;
import www.raven.jc.util.JsonUtil;

/**
 * request filter
 *
 * @author 刘家辉
 * @date 2023/12/01
 */
@Slf4j
@Component
public class RequestFilter implements GlobalFilter, Ordered {

	@Autowired
	private SecurityProperty securityProperty;

	@Override
	public int getOrder() {
		// -2 is response filter, request filter should be called before that
		return -3;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange,
	                         GatewayFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		log.info("RequestFilter执行");
		log.info("收到一次请求");
		log.info("Request Method: {}", request.getMethod());
		log.info("Request URI: {}", request.getURI());
		log.info("Request Headers: {}", request.getHeaders());
		log.info("Request Query Params: {}", request.getQueryParams());
		for (String path : securityProperty.getWordsArray()) {
			if (request.getURI().getPath().contains(path)) {
				return chain.filter(exchange);
			}
		}
		long time = Long.parseLong(
				Objects.requireNonNull(request.getHeaders().get(JwtConstant.TIME)).getFirst());
		if (time < System.currentTimeMillis()) {
			// 获取响应对象
			ServerHttpResponse response = exchange.getResponse();
			// 设置响应的状态码和内容类型
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			HttpResult<Object> result = HttpResult.operateFailure(ResultCode.TOKEN_EXPIRED_CODE,
					"Token expired, please reapply for a token");
			// 返回响应
			String responseBody = JsonUtil.objToJson(result);
			// 返回响应
			return response.writeWith(Mono.just(responseBody)
					.map(str -> response.bufferFactory().wrap(str.getBytes(StandardCharsets.UTF_8))));
		}

		return chain.filter(exchange);
	}
}
