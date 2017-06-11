package com.service.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
* @ClassName: Cookies 
* @Description: TODO[对cookie的封装] 
* @author [于霆霖] 
* @date 2016年8月31日 上午10:00:19
 */
public class Cookies {
	/**
	 * 设置cookie
	 * @param response
	 * @param name  cookie名字
	 * @param value cookie值
	 * @param maxAge cookie生命周期  以秒为单位
	 */
	public static void addCookie(HttpServletResponse response,String name,String value,int maxAge){
	    Cookie cookie = new Cookie(name,value);
	    cookie.setPath("/");
	    if(maxAge>0) {
	    	cookie.setMaxAge(maxAge);
	    } 
	    response.addCookie(cookie);
	}

	
	/**
	 * 根据名字获取cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static Cookie getCookieByName(HttpServletRequest request,String name){
	    Map<String,Cookie> cookieMap = ReadCookieMap(request);
	    if(cookieMap.containsKey(name)){
	        Cookie cookie = (Cookie)cookieMap.get(name);
	        return cookie;
	    }else{
	        return null;
	    }  
	}
	
	
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){ 
	    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
	    Cookie[] cookies = request.getCookies();
	    if(null!=cookies){
	        for(Cookie cookie : cookies){
	            cookieMap.put(cookie.getName(), cookie);
	        }
	    }
	    return cookieMap;
	}
	
	/** * 更新cookie操作
	 *   @param cookie 要修改的cookie 
	 *    @param value  * 修改的cookie的值 125 
	*/
	public static void fixCookie(HttpServletResponse response ,Cookie cookie, String value) {
		 cookie.setValue(value.trim()); 
		 cookie.setMaxAge(365*24*60*60);
		 response.addCookie(cookie); 
		 }
	
	
	/** * 移除所有cookie
	 *   @param cookies 由request获取到的cookie数组
	 *    @param 
	*/
	public static void removeAllCookies(HttpServletResponse response ,Cookie[] cookies){
		if (cookies != null && cookies.length > 0) {
			for(Cookie cookie: cookies){
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}



}
