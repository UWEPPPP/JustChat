package www.raven.jc;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * social application
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@SpringBootApplication
@EnableDubbo
@EnableAsync
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@MapperScan("www.raven.jc.dao")
public class FeedApplication {

  public static void main(String[] args) {
    SpringApplication.run(FeedApplication.class, args);
  }
}
