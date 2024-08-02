package www.raven.jc.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.shaded.com.google.common.base.Joiner;
import com.alibaba.nacos.shaded.com.google.common.base.Throwables;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import www.raven.jc.util.JsonUtil;

/**
 * response filter
 *
 * @author 刘家辉
 * @date 2023/2/3 - 10:54 返回参数日志打印
 */

@Component
@Slf4j
public class ResponseFilter implements GlobalFilter, Ordered {

  private static final Joiner JOINER = Joiner.on("");
  private static final String CONTENT_TYPE = "application/json";

  public static void logResponse(String str) {
    StringBuilder sb = new StringBuilder();
    int index = str.indexOf("\"data\"");
    String realStr = str.substring(0, index - 1);
    sb.append(realStr).append("}");
    Map<String, Object> commonResult = JsonUtil.jsonToObj(sb.toString(), Map.class);
    log.info("该请求的返回");
    log.info("Response Code: {}", commonResult.get("code"));
    log.info("Response Message: {}", commonResult.get("message"));
  }

  @Override
  public int getOrder() {
    // -1 is response write filter, must be called before that
    return -2;
  }

  @Override
  @SuppressWarnings("all")
  public Mono<Void> filter(ServerWebExchange exchange,
      GatewayFilterChain chain) {
    ServerHttpResponse originalResponse = exchange.getResponse();
    DataBufferFactory bufferFactory = originalResponse.bufferFactory();
    ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
      @Override
      public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (Objects.requireNonNull(getStatusCode()).equals(HttpStatus.OK) && body instanceof Flux) {
          // 获取ContentType，判断是否返回JSON格式数据
          String originalResponseContentType = exchange.getAttribute(
              ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
          if (StringUtils.isNotBlank(originalResponseContentType)
              && originalResponseContentType.contains(CONTENT_TYPE)) {
            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
            //（返回数据内如果字符串过大，默认会切割）解决返回体分段传输
            return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
              List<String> list = Lists.newArrayList();
              dataBuffers.forEach(dataBuffer -> {
                try {
                  byte[] content = new byte[dataBuffer.readableByteCount()];
                  dataBuffer.read(content);
                  DataBufferUtils.release(dataBuffer);
                  list.add(new String(content, StandardCharsets.UTF_8));
                } catch (Exception e) {
                  log.info("加载Response字节流异常，失败原因：{}",
                      Throwables.getStackTraceAsString(e));
                }
              });
              logResponse(list.getFirst());
              String responseData = JOINER.join(list);
              byte[] uppedContent = new String(responseData.getBytes(),
                  StandardCharsets.UTF_8).getBytes();
              originalResponse.getHeaders().setContentLength(uppedContent.length);
              return bufferFactory.wrap(uppedContent);
            }));
          }
        }
        return super.writeWith(body);
      }

      @Override
      public Mono<Void> writeAndFlushWith(
          Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(p -> p));
      }
    };
    return chain.filter(exchange.mutate().response(response).build());
  }

}
