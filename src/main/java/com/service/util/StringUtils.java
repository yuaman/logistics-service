package com.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 字符串工具类
 * 
 * @author sunaolin
 * 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    public StringUtils() {
        super();
    }

    /**
     * 使用给定的 charset 将此 String 编码到 byte 序列，并将结果存储到新的 byte 数组。
     * 
     * @param content 字符串对象
     * 
     * @param charset 编码方式
     * 
     * @return 所得 byte 数组
     */
    public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }

        try {
            return content.getBytes(charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Not support:" + charset, ex);
        }
    }

}