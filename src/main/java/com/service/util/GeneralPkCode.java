package com.service.util;
import java.util.Random;
import java.util.UUID;

/**
 * @author Administrator
 *	主键生成器
 */
public class GeneralPkCode {
	
	/**
	 * 生成UUID主键
	 */
	public static String getUuId(){
    	UUID uuid  =  UUID.randomUUID(); 
    	return uuid.toString().replaceAll("-", "");
	}
	
	/**
	 * 根据系统时间毫秒数及随机数生成主键
	 */
	
	public static String getMillies(){
		String milliesId = String.valueOf(System.currentTimeMillis()+new Random().nextInt(100));
    	return milliesId;
	}

}
