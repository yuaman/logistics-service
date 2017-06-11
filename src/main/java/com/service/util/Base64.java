package com.service.util;


  
import java.io.UnsupportedEncodingException;  
  
import sun.misc.*;

/**
 *
* @ClassName: Base64
* @Description: TODO[base64加密解密]
* @author zhangdanyx@sina.cn[张丹]
* @date 2016年10月18日 上午10:24:56
 */
public class Base64 {
   // 加密
   @SuppressWarnings("restriction")
   public static String getBase64(String str) {
       byte[] b = null;
       String s = null;
       try {
           b = str.getBytes("utf-8");
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       if (b != null) {
           s = new BASE64Encoder().encode(b);
       }
       return s;
   }


   // 加密
   @SuppressWarnings("restriction")
   public static byte[] getBase64Byte(String str) {
       byte[] b = null;
       String s = null;
       try {
           b = str.getBytes("utf-8");
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       if (b != null) {
           s = new BASE64Encoder().encode(b);
       }
       byte[] srtbyte = s.getBytes();
       return srtbyte;
   }


   // 解密
   @SuppressWarnings("restriction")
   public static String getFromBase64(String s) {
       byte[] b = null;
       String result = null;
       if (s != null) {
           BASE64Decoder decoder = new BASE64Decoder();
           try {
               b = decoder.decodeBuffer(s);
               result = new String(b, "utf-8");
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       return result;
   }
   public static void main(String[] args) {
       System.out.println(Base64.getBase64("123456"));
       System.out.println(Base64.getFromBase64("MTIzNDU2"));
   }
}

