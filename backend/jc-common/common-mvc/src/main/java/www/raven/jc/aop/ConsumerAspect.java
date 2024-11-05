package www.raven.jc.aop;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * consumer aspect
 *
 * @author 刘家辉
 * @date 2024/02/06
 */
@Aspect
@Component
@Slf4j
public class ConsumerAspect {

	@Pointcut("execution(* www.raven.jc.event..*.*(..))")
	public void pointcut() {

	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		// 获取方法的全名，包括类名和方法名
		String methodFullName = joinPoint.getSignature().toLongString();
		// 获取方法的参数
		Object[] args = joinPoint.getArgs();
		// 打印出方法的全名和参数的详细信息
		log.info("----RocketMq 消费者收到消息 :  接收方法全名: {}, 参数: {}", methodFullName,
				Arrays.toString(args));
		// 记录方法开始执行的时间
		long startTime = System.currentTimeMillis();
		// 执行方法
		Object result = joinPoint.proceed();
		// 记录方法结束执行的时间
		long endTime = System.currentTimeMillis();
		// 计算方法执行的时间
		long executeTime = endTime - startTime;
		// 打印出方法的全名、结果和执行时间
		log.info("----RocketMq完成消费 : 方法全名: {}, 结果: {}, 执行时间: {} 毫秒", methodFullName,
				result, executeTime);
		return result;

	}
}
