package com.dongyulong.dogn.autoconfigure.controller;

import com.dongyulong.dogn.common.exception.DognCode;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.WebException;
import com.dongyulong.dogn.common.result.BaseResult;
import com.dongyulong.dogn.common.result.ResultBuilder;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 捕获一个全局异常信息
 *
 * @author zhangshaolong
 * @create 2021/12/20
 **/
@RestController
public class DognController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    /**
     * 统一的没有找到方法的处理
     *
     * @return
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public BaseResult handleError() {
        return ResultBuilder.buildFail(ErrorCode.METHOD_NOT_FOUND);
    }


    /**
     * 重新抛出异常
     */
    @RequestMapping("/error/filter")
    public BaseResult errorFilter(HttpServletRequest request) {
        Object obejct = request.getAttribute("filter.error");
        DognCode errorCode = ErrorCode.SERVICE_ERROR;
        if (obejct instanceof WebException) {
            WebException exception = (WebException) obejct;
            errorCode = exception.getErrorCode();
        }
        return ResultBuilder.buildFail(errorCode);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
