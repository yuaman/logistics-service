package com.service.controller;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
/**
 * 
 * @ClassName YuantongInterface
 * @Description 圆通下单及获取 mailNo、目的地code的实现类
 * @author 于霆霖
 * @Date 2016年12月2日 下午3:16:02
 * @version 1.0.0
 */

public class YuantongInterface {
	private static Log logger  = LogFactory.getLog(YuantongInterface.class.getName());

	/**
	 * 
	* @Title: SfCreatOrder 
	* @Description: TODO[SfCreatOrder下单] 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String YTOCreatOrder(
							String name	 			,
							String address			,
							String prov				,
							String mobile		    ,
							String itemName			,
							String city             ,
							String country
							){
		// 跨境暂未考虑
		/**
		 * logistics_interface
		 */
		StringBuffer sb = new StringBuffer(); 
		sb.append("<RequestOrder>")
		  .append("<clientID>")
		  .append(YuantongParams.ClientId)
		  .append("</clientID>")
		  .append("<logisticProviderID>")
		  .append("YTO")
		  .append("</logisticProviderID>")
		  .append("<customerId>")
		  .append(YuantongParams.ClientId)
		  .append("</customerId>")
		  .append("<txLogisticID>")
		  .append(String.valueOf(System.currentTimeMillis()))
		  .append("</txLogisticID>")
		  .append("<orderType>")
		  .append(1)
		  .append("</orderType>")
		  .append("<serviceType>")
		  .append(0)
		  .append("</serviceType>")
		  
		  .append("<sender>")
		     .append("<name>")
		     .append("大连智慧光科技")
		     .append("</name>")
             .append("<mobile>")
             .append("0411-39268819")
             .append("</mobile>")
             .append(" <prov>")
             .append("辽宁")
             .append("</prov>")
             .append("<city>")
             .append("大连、金州新区")
             .append("</city>")
             .append("<address>")
             .append("双D港SEMI大厦")
             .append("</address>")
		  .append("</sender>")
		  
		  .append("<receiver>")
		     .append("<name>")
		     .append(name)
		     .append("</name>")
          .append("<mobile>")
          .append(mobile)
          .append("</mobile>")
          .append(" <prov>")
          .append(prov)
          .append("</prov>")
          .append("<city>")
          .append(city+","+country)
          .append("</city>")
          .append("<address>")
          .append(address)
          .append("</address>")
		  .append("</receiver>")
		  
		  .append("<item>")
		  .append("<itemName>")
	      .append(itemName)
	      .append("</itemName>")
	      .append("<number>")
	      .append(1)
	      .append("</number>")
		  .append("</item>")
		   
		  .append("<special>")
		  .append(0)
		  .append("</special>")
		  
		  .append("</RequestOrder>");
		/**
		 * data_digest
		 */
		String rep = YTO(sb.toString());
		logger.info("请求响应"+rep);
		
		/**
		 * 对响应进行处理
		 */
        List list = getContext(rep);  
        
        String mailNo = (String) list.get(3);
        logger.info("YTO_maiNo:==="+mailNo);
        
        String san_duan_ma = (String) list.get(4);
        logger.info("三段码"+list.get(4));
        
        String destCode  = (String)list.get(5);
        logger.info("目的地code===="+list.get(5));
        
        /**
         * 圆通目的地接口调用
         */
//        String transferCenterCodeXML = transferCenterCode(mailNo,address,city,prov,country);
		
//        List list2 = getContext(transferCenterCodeXML);
        
		return mailNo+","+san_duan_ma+","+destCode;
	}
	
	/**
	 * XML转map
	 * @param html
	 * @return
	 */
	public static List getContext(String html) {  
        List resultList = new ArrayList();  
        Pattern p = Pattern.compile(">([^</]+)</");//正则表达式 commend by danielinbiti  
        Matcher m = p.matcher(html );//  
        while (m.find()) {  
            resultList.add(m.group(1));//  
        }  
        return resultList;  
    }  
	
	/**
	 * 得到目的地code
	 *//*
	public static  String transferCenterCode(String maiNo,String address,String city,String prov,String country) {  
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");  
		java.util.Date date=new java.util.Date();  
		String timestamp=sdf.format(date);
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0'?>")
		.append("<ufinterface>")
		.append("<Result>")
		.append("<ReceiveTransferAddressInfo>")
		.append("<Province>")
		.append(prov)
		.append("</Province>")
		.append("<City>")
		.append(city)
		.append("</City>")
		.append("<District>")
		.append(country)
		.append("</District>")
		.append("</ReceiveTransferAddressInfo>")
		.append("</Result>")
		.append("</ufinterface>");
				
		try{
			//打开连接
			URL url = new URL(YuantongParams.url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			
	         logger.info("YTO目的地codexml传输之前:"+sb);
			//加密
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update((sb+ YuantongParams.PartnerID).getBytes("UTF-8"));
			byte[] abyte0 = messagedigest.digest();
			String data_digest = new String(Base64.encodeBase64(abyte0));
			
			String A = "app_key"+YuantongParams.app_key+
					   "format"+YuantongParams.format+
					   "method"+YuantongParams.method+
					   "timestamp"+timestamp+
					   "user_id"+YuantongParams.user_id+
					   "v"+YuantongParams.v;
			
			String B = YuantongParams.Secret_Key + A;
			
			String C = MD5.MD5_32bit(B);
			
			String D = C.toUpperCase();
			
			
			//开始时间
			long a = System.currentTimeMillis();
			
			//查询
			String queryString = "sign=" + D
					+ "&app_key="+ YuantongParams.app_key
					+ "&format="+YuantongParams.format
					+ "&method=" + YuantongParams.method
					+ "&timestamp=" +timestamp
					+ "&user_id=" + YuantongParams.user_id
					+ "&v=" + YuantongParams.v
					+ "&param=" + sb.toString();
			
			logger.info("目的地code请求发送全文"+queryString);
			
			out.write(queryString);
			out.flush();
			out.close();
			//获取服务端的反馈
			String responseString = "";
			String strLine = "";
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((strLine = reader.readLine()) != null) {
				responseString += strLine + "\n";
			}
			in.close();
			
			//结束时间
			long b = System.currentTimeMillis();
			
			//响应时间
			long c = b - a;
//			System.err.print("YTO目的地code响应时间："+c + "ms\n");
			
//			System.err.print("YTO目的地code请求的返回信息："+responseString);
			return responseString;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
    */
	
	
	/**
	 * 圆通下单
	 * @param xml
	 * @return
	 */
	
	public static String YTO(String xml) {
		try{
			//打开连接
			URL url = new URL(YuantongParams.YTCreateOrder);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			
	         logger.info("xml传输之前:"+xml);
			//加密
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update((xml+ YuantongParams.PartnerID).getBytes("UTF-8"));
			byte[] abyte0 = messagedigest.digest();
			String data_digest = new String(Base64.encodeBase64(abyte0));
			
			//开始时间
			long a = System.currentTimeMillis();
			
			//查询
			String queryString = "logistics_interface=" + URLEncoder.encode(xml, "UTF-8")
					+ "&data_digest="+ URLEncoder.encode(data_digest,"UTF-8")
					+ "&clientId=" + URLEncoder.encode(YuantongParams.ClientId, "UTF-8");
			out.write(queryString);
			out.flush();
			out.close();
			//获取服务端的反馈
			String responseString = "";
			String strLine = "";
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((strLine = reader.readLine()) != null) {
				responseString += strLine + "\n";
			}
			in.close();
			
			//结束时间
			long b = System.currentTimeMillis();
			
			//响应时间
			long c = b - a;
//			System.err.print("响应时间："+c + "ms\n");
			
//			System.err.print("请求的返回信息："+responseString);
			return responseString;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	 public static String MD5(String sourceStr) {
	        String result = "";
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            md.update(sourceStr.getBytes());
	            byte b[] = md.digest();
	            int i;
	            StringBuffer buf = new StringBuffer("");
	            for (int offset = 0; offset < b.length; offset++) {
	                i = b[offset];
	                if (i < 0)
	                    i += 256;
	                if (i < 16)
	                    buf.append("0");
	                buf.append(Integer.toHexString(i));
	            }
	            result = buf.toString();
//	            System.out.println("MD5(" + sourceStr + ",32) = " + result);
	        } catch (NoSuchAlgorithmException e) {
	            System.out.println(e);
	        }
//	        System.out.println("32位大写md5:"+result.toUpperCase());
	        return result.toUpperCase();
	    }
	 
	 public static String Data(String Citys,String Pro,String Country)
		{
			String method = "yto.BaseData.TransferInfo";
	        String app_key = YuantongParams.app_key;
	        String user_id = YuantongParams.user_id;
	        String Format = "xml";
	        String Secret_Key = YuantongParams.Secret_Key;//Secret_Key 私钥
	        //String WaybillNumber = "1111111111";//单号
	     /*   String Pro="江西省";
	        String Citys="宜春市";
			String Country="樟树市";*/
	        
			SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date now = Calendar.getInstance().getTime();
			String timeStr=time.format(now); 
			
	        String Paramet = "app_key=" + app_key + "&format=" + Format + "&method="
	        + method + "&timestamp=" + timeStr
	        + "&user_id=" + user_id + "&v=1.0";
	        
	        String param="&param=<?xml version=\"1.0\"?><ufinterface><Result><ReceiveTransferAddressInfo><Province>"
	        +Pro
	        +"</Province><City>"
			+Citys
			+"</City><District>"
			+Country
			+"</District></ReceiveTransferAddressInfo></Result></ufinterface>";
	        
	        String[] Arr= Paramet.split("&");
	        StringBuilder Sb=new StringBuilder();
	        Sb.append(Secret_Key);
	        for(int i=0;i<Arr.length;i++)
	        {
	        	if(Arr[i].split("=").length!=2)
	        	{
	        		System.out.println("参数格式不正确");
	        	}
	        	String ParName=(Arr[i].split("="))[0].trim();
	        	String ParValue=(Arr[i].split("="))[1].trim();
	        	Sb.append(ParName+ParValue);
	        }
//	        System.out.println("待加密的sign："+Sb.toString());
	        String sign=MD5(Sb.toString());
	        
	        logger.info("destCode请求最终发送的data："+"sign="+sign+"&"+Paramet+param);
	        
	        return "sign="+sign+"&"+Paramet+param;
	        
		}
	 
	 
	 /**
	     * 向指定 URL 发送POST方法的请求
	     * 
	     */	public static String sender(String xmlBuilder,String url) {
			String responsexml = "";
			try {
				// 打开连接
				if(url==null){
					
				}
				URL strUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) strUrl.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setReadTimeout(120*1000*1000);
				connection.setConnectTimeout(120*1000*1000);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=utf-8");
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(xmlBuilder);
				out.flush();
				out.close();

				// 获取服务端的反馈
				String responseString = "";
				String strLine = "";
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				while ((strLine = reader.readLine()) != null) {
					responseString += strLine + "\n";
				}
				in.close();
				responsexml = responseString;
				
			} catch (Exception e) {
				
			}
			return responsexml;
		}
	     
	     public static String destCode(String citys,String Pro,String Country){
	 		String Url = "http://58.32.246.70:8001"; //
			String a = sender(Data(citys, Pro,Country),Url);
			List list = getContext(a);
			logger.info("目的地以及代码"+list);
			return (String) list.get(0);	
	     }
}




