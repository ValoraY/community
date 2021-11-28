package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    //生成验证码、salt的时候需要用到
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //md5 对密码进行加密
    //hello -> abc123
    //hello + salt -> 1bc123thu
    //传入的key是 密码和salt的拼接字符串
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//把传入的结果加密成16进制的字符串
    }

    /**
     * 将 服务器返回给浏览器的数据 转为 json格式的字符串
     * @param code 服务器返回给浏览器的编码（一定有）
     * @param msg  服务器返回给浏览器的提示信息（不一定有）方法重载
     * @param map 服务器返回给浏览器的业务信息（不一定有）方法重载
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map != null){
            for(String key: map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toString();
    }

    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code,Map<String,Object> map){
        return getJSONString(code,null,map);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",13);
        String jsonString = getJSONString(0, "ok", map);
        System.out.println(jsonString);
    }

}
