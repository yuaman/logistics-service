
package com.service.controller;




public class DepponParams {

	
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
}
