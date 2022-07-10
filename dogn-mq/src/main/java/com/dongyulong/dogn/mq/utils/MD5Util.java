package com.dongyulong.dogn.mq.utils;

import java.security.MessageDigest;

/**
 * @Author 王雪源
 * @Date 2021/12/20 13:40
 * @Version 1.0
 */
public class MD5Util {

    public static String md5(String text, String salt) throws Exception {
        byte[] bytes = (text + salt).getBytes();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 255) < 16) {
                sb.append("0");
            }
            sb.append(Long.toString((bytes[i] & 255), 16));
        }
        return sb.toString();
    }
}
