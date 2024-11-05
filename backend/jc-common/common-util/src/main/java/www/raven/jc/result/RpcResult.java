package www.raven.jc.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import www.raven.jc.serializable.CommonSerializable;

/**
 * api result
 *
 * @author 刘家辉
 * @date 2024/01/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcResult<T> extends CommonSerializable {

	private boolean isSuccess;
	private String message;
	private T data;

	private RpcResult(boolean isSuccess, String message, T data) {
		this.isSuccess = isSuccess;
		this.message = message;
		this.data = data;
	}

	private RpcResult(boolean isSuccess, String message) {
		this.isSuccess = isSuccess;
		this.message = message;
	}

	public static <T> RpcResult<T> operateFailure(String message) {
		return new RpcResult<>(false, message);
	}

	public static <T> RpcResult<T> operateSuccess(String message) {
		return new RpcResult<>(true, message);
	}

	public static <T> RpcResult<T> operateSuccess(String message, T data) {
		return new RpcResult<>(true, message, data);
	}

}
