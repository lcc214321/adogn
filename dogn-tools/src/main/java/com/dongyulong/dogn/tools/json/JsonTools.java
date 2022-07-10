package com.dongyulong.dogn.tools.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * jackson
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/8 7:59 上午
 * @since v1.0
 */
public class JsonTools {

    public static ThreadLocal<ObjectMapper> objMapperLocal = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    });

    public static ThreadLocal<ObjectMapper> objMapperNotNull = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    });

    public static <T> T toT(String jsonString, Class<T> clazz) {
        try {
            return objMapperLocal.get().readValue(jsonString, clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    public static <T> T toType(String jsonString, TypeReference<T> typeReference) {
        try {
            return objMapperLocal.get().readValue(jsonString, typeReference);
        } catch (Throwable e) {
            return null;
        }
    }

    public static String toJSON(Object value) {
        String result = null;
        try {
            result = objMapperLocal.get().writeValueAsString(value);
        } catch (Exception e) {
        }
        if ("null".equals(result)) {
            result = null;
        }
        return result;
    }


    public static <T> T toT(ObjectMapper objectMapper, String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    public static String toJSON(ObjectMapper objectMapper, Object value) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(value);
        } catch (Exception e) {
        }
        if ("null".equals(result)) {
            result = null;
        }
        return result;
    }

    public static Map<String, Object> toMap(ObjectMapper objectMapper, String jsonString) {
        return (Map) toT(objectMapper, jsonString, Map.class);
    }

    public static Map<String, Object> toMap(String jsonString) {
        return (Map) toT(jsonString, Map.class);
    }

    public static Map<String, String> toMap2(String jsonString) {
        return (Map) toT(jsonString, Map.class);
    }

}
