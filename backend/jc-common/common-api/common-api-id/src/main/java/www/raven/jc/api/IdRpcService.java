package www.raven.jc.api;

import www.raven.jc.result.RpcResult;

/**
 * account api
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface IdRpcService {
	/**
	 * get id
	 *
	 * @return {@link RpcResult }<{@link Long }>
	 */
	RpcResult<String> getId();
}