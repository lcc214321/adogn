package com.dongyulong.dogn.tools.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/88:01 上午
 * @since v1.0
 */
public class JsonMapper {
    private static final char UNDERLINE = '_';
    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    public static final Gson GSON = new Gson();

    public static final int UNDERLINE_TO_CAMEL_CASE = 0;
    public static final int CAMEL_CASE_TO_UNDERLINE = 1;
    public static Pattern linePattern = Pattern.compile("_(\\w)");
    public static Pattern camelPattern = Pattern.compile("[A-Z]");

    public static String toJson(Object obj) {
        String jsonStr = "";
        try {
            jsonStr = JSON.toJSONString(obj);
        } catch (Exception e) {
            logger.error("toJson failed.", e);
        }
        return jsonStr;
    }

    public static <T> T json2Bean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (JSONException e) {
            logger.debug("try again with gson. json:" + jsonString);
            t = GSON.fromJson(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("json2Bean failed." + jsonString, e);
        }
        return t;
    }

    public static <T> T json2Bean(String jsonString, Type clzz) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, clzz);
        } catch (JSONException e) {
            logger.debug("try again with gson. json:" + jsonString);
            t = GSON.fromJson(jsonString, clzz);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("json2Bean failed." + jsonString, e);
        }
        return t;
    }

    public static <T> List<T> json2List(String jsonString, Class<T> cls) {
        List<T> list = null;
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            logger.error("json2List failed." + jsonString, e);
        }
        return list;
    }

    public static Map<String, Object> json2Map(String jsonString) {
        Map<String, Object> map = null;
        try {
            map = JSON.parseObject(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("json2Map failed." + jsonString, e);
        }
        return map;
    }

    public static String toJson(Object obj, int type) {
        String str = toJson(obj);
        StringBuffer sb = new StringBuffer();
        if (type == UNDERLINE_TO_CAMEL_CASE) {
            Matcher matcher = linePattern.matcher(str);
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
        } else {
            Matcher matcher = camelPattern.matcher(str);
            while (matcher.find()) {
                matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            }
            matcher.appendTail(sb);
        }
        return sb.toString();
    }
}
