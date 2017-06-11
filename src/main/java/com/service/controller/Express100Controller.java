package com.service.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.sun.jmx.snmp.Timestamp;
import com.xhfxw.logistics.domain.LogisticsAddrNodeInfo;
import com.xhfxw.logistics.repository.LogisAddtNodeInfoRepository;
import com.xhfxw.logistics.util.GeneralPkCode;
import com.xhfxw.logistics.util.MD5;
import com.xhfxw.logistics.util.YMDTools;
import jxl.write.DateTime;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.filesystem.local.Convert;
import org.hibernate.annotations.Source;
import org.hibernate.jpa.internal.util.LogHelper;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhfxw.logistics.util.JacksonHelper;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import static javax.print.attribute.standard.MediaPrintableArea.MM;

/**
 * 快递100物流对接工具类
 * @author 于霆霖
 *2017/05/15
 */

@RequestMapping("/express-joint")
@Controller

public class Express100Controller  {

    private static Log logger  = LogFactory.getLog(SfJointController.class);
    //订阅请求接口地址
    public static final String pollAddr = "";
    //授权码
    public static final String key = "";
    //信息查询接口地址
    public static final String queryAddr = "";
    //公司编号
    public static final String customer = "";
    //发短信接口
    public static final String smsAddr = "";

    @Inject
    private LogisAddtNodeInfoRepository logisAddtNodeInfoRepository;

    public void setLogisAddtNodeInfoRepository(LogisAddtNodeInfoRepository logisAddtNodeInfoRepository) {
        this.logisAddtNodeInfoRepository = logisAddtNodeInfoRepository;
    }

    /**
     * 测试发送短信,短信接口暂时未开通，有发货短信通知和购买成功短信通知
     * //通知已发货模版：1；通知购买成功：5；
     * @throws Exception
     */
//    @org.junit.Test
    @RequestMapping("/test-sms")
    @ResponseBody
    public  String Testsms() throws Exception {
        String result = sms("15326171094","江流","zhongtong","京东快递","倩碧黄油","1");
//        logger.info("===========================");
        return result;
    }

    /**
     * 发送短信接口
     * @throws Exception
     */

    @RequestMapping("/sms")
    @ResponseBody
    public static String sms(String phone,String receiver,String comCode,String comName,String commondity,String templateId) throws Exception {
        Map<String, String> phones = new LinkedHashMap<>();
        phones.put("phone", "15326171094");
        phones.put("receiver", "江流");
        phones.put("comCode", "zhongtong");
        phones.put("comName", "京东快递");
        phones.put("commondity", "倩碧黄油");

        Map<String,Object> json = new LinkedHashMap<>();
        //通知已发货模版：1；通知购买成功：5；
        json.put("templateId", "1");
        json.put("phones", phones);

        Map<String, String> map = new LinkedHashMap();
        map.put("method", "sendsmsbytemplate");
        map.put("userid",customer);
        map.put("token",JacksonHelper.toJSON(MD5.encode(customer+key)) );
        map.put("json", JacksonHelper.toJSON(json));

        logger.info("map========="+map);

        String result = postData(smsAddr, map, "utf-8");

        logger.info("result========"+result);

        return result;

    }

    /**
     * 查询地址节点信息
     * @throws Exception
     */
    @RequestMapping("/test-express100query")
    @ResponseBody
    public static String TestExpress100query() throws Exception {
        String result = Express100query("zhongtong","438179789777","广东深圳","北京朝阳");
        return result;
    }

    /**
     * 查询地址节点信息
     * @return
     * @throws Exception
     */
//	@org.junit.Test
    @RequestMapping("express100query")
    @ResponseBody
    public static String Express100query(String company,String number,String from,String to) throws Exception {
        Map<String, String> params_map = new LinkedHashMap<>();
        params_map.put("com", company);
        params_map.put("num", number);
        params_map.put("from", from);
        params_map.put("to", to);

        String sign = MD5.encode(JacksonHelper.toJSON(params_map)+key+customer);

        Map<String, String> map = new LinkedHashMap<>();
        map.put("customer", customer);
        map.put("sign",sign);
        map.put("param",JacksonHelper.toJSON(params_map));

        logger.info("params_map_string"+JacksonHelper.toJSON(params_map));

        String result = postData(queryAddr, map, "utf-8");

        Map<String, Object> response_map = JacksonHelper.fromJSON(result, Map.class);

        logger.info("response_map========="+response_map);

        String state = response_map.get("state").toString();

        logger.info("state========="+state);

        if (state.equals("0")) {
            state = "在途中";
        }else if (state.equals("1")) {
            state = "已揽收";
        }else if (state.equals("2")) {
            state = "疑难";
        }else if (state.equals("3")) {
            state = "已签收";
        }

        ArrayList<LinkedHashMap<String,String>> node_array  = (ArrayList<LinkedHashMap<String, String>>) response_map.get("data");

//		ArrayList<LinkedHashMap<String, String>> node_array = JacksonHelper.fromJSON?(data_string, ArrayList.class);

        for (LinkedHashMap<String, String> node : node_array) {
            String context = node.get("context").toString();
            logger.info("context========"+context);

            String ftime = node.get("ftime").toString();
            logger.info("ftime========="+ftime);

        }
        logger.info("result=========="+result);

        return JacksonHelper.toJSON(result);
    }

    /**
     * 信息推送接收
     * @throws UnsupportedEncodingException
     * //正式地址：http://106.14.82.164:8081/express-joint/express100accept.do?param=
    //利用其正式服务器推送过来的数据在json前多一个逗号，本地测不出来，发到服务器即会报错，所以需要将回返数据去掉首位字符（如果有一天又报错，说明他们发现了问题，我们去掉首位字符必定又报错）
     */
//	@org.junit.Test
    @RequestMapping("express100accept")
    @ResponseBody
    public String Express100accept(String param) throws UnsupportedEncodingException {
//        param = URLDecoder.decode(param, "utf-8");
        logger.info("param_before=========" + param);
        param = param.substring(1);
        logger.info("param_after=========" + param);


        Map<String, Object> map = JacksonHelper.fromJSON(param, Map.class);

        logger.info("map========"+map);

        LinkedHashMap< String, Object> lastResult = (LinkedHashMap<String, Object>) map.get("lastResult");

        logger.info("lastResult========"+lastResult);

        String mailNo = lastResult.get("nu").toString();

        logger.info("mailNo===="+mailNo);

//        String state = lastResult.get("status").toString();

//        String statStr = stateMap.get("status").toString();

//        logger.info("statStr========="+statStr);

        ArrayList<LinkedHashMap<String, String>> data = (ArrayList<LinkedHashMap<String, String>>) lastResult.get("data");

        logger.info("data"+data);

//        for (LinkedHashMap<String,String> node : data) {

        //每次接收快递100推送只存储最新一条记录
        Map<String, String> node = data.get(0);
        logger.info("node================" + node);


            //节点信息待存储
            String time = node.get("time").toString();

            logger.info(time);

            String ftime = node.get("ftime").toString();
            logger.info(ftime);

            String context = node.get("context").toString();
            logger.info(context);

            //occurTime  LocalDateTime存储不了报错，只好改成Date
//            LocalDateTime occurTime = LocalDateTime.parse(ftime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//            logger.info("ocurTime========="+occurTime);
            //路由节点信息持久化
            LogisticsAddrNodeInfo logisticsAddrNodeInfo = new LogisticsAddrNodeInfo();
            logisticsAddrNodeInfo.setMailNo(mailNo);
            logisticsAddrNodeInfo.setOccurTime(YMDTools.strToDate(ftime));
//            logisticsAddrNodeInfo.setAction("aaa");
            logisticsAddrNodeInfo.setRemark(context);
            logisticsAddrNodeInfo.setCompany("kuaidi100");
//            logisticsAddrNodeInfo.setStation("aaa");
//            logisticsAddrNodeInfo.setTradeSn(GeneralPkCode.getMillies());

            logger.info("logisticsAddrNodeInfo==="+JacksonHelper.toJSON(logisticsAddrNodeInfo));
            logisAddtNodeInfoRepository.save(logisticsAddrNodeInfo);
//        }



        Map<String,Object> response_map = new LinkedHashMap<>();
        response_map.put("result", true);
        response_map.put("returnCode", "200");
        response_map.put("message", "成功");

        logger.info("response_map========="+response_map);
        return JacksonHelper.toJSON(response_map);
    }

    /**
     * 测试订阅请求
     * @throws Exception
     */
    @RequestMapping("/tes-express100poll")
    @ResponseBody
    public static String TestExpress100poll() throws Exception {
        String result = Express100poll("ems","9703803632508","广东省深圳市南山区","北京市朝阳区");
        return result;
    }

    /**
     * 订阅请求
     * @return
     * @throws Exception
     */
//	@org.junit.Test
    @RequestMapping("express100poll")
    @ResponseBody
    public static  String Express100poll(String company,String number,String from,String to) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("company", company);
        map.put("number", number);
        map.put("from", from);//河北省驻马店市驻马店区赵家屯
        map.put("to",to);//辽宁省铁岭市开原区赵家屯
        map.put("key", key);

        Map<String, String> parameters_map = new LinkedHashMap<>();
        parameters_map.put("callbackurl", "http://www.xhfxw.com/kuaidi?callbackid=wwwwwwww");
        String parameters_map_string = JacksonHelper.toJSON(parameters_map);

        map.put("parameters",parameters_map);

        Map<String, String> total_map = new LinkedHashMap<>();
        total_map.put("schema", "json");
        String map_string = JacksonHelper.toJSON(map);
        map_string = map_string.replaceAll("//", "");
        total_map.put("param",map_string);

        String poll_result = postData(pollAddr, total_map, "utf-8");
        logger.info("poll_result========="+poll_result);

        HashMap<String, String> hashMap = JacksonHelper.fromJSON(poll_result, HashMap.class);

        if (hashMap.containsKey("message")) {
            if (hashMap.get("message").toString().equals("提交成功")) {
                return "success";
            }
        }

        return "failed";
    }


    /**
     *     发送执行
     * @param url
     * @param params
     * @param codePage
     * @return
     * @throws Exception
     */
    public  static String postData(String url, Map<String, String> params, String codePage) throws Exception {

        final HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(25 * 1000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(30 * 1000);

        final PostMethod method = new PostMethod(url);
        if (params != null) {
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, codePage);
            method.setRequestBody(assembleRequestParams(params));
        }
        String result = "";
        try {
            httpClient.executeMethod(method);
            result = new String(method.getResponseBody(), codePage);
        } catch (final Exception e) {
            throw e;
        } finally {
            method.releaseConnection();
        }
        return result;
    }



    /**
     * 组装http请求参数
     *
     * @param
     * @param
     * @return
     */
    private  static NameValuePair[] assembleRequestParams(Map<String, String> data) {
        final List<NameValuePair> nameValueList = new ArrayList<>(data.size());
        data.forEach((k, v) -> {
            nameValueList.add(new NameValuePair(k, v));
        });
        return nameValueList.toArray(new NameValuePair[nameValueList.size()]);
    }

    public static Map<String,String> stateMap = new HashMap<>();
    static {
        stateMap.put("0","在途中");
        stateMap.put("1","已揽收");
        stateMap.put("2","疑难");
        stateMap.put("3","已签收");

    }



}

