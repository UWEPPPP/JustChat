package www.raven.jc.config;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import www.raven.jc.api.IdRpcService;

/**
 * dubbo config
 *
 * @author 刘家辉
 * @date 2024/02/06
 */
@Configuration
public class UserDubboConfig {

	@Bean
	@DubboReference(interfaceClass = IdRpcService.class, version = "1.0.0", timeout = 15000)
	public ReferenceBean<IdRpcService> idRpcService() {
		return new ReferenceBean<>();
	}
}
