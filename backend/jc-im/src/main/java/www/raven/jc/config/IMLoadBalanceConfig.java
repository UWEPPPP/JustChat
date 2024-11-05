package www.raven.jc.config;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * imload balance config
 *
 * @author Rawven
 * @date 2024/11/05
 */
public class IMLoadBalanceConfig implements LoadBalance {
	public static final String ID_HASH = "ID_HASH";
	private final Random random = new Random();

	@Override
	public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
		String hash = RpcContext.getClientAttachment().getAttachment(ID_HASH);

		// 判断 hash 是否存在并非空
		if (hash != null && !hash.isEmpty()) {
			// 基于 Hash 计算目标实例索引
			int targetIndex = Math.abs(hash.hashCode() % invokers.size());
			return invokers.get(targetIndex);
		}

		// 当 hash 为空时，随机选择一个实例
		return invokers.get(random.nextInt(invokers.size()));
	}
}
