package com.service.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.service.domain.LogisticsAddrNodeInfo;
import com.service.repository.LogisAddtNodeInfoRepository;
import com.service.util.*;
import com.service.GetUrl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Encoder;
import sun.rmi.log.LogOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @ClassName SfInterface
 * @Description 顺丰下单实现类(报文修改务必慎重)
 * @author 于霆霖
 * @Date 2016年12月2日 下午3:17:34    2017/05调整
 * @version 1.0.0
 */
@Controller
@RequestMapping("/sf-joint")
public class SfJointController  {

    private static Log logger  = LogFactory.getLog(SfJointController.class.getName());

    public LogisAddtNodeInfoRepository getLogisAddtNodeInfoRepository() {
        return logisAddtNodeInfoRepository;
    }

    @Inject
    private LogisAddtNodeInfoRepository logisAddtNodeInfoRepository;

    public void setLogisAddtNodeInfoRepository(LogisAddtNodeInfoRepository logisAddtNodeInfoRepository) {
        this.logisAddtNodeInfoRepository = logisAddtNodeInfoRepository;
    }

    //公共接口   临时开发环境
    public static final String SfCreateOrderAddr = "";
    //正式地址
//    public static final String SfCreateOrderAddr = "http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService";
    //密钥
    public static final String checkword = "";
    //校验字段
    //   public static final String checkword = "r4BweRZIrNY1M6piJxg2tXQAh3L0QPiW";
    //接入编码
    public static final String BSPdevelop = "";
    //    public static final String BSPdevelop = "0102480397";
    //顺丰分配账户
    public static final String custid ="";
    //代收货款卡号
    public static final String CODCODE ="";

    //业务类型   1.顺丰次日2.顺丰隔日5.顺丰次晨	6.顺丰即日 37.云仓专配次日38.打印云仓专配隔日；
    public static final String expressType = "6";
    //包裹数，一个包裹对应一个运单号， 如果是大于 1 个包裹，则返回则按照 子母件的方式返回母运单号和子运单 号
    public static final int parcelQuantity= 1;
    //付款方式： 1:寄方付 报文中传“月结卡号”； 面单打印   寄付月结； 2:收方付 报文中不传”月结卡号“ 面单打印：到付；
    public static final String payMethod="1";
    //条形码生成地址(包括运单号及订单号，暂时为win下localhost目录，布置到生产环境之后应使用linux目录 )
    public static String barcodePath = "/opt/image/EMS/";
    //public static String barcode_path = "/workspace/Barcode/SF/";


//    /**
//     *测试    顺丰路由查询
//     */
//    @RequestMapping("test-sf-acceptAddr")
//    @ResponseBody
//    public static String TestSFacceptAddr(){
//
//
//          String rep = SfAcceptAddr(req);
//        return req;
//    }


    /**
     *顺丰路由推送
     */
    @RequestMapping(value = "/sf-acceptAddr", method = RequestMethod.POST)
    @ResponseBody
    public String SfAcceptAddr(@RequestBody String acceptAddrMail, HttpServletRequest request) throws UnsupportedEncodingException {
//        acceptAddrMail = "<?xml version='1.0' encoding='UTF-8'?><Request service=\"RoutePushService\" lang=\"zh-CN\"><Body><WaybillRoute id=\"5348054\" mailno=\"444824538316\" orderid=\"1000013814152606802191\" acceptTime=\"2016-12-23 14:36:36\" acceptAddress=\"深圳市\" remark=\"上门收件\" opCode=\"50\"/></Body></Request>";
        logger.info("acceptAddrMail===========" + acceptAddrMail);

        //返回节点数据处理
        String data = StringUtils.substringBetween(acceptAddrMail, "<?xml version='1.0' encoding='UTF-8'?><Request service=\"RoutePushService\" lang=\"zh-CN\"><Body><WaybillRoute ", "/></Body></Request>");
        logger.info("data===========" + data);

        String dataArray[] = data.split(" ");
        logger.info(dataArray.toString());

        String mailno = StringUtils.substringAfter(dataArray[1], "mailno=");
        logger.info("mailno" + mailno);
        String orderid = StringUtils.substringAfter(dataArray[2], "orderid=");
        logger.info("orderid" + orderid);
        String acceptTime = StringUtils.substringAfter(dataArray[3], "acceptTime=") + " " + dataArray[4];
        acceptTime = acceptTime.substring(1, acceptTime.length() - 1);
        logger.info("acceptTime" + acceptTime);
        String acceptAddress = StringUtils.substringAfter(dataArray[5], "acceptAddress=");
        logger.info("acceptAddress" + acceptAddress);
        String remark = StringUtils.substringAfter(dataArray[6], "remark=");
        logger.info("remark" + remark);
        String opCode = StringUtils.substringAfter(dataArray[7], "opCode=");
        logger.info("opCode" + opCode);


        //路由节点信息持久化
        LogisticsAddrNodeInfo logisticsAddrNodeInfo = new LogisticsAddrNodeInfo();
        logisticsAddrNodeInfo.setMailNo(mailno);
        logisticsAddrNodeInfo.setOccurTime(YMDTools.strToDate(acceptTime));
//      logisticsAddrNodeInfo.setAction("aaa");
        logisticsAddrNodeInfo.setRemark(remark);
        logisticsAddrNodeInfo.setStation(acceptAddress);
        logisticsAddrNodeInfo.setTradeSn(orderid);
        logisticsAddrNodeInfo.setCompany("sf");

        logger.info("logisticsAddrNodeInfo===" + JacksonHelper.toJSON(logisticsAddrNodeInfo));
        logisAddtNodeInfoRepository.save(logisticsAddrNodeInfo);

        //成功回返
        StringBuffer sb = new StringBuffer();
        sb.append("<Response service='RoutePushService'>");
        sb.append("<Head>OK</Head>");
        sb.append("</Response>");

        return sb.toString();
    }

    /**
     *测试    顺丰路由查询
     */
    @RequestMapping("test-sf-requestAddr")
    @ResponseBody
    public static String TestSfRequestAddr(){
        String req = SfRequestAddr("613705703037");
        return req;
    }


    /**
     *顺丰路由查询
     */
    @RequestMapping("sf-requestAddr")
    @ResponseBody
    public static String SfRequestAddr(String billNo){

        StringBuffer sb = new StringBuffer();
        sb.append("<Request service='RouteService' lang='zh-CN'>");
        sb.append("<Head>"+BSPdevelop+"</Head>");
        sb.append("<Body>");
        sb.append("<RouteRequest  ");
        sb.append("tracking_type='1' ");
        sb.append("method_type='1' ")
                .append(" tracking_number")
                .append("=")
                .append("'")
                .append(billNo)
                .append("' ")
                .append("/>")
                .append("</Body>")
                .append("</Request>");

        logger.info("顺丰路由查询报文"+sb.toString());
        String rep = null;
        try {
            rep = SfReq(sb.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.info("顺丰路由查询响应response"+rep);

        //返回节点数据处理
        String data = StringUtils.substringBetween(rep, "<RouteResponse mailno="+billNo+"> ","</RouteResponse>");

        return data;
    }


    /**
     *测试    顺丰下单假数据 (暂且直接返回页面)
     */
    @SuppressWarnings({ "unused", "unused" })
    @RequestMapping("test-sf-checkticket")
    @ResponseBody
    public static ModelAndView TestSfCheckTicket(String orderId){

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("orderId", orderId);

        hashMap.put("j_company", "象牙山国际旅游集团");
        hashMap.put("j_province", "辽宁省");
        hashMap.put("j_city", "铁岭市");
        hashMap.put("j_county", "三十里铺");
        hashMap.put("j_address", "赵家屯");
        hashMap.put("j_contact", "赵四");
        hashMap.put("j_tel", "15942425969");

        hashMap.put("d_province", "浙江省");
        hashMap.put("d_city", "杭州");
        hashMap.put("d_county", "阿里巴巴园区");
        hashMap.put("d_address", "总裁楼");
        hashMap.put("d_contact", "马云Daddy");
        hashMap.put("d_Tel", "13149869087");
        hashMap.put("cargoName", "路易威登");
//		hashMap.put("cod_value", "2");
//		hashMap.put("insure_value", "30");

        List<HashMap<String, String>> info = new ArrayList<>();
        info.add(hashMap);

        String printData = JSONTool.convertCollection2Json(info);

        HashMap<String,String> sf_rep_hashMap = SfCreatOrder(printData);

        String sf_rep = JSONTool.convertCollection2Json(sf_rep_hashMap);

        //重复下单情况处理
        if (sf_rep == "") {

        }

        /**
         * 顺丰响应处理
         */
        int index = 0;
        String mailno = "mailno";
        String mailNoo_master = "";
        String mailNoo_slave = "";
        String transferCenterCode = "destcode";
        String destcode = "";
        index = sf_rep.indexOf("mailno");
        mailNoo_master =  sf_rep.substring(sf_rep.indexOf("mailno")+"mailno".length()+2, sf_rep.indexOf("mailno")+mailno.length()+2+12);
        if (parcelQuantity == 2) {
            mailNoo_slave = sf_rep.substring(sf_rep.indexOf("mailno")+mailno.length()+2+13,sf_rep.indexOf("mailno")+mailno.length()+2+13+12);
        }
        destcode = sf_rep.substring(sf_rep.indexOf("destcode")+transferCenterCode.length()+2, sf_rep.indexOf("destcode")+transferCenterCode.length()+2+3);

        logger.info("mailNoo_master===================="+mailNoo_master);
        if (parcelQuantity == 2) {
            logger.info("mailNoo_slave====================="+mailNoo_slave);
        }
        logger.info("destCode=================="+destcode);


        HashMap<String,String> return_hashMap = new HashMap<>();
        return_hashMap.put("mailNoo_master",mailNoo_master);
        if (CommTool.checkStr(mailNoo_slave)){
            return_hashMap.put("mailNoo_slave",mailNoo_slave);
        }
        return_hashMap.put("destcode",destcode);


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("j_company", "象牙山国际旅游集团");
        modelAndView.addObject("j_province", "辽宁省");
        modelAndView.addObject("j_city", "铁岭市");
        modelAndView.addObject("j_county", "三十里铺");
        modelAndView.addObject("j_address", "赵家屯");
        modelAndView.addObject("j_contact", "赵四");
        modelAndView.addObject("j_tel", "15942425969");
        modelAndView.addObject("d_province", "浙江省");
        modelAndView.addObject("d_city", "杭州");
        modelAndView.addObject("d_county", "阿里巴巴园区");
        modelAndView.addObject("d_address", "总裁楼");
        modelAndView.addObject("d_contact", "马云Daddy");
        modelAndView.addObject("d_Tel", "13149869087");
        modelAndView.addObject("cargoName", "路易威登");
        modelAndView.addObject("destCode", destcode);
        modelAndView.addObject("mailNoo_master", mailNoo_master);
        if (parcelQuantity == 2) {
            modelAndView.addObject("mailNoo_slave",mailNoo_slave);
        }

        //面单业务类型判断  1.顺丰次日2.顺丰隔日5.顺丰次晨	6.顺丰即日 37.云仓专配次日38.打印云仓专配隔日；v
        if(expressType.equals("1")){
            modelAndView.addObject("express_type", "顺丰次日");
        }else if(expressType.equals("2")){
            modelAndView.addObject("express_type", "顺丰隔日");
        }else if(expressType.equals("5")){
            modelAndView.addObject("express_type", "顺丰次晨");
        }else if(expressType.equals("6")){
            modelAndView.addObject("express_type", "顺丰即日");
        }else if(expressType.equals("37")){
            modelAndView.addObject("express_type", "云仓专配次日");
        }if(expressType.equals("38")){
            modelAndView.addObject("express_type", "云仓专配隔日");
        }

        //代收货款
        HashMap<String, Object> hashMap_verify  = JSONTool.convertJson2Collection(printData, HashMap.class).get(0);
        if (hashMap_verify.containsKey("cod_value")) {
            modelAndView.addObject("cod_cusId", custid);
            modelAndView.addObject("cod_value", "2");
        }
        modelAndView.addObject("cusId", custid);

        //保价与面单打印“声明价值”
        if (hashMap_verify.containsKey("insure_value")) {
            logger.info("insure_value============="+hashMap_verify.get("insure_value").toString());
            modelAndView.addObject("insure_value", hashMap_verify.get("insure_value").toString());
        }

        if (payMethod.equals("2")) {
            modelAndView.addObject("pay_method", "到付");
        }else if (payMethod.equals("1")) {
            modelAndView.addObject("pay_method", "寄付月结");
        }

        //为这些单号生成条形码
        Barcode128c.getCode128CPicture(mailNoo_master, barcodePath+"mailNoMaster_"+mailNoo_master+".jpg");

        if (parcelQuantity == 2) {
            Barcode128c.getCode128CPicture(mailNoo_slave, barcodePath+"mailNoSlave_"+mailNoo_slave+".jpg");
        }


        modelAndView.addObject("staticUrl", GetUrl.STATICURL);
        if (parcelQuantity == 1) {
            modelAndView.setViewName("/SF_ticket");
        }else if (parcelQuantity == 2) {
            modelAndView.setViewName("/SF_ticket_double");
        }
        return modelAndView;
    }

    /**
     *测试    顺丰下单假数据 (暂且直接返回页面)
     */
    @SuppressWarnings({ "unused", "unused" })
    @RequestMapping("test-sf-creatOrder")
    @ResponseBody
    public static String TestSfCreatOrder(String orderId){

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("orderId", orderId);

        hashMap.put("j_company", "象牙山国际旅游集团");
        hashMap.put("j_province", "辽宁省");
        hashMap.put("j_city", "铁岭市");
        hashMap.put("j_county", "三十里铺");
        hashMap.put("j_address", "赵家屯");
        hashMap.put("j_contact", "赵四");
        hashMap.put("j_tel", "15942425969");

        hashMap.put("d_province", "浙江省");
        hashMap.put("d_city", "杭州");
        hashMap.put("d_county", "阿里巴巴园区");
        hashMap.put("d_address", "总裁楼");
        hashMap.put("d_contact", "马云Daddy");
        hashMap.put("d_Tel", "13149869087");
        hashMap.put("cargoName", "路易威登");
//		hashMap.put("cod_value", "2");
//		hashMap.put("insure_value", "30");

        List<HashMap<String, String>> info = new ArrayList<>();
        info.add(hashMap);

        String printData = JSONTool.convertCollection2Json(info);

        HashMap<String,String> sf_rep_hashMap = SfCreatOrder(printData);

        String sf_rep = JSONTool.convertCollection2Json(sf_rep_hashMap);

        //重复下单情况处理
        if (sf_rep == "") {

        }

        return sf_rep;

    }

    /**
     * 顺丰下单接口
     * @return
     */
    @RequestMapping("sf-creatOrder")
    @ResponseBody
    public static HashMap<String, String> SfCreatOrder(String xml) {
        String sf_rep = null;
        try {
            sf_rep = SfReq(SfXmlUnit(xml));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.info("顺丰下单响应response"+sf_rep);

        /**
         * 顺丰响应处理
         */
        int index = 0;
        String mailno = "mailno";
        String mailNoo_master = "";
        String mailNoo_slave = "";
        String transferCenterCode = "destcode";
        String destcode = "";
        index = sf_rep.indexOf("mailno");
        mailNoo_master =  sf_rep.substring(sf_rep.indexOf("mailno")+"mailno".length()+2, sf_rep.indexOf("mailno")+mailno.length()+2+12);
        if (parcelQuantity == 2) {
            mailNoo_slave = sf_rep.substring(sf_rep.indexOf("mailno")+mailno.length()+2+13,sf_rep.indexOf("mailno")+mailno.length()+2+13+12);
        }
        destcode = sf_rep.substring(sf_rep.indexOf("destcode")+transferCenterCode.length()+2, sf_rep.indexOf("destcode")+transferCenterCode.length()+2+3);

        logger.info("mailNoo_master===================="+mailNoo_master);
        if (parcelQuantity == 2) {
            logger.info("mailNoo_slave====================="+mailNoo_slave);
        }
        logger.info("destCode=================="+destcode);


        HashMap<String,String> return_hashMap = new HashMap<>();
        return_hashMap.put("mailNo_master",mailNoo_master);
        if (CommTool.checkStr(mailNoo_slave)){
            return_hashMap.put("mailNo_slave",mailNoo_slave);
        }
        return_hashMap.put("destcode",destcode);

//        String returnHashmap = JSONTool.convertCollection2Json(return_hashMap);

        return return_hashMap;

    }

    /**
     *
     * @Title:
     * @Description: TODO[顺丰报文组成]
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("sf-xmlUnit")
    @ResponseBody
    public static String SfXmlUnit(
            @RequestParam(value="xml",required=true)String xml
    ){
        //解析寄件人收件人信息报文
        HashMap<String, String> data = JSONTool.convertJson2List(xml, HashMap.class).get(0);

        String orderId = data.get("orderId").toString();
        String d_contact = data.get("d_contact").toString();
        String d_Mobile = data.get("d_Tel").toString();
        String d_province = data.get("d_province").toString();
        String d_city = data.get("d_city").toString();
        String d_county = data.get("d_county").toString();
        String d_address = data.get("d_address").toString();
        String cargoName = data.get("cargoName").toString();
        String j_province = data.get("j_province").toString();
        String j_company = data.get("j_company").toString();
        String j_city = data.get("j_city").toString();
        String j_county = data.get("j_county").toString();
        String j_address = data.get("j_address").toString();
        String j_contact = data.get("j_contact").toString();
        String j_tel = data.get("j_tel").toString();

        String cod_value = "";
        if (data.containsKey("cod_value")) {
            cod_value = data.get("cod_value").toString();
        }
        String insure_value = "";
        if (data.containsKey("insure_value")) {
            insure_value = data.get("insure_value").toString();
        }


        /**
         * 报文order
         */
        StringBuffer sb = new StringBuffer();
        sb.append("<Request service='OrderService' lang='zh-CN'>");
        sb.append("<Head>"+BSPdevelop+"</Head>");
        sb.append("<Body>");
        sb.append("<Order ");
        sb.append("orderid")
                .append("=")
                .append("'")
                .append(orderId)
                .append("' ");
        sb.append("is_gen_bill_no")
                .append("=")
                .append("'")
                .append("1")
                .append("' ")
                .append("j_company")
                .append("=")
                .append("'")
                .append(j_company)
                .append("' ")
                .append("j_province")
                .append("=")
                .append("'")
                .append(j_province)
                .append("' ")
                .append("j_city")
                .append("=")
                .append("'")
                .append(j_city)
                .append("' ")
                .append("j_county")
                .append("=")
                .append("'")
                .append(j_county)
                .append("' ")
                .append("j_address")
                .append("=")
                .append("'")
                .append(j_address)
                .append("' ")
                .append("j_contact")
                .append("=")
                .append("'")
                .append(j_contact)
                .append("' ")
                .append("j_tel")
                .append("=")
                .append("'")
                .append(j_tel)
                .append("' ")
                .append("d_company")
                .append("=")
                .append("'")
                .append("中国")
                .append("' ")
                .append("d_province")
                .append("=")
                .append("'")
                .append(d_province)
                .append("' ")
                .append("d_city")
                .append("=")
                .append("'")
                .append(d_city)
                .append("' ")
                .append("d_county")
                .append("=")
                .append("'")
                .append(d_county)
                .append("' ")
                .append("d_address")
                .append("=")
                .append("'")
                .append(d_address)
                .append("' ")
                .append("d_contact")
                .append("=")
                .append("'")
                .append(d_contact)
                .append("' ")
                .append("d_tel")
                .append("=")
                .append("'")
                .append(d_Mobile)
                .append("' ")
                .append("d_mobile")
                .append("=")
                .append("'")
                .append(d_Mobile)
                .append("' ")
                .append("express_type")
                .append("=")
                .append("'")
                .append(expressType)
                .append("' ")
                .append("pay_method")
                .append("=")
                .append("'")
                .append(payMethod)
                .append("' ");

        if(payMethod == "1"){
            sb.append("custid")
                    .append("=")
                    .append("'")
                    .append(custid)
                    .append("' ");
        }

        sb.append("parcel_quantity")
                .append("=")
                .append("'")
                .append(parcelQuantity)
                .append("'")
                .append(">");

        sb.append("<Cargo ")
                .append("name")
                .append("=")
                .append("'")
                .append(cargoName)
                .append("'")
                .append("></Cargo>");

        if (data.containsKey("cod_value")) {
            logger.info("==================");
            sb.append("<AddedService ")
                    .append("name='COD' ")
                    .append("value")
                    .append("=")
                    .append("'")
                    .append(cod_value)
                    .append("' ")
                    .append("value1")
                    .append("=")
                    .append("'")
                    .append(custid)
                    .append("'")
                    .append("></AddedService>");
        };

        if (data.containsKey("insure_value")) {
            sb.append("<AddedService ")
                    .append("name='INSURE' ")
                    .append("value")
                    .append("=")
                    .append("'")
                    .append(insure_value)
                    .append("' ")
                    .append("></AddedService>");
        }
        sb.append("</Order>");
        sb.append("</Body>");
        sb.append("</Request>");
        logger.info("即将Map转成Xml, Xml：" + sb.toString());

        return sb.toString();
    }

    /**
     * 顺丰请求发送工具类
     * @param xml
     * @return
     * @throws Exception
     */
    public static String SfReq(String xml) throws Exception {

        HttpClient client = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(SfCreateOrderAddr);

        String verifyCode = md5EncryptAndBase64(xml + checkword);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("xml", xml));
        parameters.add(new BasicNameValuePair("verifyCode", verifyCode));

        httpPost.setEntity(new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8")));

        HttpResponse httpResponse = client.execute(httpPost);

        logger.info(httpResponse.getStatusLine().getStatusCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
        StringBuffer sb = new StringBuffer();
        String content = null;
        while ((content = reader.readLine()) != null) {
            sb.append(content);
        }

        String res = sb.toString();

        return res;
    }

    /**
     * 加密
     * @param str
     * @return
     */
    public static String md5EncryptAndBase64(String str) {
        return encodeBase64(md5Encrypt(str));
    }

    private static byte[] md5Encrypt(String encryptStr) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encryptStr.getBytes("utf8"));
            return md5.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeBase64(byte[] b) {
        @SuppressWarnings("restriction")
        BASE64Encoder base64Encode = new BASE64Encoder();
        @SuppressWarnings("restriction")
        String str = base64Encode.encode(b);
        return str;
    }
    /**
     * XML转map   非常好用
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
}
