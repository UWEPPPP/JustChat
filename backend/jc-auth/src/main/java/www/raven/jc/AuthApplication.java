package www.raven.jc;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * auth application
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@EnableAspectJAutoProxy

public class AuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }
}
