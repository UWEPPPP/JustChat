package www.raven.jc.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * common result
 *
 * @author 刘家辉
 * @date 2023/11/21
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class HttpResult<T> {

  boolean isSuccess;
  private Integer code;
  private String message;
  private T data;

  private HttpResult(Integer code, boolean isSuccess, String message) {
    this.code = code;
    this.isSuccess = isSuccess;
    this.message = message;
  }

  private HttpResult(Integer code, boolean isSuccess, String message,
      T data) {
    this.code = code;
    this.isSuccess = isSuccess;
    this.message = message;
    this.data = data;
  }

  public static HttpResult<Void> operateFailure(String message) {
    return new HttpResult<>(ResultCode.FAIL_CODE, false, message);
  }

  public static HttpResult<Void> operateSuccess(String message) {
    return new HttpResult<>(ResultCode.SUCCESS_CODE, true, message);
  }

  public static <T> HttpResult<T> operateSuccess(String message, T data) {
    return new HttpResult<>(
        ResultCode.SUCCESS_CODE,
        true,
        message,
        data
    );
  }

  public static <T> HttpResult<T> operateFailure(String message, T data) {
    return new HttpResult<>(
        ResultCode.FAIL_CODE,
        false,
        message,
        data
    );
  }

  public static <T> HttpResult<T> operateFailure(Integer code,
      String message) {
    return new HttpResult<>(
        code,
        false,
        message
    );
  }

  public static <T> HttpResult<T> operateFailure(Integer code,
      String message, T data) {
    return new HttpResult<>(
        code,
        false,
        message,
        data
    );
  }
}
