package www.raven.jc;

import org.apache.dubbo.config.annotation.DubboService;

import cn.hutool.core.util.IdUtil;
import www.raven.jc.api.IdRpcService;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.result.RpcResult;

/**
 * id rpc service impl
 *
 * @author Rawven
 * @date 2024/11/05
 */
@DubboService(interfaceClass = UserRpcService.class, version = "1.0.0", timeout = 15000)
public class IdRpcServiceImpl implements IdRpcService {
	@Override
	public RpcResult<String> getId() {
		return RpcResult.operateSuccess("success", IdUtil.getSnowflakeNextIdStr());
	}
}
