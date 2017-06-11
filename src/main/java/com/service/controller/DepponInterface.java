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
* @Title: DepponInterface.java 
* @Package net.youhj.controller 
* @Description: TODO[]
* @author zhangdanyx@sina.cn[张丹]   
* @date 2016年10月17日 上午9:23:58 
* @version V1.0   
*/
package com.service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.service.util.JSONTool;
import com.service.util.MD5;
import com.service.util.YMDTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DepponInterface  {
	private Log logger  = LogFactory.getLog(getClass());
	/**
	 * 
	* @Title: depponCreatOrder 
	* @Description: TODO[德邦下单] 
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
				params.put(DepponParams.LOGISTICCOMPANYID, DepponParams.LOGISTICCOMPANYIDVALUE);
				params.put(DepponParams.LOGISTICID,DepponParams.LOGISTICILOGISTICIDVALUE+((int)(Math.random()*(9999-1000+1))+1000));
				params.put(DepponParams.orderSource, DepponParams.COMPANYCODEVALUE);
				params.put(DepponParams.serviceType, "1");
				params.put(DepponParams.customerCode,DepponParams.CUSCUSTOMERCODEVALUE);
				params.put(DepponParams.customerID, DepponParams.COMPANYCODEVALUE);
				params.put(DepponParams.gmtCommit, YMDTools.currentDateTime());
				params.put(DepponParams.cargoName, cargoName);
			//总数量	
				params.put(DepponParams.totalNumber, totalNumber);
			//总重量	
				params.put(DepponParams.totalWeight, totalWeight);
			//总体积
//				params.put(DepponParams.tatalVolume,totalVoume);
			//mailNo
				
				params.put(DepponParams.MAILNO,mailNo);
	
				//月结
				params.put(DepponParams.payType, "2");
				//运输方式
				params.put(DepponParams.transportType, "PACKAGE");
			//送货上楼
				params.put(DepponParams.deliveryType, "3");
				params.put(DepponParams.backSignBill, "0");
			
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
			String digestValue=net.youhj.util.Base64.getBase64(MD5.GetMD5Code(string+DepponParams.APIKEY+DepponParams.TIMESTAMPVALUE));
			 String str1 =  HttpComm.getHttpStrFou(
					DepponParams.CREATORDERURL,
					DepponParams.TIMESTAMP, DepponParams.TIMESTAMPVALUE,
					DepponParams.PARAMS,string,
					DepponParams.COMPANYCODE, DepponParams.COMPANYCODEVALUE,
					DepponParams.DIGEST, digestValue);
			 logger.info("下单报文response"+str1);
			
			return str1;
			
		}
		
	/**
	 * 追单
	 */
	public String depponTraceOrder(String mailNo){
		Map<String, Object> params = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String , Object> order = new HashMap<>();
		order.put(DepponParams.MAILNO, mailNo);
		list.add(order);
		params.put(DepponParams.ORDERS, list);
		params.put(DepponParams.LOGISTICCOMPANYID, DepponParams.LOGISTICCOMPANYIDVALUE);
		
		String paramsValue = JSONTool.convertCollection2Json(params);
		String string = paramsValue.substring(1, paramsValue.length()-1);
		 logger.info("追单报文"+string);
		String digestValue=net.youhj.util.Base64.getBase64(MD5.GetMD5Code(string+DepponParams.APIKEY+DepponParams.TIMESTAMPVALUE));
		 String str1 =  HttpComm.getHttpStrFou(
				DepponParams.ORDERFOLLOWURL,
				DepponParams.TIMESTAMP, DepponParams.TIMESTAMPVALUE,
				DepponParams.PARAMS,string,
				DepponParams.COMPANYCODE, DepponParams.COMPANYCODEVALUE,
				DepponParams.DIGEST, digestValue);
		 logger.info("追单response:"+str1);
		return str1;
		
	}

}
