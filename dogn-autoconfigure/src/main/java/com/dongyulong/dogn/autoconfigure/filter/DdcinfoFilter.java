package com.dongyulong.dogn.autoconfigure.filter;

import com.dongyulong.dogn.tools.entities.DdcinfoEntity;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.WebException;
import com.dongyulong.dogn.autoconfigure.monitor.handle.DdcHelper;
import com.dongyulong.dogn.tools.codec.EncryptUtils;
import com.dongyulong.dogn.tools.device.DeviceUtils;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ddinfo的拦截器信息
 *
 * @author zhangshaolong
 * @create 2021/12/17
 **/
@Slf4j
public class DdcinfoFilter extends OncePerRequestFilter {

    //DES_KEY
    private static final String DES_KEY = "bShORr6y6EQ=";

    private static final String DDC_INFO = "ddcinfo";

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ddcinfo = httpRequest.getHeader(DDC_INFO);
        String requestPath = httpRequest.getRequestURI();
        DdcinfoEntity entity = null;
        if (StringUtils.isNotBlank(ddcinfo)) {
            String decryptDdcInfo = null;
            try {
                decryptDdcInfo = EncryptUtils.decryptDESBase64(ddcinfo, DES_KEY);
                entity = new Gson().fromJson(decryptDdcInfo, DdcinfoEntity.class);
                //这段代码啥意思
                DdcHelper.setCurrentDdcInfo(entity);
            } catch (JsonParseException e) {
                log.error(String.format("request[%s] parse ddcinfo[%s] failed.", requestPath, decryptDdcInfo), e);
            } catch (Exception e) {
                log.error(String.format("request[%s] decrypt ddcinfo[%s] failed.", requestPath, ddcinfo), e);
            }
        } else {
            DdcHelper.setCurrentDdcInfo(null);
        }
        boolean result = checkAppVersion(entity);
        if (!result) {
            log.warn("-- LOW version, rejected!");
            log.warn("-- booking minversion: " + SystemPropertyUtils.getProperty("aop.check.minversion.booking", "1.0.0"));
            log.warn("-- driver minversion: " + SystemPropertyUtils.getProperty("aop.check.minversion.driver", "1.0.0"));
            log.warn("-- ddcInfo: " + entity.getVersion() + "/" + entity.getMobiletype() + "/" + entity.getIdentifier());
            throw new WebException(ErrorCode.UPGRADE.getCode(), SystemPropertyUtils.getProperty("aop.check.minversion.toast", ErrorCode.UPGRADE.getMsg()));
        }
        filterChain.doFilter(httpRequest, response);
    }


    //检查版本信息
    private boolean checkAppVersion(DdcinfoEntity ddcinfo) {
        if (ddcinfo == null) {
            return true;
        }
        String version = ddcinfo.getVersion();
        String mobileType = ddcinfo.getMobiletype();
        String identifier = ddcinfo.getIdentifier();
        log.debug("ddcInfo: " + version + "/" + mobileType + "/" + identifier);
        if (org.apache.commons.lang.StringUtils.isBlank(version)) {
            return false;
        }
        if (org.apache.commons.lang.StringUtils.isBlank(mobileType) || (!("1,2").contains(mobileType) && org.apache.commons.lang.StringUtils.isBlank(identifier))) {
            return true;
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(identifier) && (identifier.endsWith("h5") || identifier.endsWith("mini"))) {
            return true;
        }
        if ("1".equals(mobileType)) {
            //ios处理逻辑
            if (org.apache.commons.lang.StringUtils.isBlank(identifier)) {
                return false;
            }
            if (identifier.endsWith("driver")) {
                //司机端
                return DeviceUtils.checkVersionNew(version, SystemPropertyUtils.getProperty("aop.check.minversion.driver", "1.0.0"));
            } else {
                //乘客端
                return DeviceUtils.checkVersionNew(version, SystemPropertyUtils.getProperty("aop.check.minversion.booking", "1.0.0"));
            }
        } else if ("2".equals(mobileType)) {
            //android处理逻辑
            if (version.contains("TX") || version.contains("taxi") || (org.apache.commons.lang.StringUtils.isNotBlank(identifier) && identifier.endsWith("driver"))) {
                //司机端
                return DeviceUtils.checkVersionNew(version, SystemPropertyUtils.getProperty("aop.check.minversion.driver", "1.0.0"));
            } else {
                //乘客端
                return DeviceUtils.checkVersionNew(version, SystemPropertyUtils.getProperty("aop.check.minversion.booking", "1.0.0"));
            }
        }
        return false;
    }
}
