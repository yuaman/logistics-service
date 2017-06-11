package com.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * MD5 算法
*/
public class MD5 {
    
    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public MD5() {
    }
    
 // 获得MD5摘要算法的 MessageDigest 对象
 	private static MessageDigest _mdInst = null;
 	private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
 	private static MessageDigest getMdInst() {
 		if (_mdInst == null) {
 			try {
 				_mdInst = MessageDigest.getInstance("MD5");
 			} catch (NoSuchAlgorithmException e) {
 				e.printStackTrace();
 			}
 		}
 		return _mdInst;
 	}

 	public final static String encode(String s) {
 		try {
 			byte[] btInput = s.getBytes();
 			// 使用指定的字节更新摘要
 			getMdInst().update(btInput);
 			// 获得密文
 			byte[] md = getMdInst().digest();
 			// 把密文转换成十六进制的字符串形式
 			int j = md.length;
 			char str[] = new char[j * 2];
 			int k = 0;
 			for (int i = 0; i < j; i++) {
 				byte byte0 = md[i];
 				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
 				str[k++] = hexDigits[byte0 & 0xf];
 			}
 			return new String(str);
 		} catch (Exception e) {
 			e.printStackTrace();
 			return null;
 		}
 	}

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    @SuppressWarnings("unused")
	private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    @SuppressWarnings("static-access")
	public static void main(String[] args) {
        MD5 getMD5 = new MD5();
        System.out.println(getMD5.GetMD5Code("123456"));
    }
    
    public static final String MD5_32bit(String readyEncryptStr) throws NoSuchAlgorithmException{  
        if(readyEncryptStr != null){  
            //Get MD5 digest algorithm's MessageDigest's instance.  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            //Use specified byte update digest.  
            md.update(readyEncryptStr.getBytes());  
            //Get cipher text  
            byte [] b = md.digest();  
            //The cipher text converted to hexadecimal string  
            StringBuilder su = new StringBuilder();  
            //byte array switch hexadecimal number.  
            for(int offset = 0,bLen = b.length; offset < bLen; offset++){  
                String haxHex = Integer.toHexString(b[offset] & 0xFF);  
                if(haxHex.length() < 2){  
                    su.append("0");  
                }  
                su.append(haxHex);  
            }  
            return su.toString();  
        }else{  
            return null;  
        }  
    }  
}