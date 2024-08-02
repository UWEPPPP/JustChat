package www.raven.jc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * file system application
 *
 * @author 刘家辉
 * @date 2024/01/30
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class FileSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(FileSystemApplication.class, args);
  }
}
