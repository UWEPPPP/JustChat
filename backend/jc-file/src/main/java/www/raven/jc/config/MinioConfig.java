package www.raven.jc.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import www.raven.jc.client.MyMinioClient;

@Configuration
@Slf4j
@RefreshScope
public class MinioConfig {

  @Value("${minio.access-key}")
  private String accessKey;
  @Value("${minio.secret-key}")
  private String secretKey;
  @Value("${minio.domain}")
  private String domain;
  @Value("${minio.expose-domain}")
  private String exposeDomain;
  @Value("${minio.bucket}")
  private String bucket;

  @Bean
  public MyMinioClient myMinioClient() {
    MinioClient minioClient =
        MinioClient.builder()
            .endpoint(domain)
            .credentials(accessKey, secretKey)
            .build();
    try {
      if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }
      log.info("minio初始化成功");
    } catch (Exception e) {
      log.error("minio初始化失败:{}", e.getMessage());
    }
    return new MyMinioClient(minioClient, domain, bucket, exposeDomain);
  }
}
