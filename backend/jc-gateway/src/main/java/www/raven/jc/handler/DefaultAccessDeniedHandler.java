package www.raven.jc.handler;

import java.nio.charset.Charset;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.result.HttpResult;
import www.raven.jc.util.JsonUtil;

/**
 * default access denied handler
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@Component
public class DefaultAccessDeniedHandler implements ServerAccessDeniedHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange,
	                         AccessDeniedException denied) {
		return Mono.defer(() -> Mono.just(exchange.getResponse()))
				.flatMap(response -> {
					response.setStatusCode(HttpStatus.OK);
					response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
					DataBufferFactory dataBufferFactory = response.bufferFactory();
					String result = JsonUtil.objToJson(HttpResult.operateFailure("您权限不足"));
					DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(
							Charset.defaultCharset()));
					return response.writeWith(Mono.just(buffer));
				});
	}
}
