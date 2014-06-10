package com.pajk.diablo.im.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午2:33
 * 默认采用的是fastjson
 * </pre>
 */
public abstract class JsonUtils {

    /**
     * 将对象转换为JSON格式
     * 
     * @param model
     * @return
     * @throws java.io.IOException
     */
    public static String toStr(Object model) throws IOException {

        return JSON.toJSONString(model);
    }

    /**
     * 将JSON字符串转换为指定类实例
     * 
     * @param <T>
     * @param content
     * @param clazz
     * @return
     * @throws java.io.IOException
     */
    public static <T> T fromStr(String content, Class<T> clazz) throws IOException {
        return JSON.parseObject(content, clazz);
    }

    /**
     * 将JSON字符串转换为Map
     * 
     * @param content
     * @return
     * @throws java.io.IOException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> fromStrToMap(String content) throws IOException {
        return fromStr(content, Map.class);
    }

    public static void main(String[] args) throws IOException {
        Map<Object, Object> testMap = new HashMap<Object, Object>();
        testMap.put("1", "hehe");
        testMap.put("1", "hehe1");
        testMap.put("12", "hehe");

        String toStr = JsonUtils.toStr(testMap);
        System.out.println(toStr);
    }
}
