package com.service.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @ClassName YuantongParams
 * @Description 圆通参数列表
 * @author 于霆霖
 * @Date 2016年12月2日 下午3:16:51
 * @version 1.0.0
 */
public class YuantongParams {
	private static Log logger  = LogFactory.getLog(YuantongParams.class.getName());
	
	//公共接口   临时开发环境
		public static final String YTCreateOrder ="" ;
		public static final String ClientId = "";
		public static final String PartnerID = "";
		/**
		 * order
		 */
		
		//物流公司ID（YTO）
		public static final String logisticProviderID = "logisticProviderID";
		//	物流号
		public static final String txLogisticID = "txLogisticID";
        //订单类型(0-COD,1-普通订单,3-退货单)
		public static final String orderType = "orderType";
        //商品名称（可填默认的0）
		public static final String itemName = "itemName";
		//商品数量（可填默认的0）
		public static final String number  = "number";
		//商品类型（保留字段，暂时不用，默认填0）
		public static final String special = "special";
		//服务类型(1-上门揽收, 2-次日达 4-次晨达 8-当日达,0-自己联系)。（数据库未使用）（目前暂未使用默认为0）
		public static final String serviceType = "serviceType";
		
		//到件方联系人 
		public static final String name = "name";
		//到件方手机 
		public static final String mobile = "mobile";
		//到件方省份
	    public static final String prov = "prov";
		//到件方城市
		public static final String city  = "city";
		//到件方详细地址
		public static final String address = "address";
		//用户邮编（如果没有可以填默认的0）	
		public static final String postCode = "postCode";
		
		/**
		 * OrderResponse
		 */
		//运单号
		public static final String mailno = "mailno";
		/**
		 * 目的地code
		 */
		public static final String app_key = "";
		public static final String Secret_Key = "";
		public static final String format = "XML"; 	
		public static final String method = "yto.BaseData.TransferInfo"; 
		public static final String url = "http://58.32.246.70:8001";
		public static final String user_id = "";
		public static final String v = "1.01";
		
		
		
		  
	
}
