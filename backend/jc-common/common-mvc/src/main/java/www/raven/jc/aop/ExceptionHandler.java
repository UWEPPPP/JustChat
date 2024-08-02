package www.raven.jc.aop;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import www.raven.jc.result.HttpResult;

/**
 * @author 刘家辉
 * @date 2023/10/29
 */
@ControllerAdvice
@Component
@Slf4j
public class ExceptionHandler {

  /**
   * bind exception handler 处理 form data方式调用接口校验失败抛出的异常
   *
   * @param e e
   * @return {@link HttpResult}<{@link Void}>
   */
  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
  public HttpResult<Void> bindExceptionHandler(BindException e) {
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    List<String> collect = fieldErrors.stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());
    return HttpResult.operateFailure("请求参数异常：" + String.join(",", collect));
  }

  /**
   * method argument not valid exception handler
   * <2> 处理 json 请求体调用接口校验失败抛出的异常
   *
   * @param e e
   * @return {@link HttpResult}<{@link Void}>
   */
  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
  public HttpResult<Void> methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException e) {
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    List<String> collect = fieldErrors.stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());
    return HttpResult.operateFailure("请求参数异常：" + String.join(",", collect));
  }

  /**
   * constraint violation exception handler
   * <3> 处理单个参数校验失败抛出的异常
   *
   * @param e e
   * @return {@link HttpResult}<{@link Void}>
   */
  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
  public HttpResult<Void> constraintViolationExceptionHandler(
      ConstraintViolationException e) {
    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    List<String> collect = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    return HttpResult.operateFailure("请求参数异常：" + String.join(",", collect));
  }

  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(NullPointerException.class)
  public HttpResult<Void> handlerNullPointerException(
      NullPointerException ex) {
    log.error("空指针异常", ex);
    return HttpResult.operateFailure(ex.getMessage());
  }

  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
  public HttpResult<Void> handlerIllegalArgumentException(
      IllegalArgumentException ex) {
    return HttpResult.operateFailure(ex.getLocalizedMessage());
  }

  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
  public HttpResult<Void> handlerRuntimeException(RuntimeException ex) {
    log.error("运行时异常", ex);
    return HttpResult.operateFailure(ex.getMessage());
  }

  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
  public HttpResult<Void> handlerException(Exception ex) {
    log.error("未知异常", ex);
    return HttpResult.operateFailure(ex.getLocalizedMessage());
  }
}