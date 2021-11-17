package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

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
}
