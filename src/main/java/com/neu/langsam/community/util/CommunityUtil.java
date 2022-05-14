package com.neu.langsam.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    /**
     * 生成随机字符串
     * @return 字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");//去掉所有的"-"
    }

    /**
     *      //MD5加密
     *     //hello -> abc123def456
     *     //hello + 3e4a8(salt) -> abd123def456abc
     * @param key
     * @return 加密后的key
     */
    public static String md5(String key) {
        if(StringUtils.isBlank(key)) return null;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 封装JSON工具
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map != null) {
            for (String key : map.keySet()) {
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }
    //重载JSON工具
    public static String getJsonString(int code, String msg) {
        return getJsonString(code,msg,null);
    }
    //重载JSON工具
    public static String getJsonString(int code) {
        return getJsonString(code,null,null);
    }

}
