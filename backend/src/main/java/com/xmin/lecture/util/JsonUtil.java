package com.xmin.lecture.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.Optional;

/**
 * @author liminggangrs
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {

    }

    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json parse error", e);
        }
    }

    public static <T> T toJsonObject(String str, Class<T> clazz) {
        if (Objects.isNull(str)) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json parse error", e);
        }
    }

    public static <T> Optional<T> toJsonObject(Object obj, Class<T> clazz) {
        if (Objects.isNull(obj)) {
            return Optional.empty();
        }
        return Optional.ofNullable(objectMapper.convertValue(obj, clazz));
    }

    public static <T> T toJsonObject(String str, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json parse error", e);
        }
    }

}