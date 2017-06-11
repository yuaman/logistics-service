package com.service.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

public class JSONTool {
	/**
	* @Title: convertCollection2Json
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param o
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	*/ 
	public static  String convertCollection2Json(Object o) {
		JSONArray json = new JSONArray();
		try {
			json = JSONArray.fromObject(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != json)
			return json.toString();
		return "";
	}
	

	/**
	* @Title: convertJson2Collection
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param s
	* @param @param cc
	* @param @return    设定文件
	* @return List<HashMap<String,Object>>    返回类型
	* @throws
	*/ 
	public static  List<HashMap<String, Object>> convertJson2Collection(String s,
			Class<?> cc) {
		List<HashMap<String, Object>> c = null;
		try {
			JSONArray jsonArray = JSONArray.fromObject(s);
			c = JSONArray.toList(jsonArray, cc);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return c;
	}

	/**
	* @Title: convertJson2List
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param s
	* @param @param clazz
	* @param @return    设定文件
	* @return List<T>    返回类型
	* @throws
	*/ 
	public static  <T> List<T> convertJson2List(String s, Class<T> clazz) {
		List<T> retList = new ArrayList<T>();
		try {
			JSONArray jsonArray = JSONArray.fromObject(s);
			retList = JSONArray.toList(jsonArray, clazz);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return retList;
	}
	
	/**
	* @Title: convertJson2Class
	* @Description: TODO(将json 转化为集合类 按照C 泛化)
	* @param @param s
	* @param @param c
	* @param @return    设定文件
	* @return List<Object>    返回类型
	* @throws
	*/ 
	public static  <T>List<T> convertJson2Class(String s,
			Class<T> c) {
		List<T> cc = null;
		try {
			JSONArray jsonArray = JSONArray.fromObject(s);
			cc = JSONArray.toList(jsonArray, c);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return cc;
	}
}
