package com.dongyulong.dogn.autoconfigure.exception;

import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.DognCode;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.SystemException;
import com.dongyulong.dogn.common.exception.ToastException;
import com.dongyulong.dogn.common.exception.WebException;
import com.dongyulong.dogn.common.result.BaseResult;
import com.dongyulong.dogn.common.result.Result;
import com.dongyulong.dogn.common.result.ResultBuilder;
import com.dongyulong.dogn.autoconfigure.filter.holder.ExceptionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @author zhang.shaolong
 * @create 2021/11/15
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    /**
     * 正常业务捕获异常错误信息
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(WebException.class)
    @ResponseBody
    public BaseResult handleWeb(HttpServletRequest request, WebException e) {
        handleMonitor(e.getErrorCode());
        return ResultBuilder.buildFail(e.getErrorCode());
    }

    /**
     * 正常thrift业务捕获异常错误信息
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(DException.class)
    @ResponseBody
    public BaseResult handleDEx(HttpServletRequest request, WebException e) {
        handleMonitor(e.getErrorCode());
        return ResultBuilder.buildFail(e.getErrorCode());
    }

    /**
     * 捕获toast提示
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(ToastException.class)
    @ResponseBody
    public Result handleToast(HttpServletRequest request, ToastException e) {
        return ResultBuilder.buildResult(e.getCode().getCode(), e.getCode().getMsg(), e.getToast());
    }

    /**
     * 参数不对的异常
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public BaseResult handlerBeanValidationException(HttpServletRequest request,
                                                     MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        String message = ErrorCode.PARAM_ERROR.getMsg();
        if (errors.size() > 0) {
            message = errors.get(0).getDefaultMessage();
        }
        handleMonitor(ErrorCode.PARAM_ERROR);
        return ResultBuilder.buildFail(message, ErrorCode.PARAM_ERROR.getCode());
    }


    /**
     * 参数丢失的
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public BaseResult handleMissingParameter(HttpServletRequest request, Throwable ex) {
        handleMonitor(ErrorCode.PARAM_ERROR);
        return ResultBuilder.buildFail(ErrorCode.PARAM_ERROR);
    }


    /**
     * 空指针
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public BaseResult handleNullException(HttpServletRequest request, NullPointerException e) {
        handleMonitor(ErrorCode.SERVICE_ERROR);
        return ResultBuilder.buildFail(ErrorCode.SERVICE_ERROR);

    }

    /**
     * 无用的方法
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public BaseResult handleMethodSupport(HttpServletRequest request, Throwable ex) {
        handleMonitor(ErrorCode.METHOD_ERROR);
        return ResultBuilder.buildFail(ErrorCode.METHOD_ERROR);
    }

    /**
     * 不支持的类型
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public BaseResult handleMediaType(HttpServletRequest request, Throwable ex) {
        handleMonitor(ErrorCode.METHOD_NOT_FOUND);
        return ResultBuilder.buildFail(ErrorCode.METHOD_NOT_FOUND);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public BaseResult handleMessageNotReadable(HttpServletRequest request, Throwable ex) {
        handleMonitor(ErrorCode.SERVICE_ERROR);
        return ResultBuilder.buildFail(ErrorCode.SERVICE_ERROR);
    }


    @ExceptionHandler(BindException.class)
    @ResponseBody
    public BaseResult handleBindException(HttpServletRequest request, BindException exception) {
        handleMonitor(ErrorCode.PARAM_ERROR);
        return ResultBuilder.buildFail(exception.getFieldError().getDefaultMessage(), ErrorCode.PARAM_ERROR.getCode());

    }


    /**
     * 系统服务级别的错误,需要特殊处理下
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public BaseResult systemException(HttpServletRequest request, SystemException ex) {
        if (ex.getErrorCode().getCode() == 104 || ex.getErrorCode().getCode() == 101) {
            setInternalServerError();
        }
        handleMonitor(ex.getErrorCode());
        return ResultBuilder.buildFail(ex.getErrorCode());
    }

    /**
     * 服务级别的错误,需要特殊处理下
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public BaseResult globalException(HttpServletRequest request, Throwable ex) {
        log.error("globalException", ex);
        setInternalServerError();
        handleMonitor(ErrorCode.SERVICE_ERROR);
        return ResultBuilder.buildFail(ErrorCode.SERVICE_ERROR);
    }

    private void setInternalServerError() {
        RequestAttributes req = RequestContextHolder.getRequestAttributes();
        if (req == null) {
            return;
        }
        HttpServletResponse resp = ((ServletRequestAttributes) req).getResponse();
        if (resp != null) {
            resp.setStatus(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 处理异常埋点信息
     */
    private void handleMonitor(DognCode error) {
        ExceptionHolder.set(error.getCode(), true);

    }
}
