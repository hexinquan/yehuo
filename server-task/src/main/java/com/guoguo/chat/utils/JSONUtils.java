package com.guoguo.chat.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangyijun on 2018/4/12.
 */
public class JSONUtils {

    public static <T> Map<String, Object> toMap(T bean) {
        if (null == bean) {
            return new HashMap<>();
        }
        String text = JSON.toJSONString(bean);
        return JSON.parseObject(text, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> List<Map<String, Object>> toListMap(List<T> beanList) {
        String text = JSON.toJSONString(beanList);
        return JSON.parseObject(text, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    public static Map<String, Object> jsonToMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static List<Map<String, Object>> jsonToListMap(String json) {
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    public static List<Map<String, Object>> toListMap(Object obj) {
        if (null == obj) {
            return new ArrayList<>();
        }
        String json = JSON.toJSONString(obj);
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    public static <T> void println(T object) {
        System.out.println(JSON.toJSONString(object, true));
    }

    /**
     * 将对象字段名转换为@JsonProperty设置的字段名
     *
     * @param fromObj
     * @return
     * @throws JsonProcessingException
     */
    public static Object jsonPropertyParse(Object fromObj) throws JsonProcessingException {

        String jsonStr = new ObjectMapper().writeValueAsString(fromObj);
        return JSONObject.parse(jsonStr);

    }
}
