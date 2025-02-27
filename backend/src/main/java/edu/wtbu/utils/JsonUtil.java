package edu.wtbu.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static Map<String,Object> jsonToMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json,Map.class);
    }
}
