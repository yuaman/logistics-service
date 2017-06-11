package com.service.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class YMDTools {
	public static void main(String[] args) {
		//YMDTools ymdTools = new YMDTools();
		System.out.println(YMDTools.currentDateTime());
	}
	public final static SimpleDateFormat uid1 = new SimpleDateFormat("yyyyMMdd");
	public final static SimpleDateFormat uid2 = new SimpleDateFormat("yyMMddHHmmssSS");
	public final static SimpleDateFormat uid3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat uid4 = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat uid5 = new SimpleDateFormat("HH:mm:ss");
	public final static SimpleDateFormat uid6 = new SimpleDateFormat("yyyy-MM");
	
	public static String currentTimestamp(){
	    String  uuid =uid2.format(Calendar.getInstance().getTime()).toString();
		return uuid;
	}
	
	public static String currentDateTime(){
	    String  uuid =uid3.format(Calendar.getInstance().getTime()).toString();
		return uuid;
	}
	
	public static String currentDate(){
	    String  uuid =uid4.format(Calendar.getInstance().getTime()).toString();
		return uuid;
	}
	public static String currentMonth(){
	    String  uuid =uid6.format(Calendar.getInstance().getTime()).toString();
		return uuid;
	}
	
	
	/** 
	* @Title: strToDate 
	* @Description: TODO(字符串转DATE类型) 
	* @param @param str
	* @param @return    设定文件 
	* @return Date    返回类型 
	* @throws 
	*/ 
	public static Date strToDate(String date0){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		try {
			Date date = sdf.parse(date0);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  null;
	}

    /**
     * @param @param  str
     * @param @return 设定文件
     * @return Date    返回类型
     * @throws
     * @Title: strToDate
     * @Description: TODO(字符串转DATE类型)
     */
    public static Date timestampToDate(String date0) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = sdf.parse(date0);
            return date;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title: dateToStr
     * @Description: TODO(date类型转到 字符串)
     * @param @param sdf
	* @param @param date
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/ 
	public static String dateToStr(SimpleDateFormat sdf,Date date){
		String str=sdf.format(date);  
		return str;
	}
	
	
}
