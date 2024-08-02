package www.raven.jc.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis pro config
 *
 * @author 刘家辉
 * @date 2023/11/24
 */
@Configuration
public class MybatisProConfig {

  /**
   * mybatis plus interceptor 分页插件
   *
   * @return {@link MybatisPlusInterceptor}
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
    return interceptor;
  }

}
