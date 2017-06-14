/** 


*　　　┏┓　　　┏┓ 
*　　┏┛┻━━━┛┻┓ 
*　　┃　　　　　　　┃ 
*　　┃　　　━　　　┃ 
*　　┃　┳┛　┗┳　┃ 
*　　┃　　　　　　　┃ 
*　　┃　　　┻　　　┃ 
*　　┃　　　　　　　┃ 
*　　┗━┓　　　┏━┛ 
*　　　　┃　　　┃神兽保佑 
*　　　　┃　　　┃代码无BUG！ 
*　　　　┃　　　┗━━━┓ 
*　　　　┃　　　　　　　┣┓ 
*　　　　┃　　　　　　　┏┛ 
*　　　　┗┓┓┏━┳┓┏┛ 
*　　　　　┃┫┫　┃┫┫ 
*　　　　　┗┻┛　┗┻┛   
* @Title: DepponController.java
* @Package net.youhj.controller 
* @Description: TODO[]
* @date 2016年10月17日 上午9:23:58
* @version V1.0   
*/
package com.service.controller;

import com.service.util.*;
import com.service.util.Base64;
import com.service.util.JSONTool;
import com.service.util.MD5;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DepponController {
	private Log logger  = LogFactory.getLog(getClass());

	// 时间戳值
	public static final String TIMESTAMPVALUE = String.valueOf(System.currentTimeMillis());
	// 时间戳
	public static final String TIMESTAMP = "timestamp";
	// 下单 url
	public static final String CREATORDERURL = "";
	// 公司id
	public static final String COMPANYCODEVALUE = "";
	public static final String COMPANYCODE = "companyCode";
	// 摘要信息
	public static final String DIGEST = "digest";
	/* 摘要内容采用base64(Md5(params+apikey+timestamp))加密 */

	// 参数
	public static final String PARAMS = "params";
	// apikey
	public static final String APIKEY = "";
	// 追单url
	public static final String ORDERFOLLOWURL = "";
	// 物流公司id
	public static final String LOGISTICCOMPANYID = "logisticCompanyID";
	public static final String LOGISTICCOMPANYIDVALUE = "DEPPON";
	// mailNo
	public static final String MAILNO = "mailNo";
	// orders
	public static final String ORDERS = "orders";
	public static final String LOGISTICID = "logisticID";
	public static final String LOGISTICILOGISTICIDVALUE = "EZH";
	public static final String orderSource = "orderSource";
	public static final String serviceType = "serviceType";
	public static final String customerCode = "customerCode";
	public final static String customerID = "customerID";
	public final static String businessNetworkNo = "businessNetworkNo";
	public final static String gmtCommit = "gmtCommit";
	public final static String cargoName = "cargoName";
	public static final String backSignBill = "backSignBill";
	public static final String totalNumber = "totalNumber";
	public static final String totalWeight = "totalWeight";
	public static final String payType = "payType";
	public static final String transportType = "transportType";
	public static final String deliveryType = "deliveryType";
	public static final String tatalVolume = "totalVolume";
	//号段:8034010001-8034110000
	//渠道代号：EWBZHG
	//SIGN:EZH
	public static final Object CUSCUSTOMERCODEVALUE =  "";

	/*public static String getMailNo(){
		String mailNo = HttpComm.getHttpStrNoParam(GetUrl.RESTURL+"/deppon/getMailNo.do");
		return mailNo;
	}*/

	/**
	 * 
	* @Title: depponCreatOrder 
	* @Description: [德邦下单]
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public String depponCreatOrder(
							String cargoName	 			,
							Integer totalNumber				,
							Double totalWeight				,
//							@RequestParam(value="totalVoume",required=false)Double totalVoume				,
							String senderDetailAddress		,
							String senderMobile				,
							String senderName				,
							String senderProvince			,
							String senderCity				,
							String senderCountry			,
							String recieverDdetailAddress	,
							String recieverMobile			,
							String recieverName				,
							String recieverProvince			,
							String recieverCity				,
							String recieverCountry,
							String mailNo
							){
			//totalVolume:单位m3可为空,单件体积不能超过0.18立方，超过则退回给客户，并提供退回原因
			Map<String , Object> params = new HashMap<>();
				params.put(LOGISTICCOMPANYID, LOGISTICCOMPANYIDVALUE);
				params.put(LOGISTICID,LOGISTICILOGISTICIDVALUE+((int)(Math.random()*(9999-1000+1))+1000));
				params.put(orderSource, COMPANYCODEVALUE);
				params.put(serviceType, "1");
				params.put(customerCode,CUSCUSTOMERCODEVALUE);
				params.put(customerID, COMPANYCODEVALUE);
				params.put(gmtCommit, YMDTools.currentDateTime());
				params.put(cargoName, cargoName);
			//总数量	
				params.put("totalNumber", totalNumber);
			//总重量	
				params.put("totalWeight", totalWeight);
			//总体积
//				params.put(DepponParams.tatalVolume,totalVoume);
			//mailNo
				
				params.put(MAILNO,mailNo);
	
				//月结
				params.put(payType, "2");
				//运输方式
				params.put(transportType, "PACKAGE");
			//送货上楼
				params.put(deliveryType, "3");
				params.put(backSignBill, "0");
			
				//没有不填,有了全填
				//params.put("codValue", 200);
				//params.put("insuranceValue", 3000);
				//params.put("sieveOrder", "Y");
				//params.put("reciveLoanAccount", "6227000731470502918");
			    //params.put("accountName", "张三");
				//params.put("codType", "1");
				//短信通知
				params.put("smsNotify", "Y");
				
				//德邦营业部门编码，若没有同步德邦营业部门数据，可以不填
				//params.put("toNetworkNo", "W01061502");
				//params.put("businessNetworkNo", "W011302020515");
				//是否外发
				params.put("isOut", "Y");
				
			params.put("vistReceive", "Y");
			
			//发货人信息
			Map <String ,Object> map = new HashMap<>();
			map.put("address", senderDetailAddress);
			map.put("mobile", senderMobile);
			map.put("name", senderName);
			//省市区
			map.put("province", senderProvince);
			map.put("city", senderCity);
			map.put("county", senderCountry);
			params.put("sender", map);
			
			//收货人信息
			Map <String ,Object> map2 = new HashMap<>();
			map2.put("address", recieverDdetailAddress);
			map2.put("mobile", recieverMobile);
			map2.put("name", recieverName);
			map2.put("province", recieverProvince);
			map2.put("city", recieverCity);
			map2.put("country", recieverCountry);
			params.put("receiver", map2);
			
			
			String paramsValue = JSONTool.convertCollection2Json(params);
			String string = paramsValue.substring(1, paramsValue.length()-1);
			logger.info("下单报文"+string);
			String digestValue=Base64.getBase64(MD5.GetMD5Code(string+APIKEY+TIMESTAMPVALUE));
//			 String str1 =  HttpComm.getHttpStrFou(
//					DepponParams.CREATORDERURL,
//					DepponParams.TIMESTAMP, DepponParams.TIMESTAMPVALUE,
//					DepponParams.PARAMS,string,
//					DepponParams.COMPANYCODE, DepponParams.COMPANYCODEVALUE,
//					DepponParams.DIGEST, digestValue);
//			 logger.info("下单报文response"+str1);
//
//			return str1;
		return  "";
			
		}
		
	/**
	 * 追单
	 */
	public String depponTraceOrder(String mailNo){
		Map<String, Object> params = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String , Object> order = new HashMap<>();
		order.put(MAILNO, mailNo);
		list.add(order);
		params.put(ORDERS, list);
		params.put(LOGISTICCOMPANYID, LOGISTICCOMPANYIDVALUE);
		
		String paramsValue = JSONTool.convertCollection2Json(params);
		String string = paramsValue.substring(1, paramsValue.length()-1);
		 logger.info("追单报文"+string);
		String digestValue= Base64.getBase64(MD5.GetMD5Code(string+APIKEY+TIMESTAMPVALUE));
//		 String str1 =  HttpComm.getHttpStrFou(
//				DepponParams.ORDERFOLLOWURL,
//				DepponParams.TIMESTAMP, DepponParams.TIMESTAMPVALUE,
//				DepponParams.PARAMS,string,
//				DepponParams.COMPANYCODE, DepponParams.COMPANYCODEVALUE,
//				DepponParams.DIGEST, digestValue);
//		 logger.info("追单response:"+str1);
//		return str1;
		return  "";
	}

}
