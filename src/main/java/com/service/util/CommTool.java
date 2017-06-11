package com.service.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
* @ClassName: CommTool
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 王闯   gouliren_gmail_com
* @date 2014-1-23 下午10:13:41
*
*/ 
public class CommTool {
	
	/**
	* @Title: checkList
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param list
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/ 
	public static boolean checkList(@SuppressWarnings("rawtypes") List list){
		if(list!= null && list.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	* @Title: checkMapNull
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param map
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/ 
	public static boolean checkMapNull(@SuppressWarnings("rawtypes") Map map){
		if(map!= null && map.size()>0){
			return true;
		}
		return false;
	}
	
	
	/**
	* @Title: checkMapEmpty
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param m
	* @param @param s
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/ 
	public  static boolean checkMapEmpty(@SuppressWarnings("rawtypes") Map m,String s){
		if(m!= null && m.get(s)!=null){
			return true;
		}
		return  false;
	}
	
	/**
	* @Title: checkStr
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param str
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/ 
	public static boolean checkStr(String str){
		if(str!= null && !str.isEmpty()){
			return true;
		}
		return false;
	}
	
	
	/** 
	* @Title: checkInt 
	* @Description: 判断数值非空
	* @param @param i
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws 
	*/ 
	
	public static boolean  checkInt(Integer integer){
		if( integer!= null){
			return  true;
		}
		return false;
	}
	
	
	public static boolean checkFloat(Float float1){
		if (float1 != null) {
			return true;
		}
		return false;
	}
 	 

    
   
	
	/**
	 * 
	* @Title: isInteger 
	* @Description: 判断字符串是否是整数 
	* @param @param value
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	 public static boolean isInteger(String value) {
	  try {
	   Integer.parseInt(value);
	   return true;
	  } catch (NumberFormatException e) {
	   return false;
	  }
	 }

	 /**
	  * 
	 * @Title: isDouble 
	 * @Description: 判断字符串是否是浮点数
	 * @param @param value
	 * @param @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws
	  */
	 public static boolean isDouble(String value) {
	  try {
	   Double.parseDouble(value);
	   if (value.contains("."))
	    return true;
	   return false;
	  } catch (NumberFormatException e) {
	   return false;
	  }
	 }
	/**
	 * 
	* @Title: isInteger 
	* @Description: 判断字符串是否是数字
	* @param @param value
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	 public static boolean isNumber(String value) {
	  return isInteger(value) || isDouble(value);
	 }
	 
	 /** 
	* @Title: currentDate 
	* @Description: TODO(获取当前时间字符串类型) 
	* @param @param i
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/ 
	public static String currentDate(int i){
		 String[] format = new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"};
		 SimpleDateFormat dateFormat = new SimpleDateFormat(format[i]);
		 Date date = new Date();
		 return dateFormat.format(date);
	 }
	/**
	 * 
	* @Title: formatDate 
	* @Description: TODO[] 
	* @param @param i
	* @param @param date
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String formatDate(int i,Date date){
			 String[] format = new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"};
			 SimpleDateFormat dateFormat = new SimpleDateFormat(format[i]);
			 return dateFormat.format(date);
		 }
	/**
	 * 获取当前的前一天的时间
	* @Title: getTheDayBeforeToday 
	* @Description: TODO[] 
	* @param @return    设定文件 
	* @return Date    返回类型 
	* @throws
	 */
	public static Date getTheDayBeforeToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.format(date);
		return date;
	}
}
