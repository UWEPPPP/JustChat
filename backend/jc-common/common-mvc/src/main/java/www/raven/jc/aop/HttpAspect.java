package www.raven.jc.aop;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * logging aspect
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Aspect
@Component
@Slf4j
public class HttpAspect {

	/**
	 * 定义切点表达式,指定通知功能被应用的范围
	 */

	@Pointcut("execution(public * www.raven.jc.controller.*.*(..))")
	public void webLog() {
	}

	/**
	 * 通知包裹了目标方法，在目标方法调用之前和之后执行自定义的行为 ProceedingJoinPoint切入点可以获取切入点方法上的名字、参数、注解和对象
	 **/
	@Around("webLog()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		//获取当前请求对象
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		assert attributes != null;
		HttpServletRequest request = attributes.getRequest();
		log.info("----HTTP 收到请求 url:{},调用者:{}", request.getRequestURL().toString(),
				request.getRemoteUser());
		Object result = joinPoint.proceed();
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		long endTime = System.currentTimeMillis();
		log.info("----HTTP 被调用方法 method:{} ,请求参数 parameter:{}", request.getMethod(),
				getParameter(method, joinPoint.getArgs()));
		log.info("----HTTP 返回值:{} --总耗时:{}毫秒", result, (int) (endTime - startTime));
		return result;
	}

	/**
	 * 根据方法和传入的参数获取请求参数
	 */
	private Object getParameter(Method method, Object[] args) {
		List<Object> argList = new ArrayList<>();
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			//将RequestBody注解修饰的参数作为请求参数
			RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
			if (requestBody != null) {
				argList.add(args[i]);
			}
			//将RequestParam注解修饰的参数作为请求参数
			RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
			if (requestParam != null) {
				Map<String, Object> map = new HashMap<>();
				String key = parameters[i].getName();
				if (!StrUtil.isEmpty(requestParam.value())) {
					key = requestParam.value();
				}
				map.put(key, args[i]);
				argList.add(map);
			}
		}
		if (argList.isEmpty()) {
			return null;
		} else if (argList.size() == 1) {
			return argList.getFirst();
		} else {
			return argList;
		}
	}

}