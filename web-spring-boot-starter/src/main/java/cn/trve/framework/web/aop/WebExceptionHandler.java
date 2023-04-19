package cn.trve.framework.web.aop;

import cn.trve.framework.web.constant.dict.SystemConstant;
import cn.trve.framework.web.exception.BaseRuntimeException;
import cn.trve.framework.web.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * <b>全局异常处理</b>
 * <b>Description:</b>
 * 本处处理进入controller线程的异常
 * filer抛出的异常无法在此处理
 * <b>Company:</b>
 * <b>Author:</b> Wangtao
 * <b>Date:</b> 2021/3/24 17:17
 * </pre>
 */
@RestControllerAdvice
public class WebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<Result<?>> methodArgumentNotValid(BindException e) {
        log(e);

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();

        String errorMessage = allErrors.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));

        return ResponseEntity.ok(Result.fail(Result.Status.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler()
    public ResponseEntity<Result<?>> unknownException(Exception e) {
        log(e);

        return ResponseEntity.ok(Result.fail(SystemConstant.DEFAULT_UNKNOWN_EXCEPTION_VIEW_MESSAGE));
    }

    @ExceptionHandler()
    public ResponseEntity<Result<?>> baseRuntimeException(BaseRuntimeException e) {
        log(e);
        return ResponseEntity.ok(Result.fail(e.getViewMessage()));
    }

    private void log(Throwable e) {
        if (e instanceof BaseRuntimeException be) {
            be.log(LOGGER);
        } else {
            LOGGER.error("发生了未知异常: ", e);
        }
    }


}

