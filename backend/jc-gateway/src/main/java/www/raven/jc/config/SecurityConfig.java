package www.raven.jc.config;

import java.util.Collections;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import www.raven.jc.handler.DefaultAccessDeniedHandler;
import www.raven.jc.handler.DefaultAuthenticationEntryPoint;
import www.raven.jc.override.DefaultSecurityContextRepository;
import www.raven.jc.override.TokenAuthenticationManager;

/**
 * security config
 *
 * @author 刘家辉
 * @date 2023/11/26
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Autowired
	private TokenAuthenticationManager tokenAuthenticationManager;
	@Autowired
	private DefaultAccessDeniedHandler defaultAccessDeniedHandler;
	@Autowired
	private DefaultAuthenticationEntryPoint authenticationEntryPoint;
	@Autowired
	private DefaultSecurityContextRepository defaultSecurityContextRepository;
	@Autowired
	private SecurityProperty securityProperty;

	@Bean
	@SuppressWarnings("all")
	public SecurityWebFilterChain springSecurityFilterChain(
			ServerHttpSecurity http) {
		http.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
				.authenticationManager(reactiveAuthenticationManager())
				.securityContextRepository(defaultSecurityContextRepository)
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers(securityProperty.auth).permitAll()
						.pathMatchers(securityProperty.users)
						.hasAnyRole(securityProperty.roleUser, securityProperty.roleAdmin)
						.pathMatchers(securityProperty.admins).hasRole(securityProperty.roleAdmin)
						.anyExchange().authenticated()
				)
				.formLogin()
				.and()
				.exceptionHandling()
				.accessDeniedHandler(defaultAccessDeniedHandler)
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
		;
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		//1.允许任何来源
		corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
		//2.允许任何请求头
		corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
		//3.允许任何方法
		corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
		//4.允许凭证
		corsConfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	ReactiveAuthenticationManager reactiveAuthenticationManager() {
		LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
		managers.add(authentication -> {
			// 其他登陆方式 (比如手机号验证码登陆) 可在此设置不得抛出异常或者 Mono.error
			return Mono.empty();
		});
		managers.add(tokenAuthenticationManager);
		return new DelegatingReactiveAuthenticationManager(managers);
	}

}
