package www.raven.jc;

import org.apache.dubbo.config.annotation.DubboService;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import www.raven.jc.api.IdRpcService;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.result.RpcResult;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * id rpc service impl
 *
 * @author Rawven
 * @date 2024/11/05
 */
@DubboService(interfaceClass = UserRpcService.class, version = "1.0.0", timeout = 15000)
@Slf4j
public class IdRpcServiceImpl implements IdRpcService {

	private long workerId;

	@Override
	public RpcResult<String> getId() {

		Snowflake snowflake = IdUtil.getSnowflake(getWorkerId());
		return RpcResult.operateSuccess("success", snowflake.nextIdStr());
	}

	public long getWorkerId() {
		if (workerId != 0) {
			return workerId;
		}
		// 根据 IP 地址生成 workerId（需保证唯一性）
		String hostAddress;
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
			// 取低 5 位作为 workerId
			workerId = hostAddress.hashCode() & 0x1F;
		} catch (UnknownHostException e) {
			log.error("获取 IP 地址失败", e);
			workerId = 0;
		}
		return workerId;
	}
}
