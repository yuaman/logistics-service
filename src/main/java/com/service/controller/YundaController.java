package com.service.controller;

import com.xhfxw.logistics.GetUrl;
import com.xhfxw.logistics.domain.LogisticsAddrNodeInfo;
import com.xhfxw.logistics.repository.LogisAddtNodeInfoRepository;
import com.xhfxw.logistics.util.*;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import org.jbarcode.util.ImageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

//import com.xhfxw.logistics.util.BarcodeUtil;

import javax.inject.Inject;

/**
 *
 * @ClassName SfInterface
 * @Description 韵达下单实现类(报文修改务必慎重)
 * @author 于霆霖
 * @Date 2017年05月11日 下午3:17:34
 * @version 1.0.0
 */
@Controller
@RequestMapping("/yunda-joint")
public class YundaController  {

    private static Log logger  = LogFactory.getLog(YundaController.class);

    @Inject
    private LogisAddtNodeInfoRepository logisAddtNodeInfoRepository;

    //公共接口   临时开发环境
    public static final String YUNDACreateOrderAddr = "";
    //路由查询地址
    public static final String YUNDAqueryAddr = "";
    //公司代号
    public static final String partnerid = "";
    //测试账号
    public static final String account = "";
    //测试密码
    public static final String password = "";
    //版本号
    public static final String version = "";

    //密钥
    public static final String checkword = "";
    //接入编码
    public static final String BSPdevelop = "";
    //顺丰分配账户
    public static final String custid ="";
    //代收货款卡号
    public static final String CODCODE ="";

    //数据类型
    public static final String CONTENT_TYPE_JSON = "";
    public static final String CONTENT_TYPE_XML = "";
    public static final String CONTENT_TYPE_FORM = "";

    //业务类型   1.顺丰次日2.顺丰隔日5.顺丰次晨	6.顺丰即日 37.云仓专配次日38.打印云仓专配隔日；
    public static final String express_type = "1";
    //包裹数，一个包裹对应一个运单号， 如果是大于 1 个包裹，则返回则按照 子母件的方式返回母运单号和子运单 号
    public static final int parcel_quantity= 1;
    //付款方式： 1:寄方付 报文中传“月结卡号”； 面单打印   寄付月结； 2:收方付 报文中不传”月结卡号“ 面单打印：到付；
    public static final String pay_method="1";
    //条形码生成地址(包括运单号及订单号，暂时为win下localhost目录，布置到生产环境之后应使用linux目录 )
    public static String barcodePath = "/opt/image/EMS/";
    // public static String barcode_path = "/workspace/Barcode/YUNDA/";

    /**
     * 韵达接受地址
     * @param addr_info
     * @return
     */
    @RequestMapping(value = "/yunda-acceptAddr", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String YUNDAacceptAddr(@RequestParam(value = "request", required = true) String request,
                                  @RequestParam(value = "version", required = true) String version,
                                  @RequestParam(value = "partnerid", required = true) String partnerid,
                                  @RequestParam(value = "validation", required = true) String validation,
                                  @RequestParam(value = "xmldata", required = true) String xmldata) throws UnsupportedEncodingException {
//   	 addr_info = "<order><time>2016-05-03 12:00:00</time><city>黑龙江</city><facilityName>黑龙江公司</facilityName><action>GOT</action><desc>在黑鸭子山进行揽件扫描</desc><mailNo>1200578945607</mailNo></order><order><time>2016-05-03 12:00:00</time><city>黑龙江</city><facilityName>黑龙江公司</facilityName><action>GOT</action><desc>在黑鸭子山进行揽件扫描</desc><mailNo>1200578945607</mailNo></order>";

        logger.info("xmldata==========="+xmldata);

        xmldata = URLDecoder.decode(xmldata,"utf-8");

        String addr_info = com.xhfxw.logistics.util.Base64.getFromBase64(xmldata);

        logger.info("addr_info_before============" + addr_info);
 //       String addrInfo[] = addr_info.split("</order>");
  //      logger.info("addrInfo====" + addrInfo);

//        for (String addr : addrInfo) {
//            addr = addr + "</order>";
            logger.info("addr=========" + addr_info);
            Map<String, String> map = Map2Xml.getXML(addr_info);
            logger.info("map========" + map);
            //路由节点信息持久化
            LogisticsAddrNodeInfo logisticsAddrNodeInfo = new LogisticsAddrNodeInfo();
            logisticsAddrNodeInfo.setMailNo(map.get("mailNo").toString());
            logisticsAddrNodeInfo.setOccurTime(YMDTools.strToDate(map.get("time").toString()));
            logisticsAddrNodeInfo.setAction(map.get("action").toString());
            logisticsAddrNodeInfo.setRemark(map.get("desc".toString()));
            logisticsAddrNodeInfo.setStation(map.get("city").toString());
            logisticsAddrNodeInfo.setCompany("yunda");
            //       logisticsAddrNodeInfo.setTradeSn(orderid);

            logger.info("logisticsAddrNodeInfo===" + JacksonHelper.toJSON(logisticsAddrNodeInfo));
            logisAddtNodeInfoRepository.save(logisticsAddrNodeInfo);
//        }

        StringBuffer sb = new StringBuffer();
        sb.append("<response>")
                .append("<result>true</result><")
                .append("remark></remark>")
                .append("</response> ");
        return sb.toString();
    }


    /**
     * 韵达路由查询测试
     * @return
     */
    @RequestMapping("/test-yunda-query")
    @ResponseBody
   public static String TestYundaQuery(){
        String mailNo = "4060002646725";
        String addr_info = YUNDAqeury(mailNo);
        return  addr_info;
   }

    /**
     * 韵达路由查询
     */
    @RequestMapping("/yunda-query")
    @ResponseBody
    public static String YUNDAqeury(String mailNo) {

        String addr_info = YUNDAexcuteQuery(YUNDAqueryAddr, mailNo, "N");
        LinkedHashMap<String, String> linkedHashMap = JacksonHelper.fromJSON(addr_info, LinkedHashMap.class);
        logger.info("韵达路由查询响应"+linkedHashMap);
        if (linkedHashMap.containsKey("steps")) {
            String steps = linkedHashMap.get("steps").toString();
            logger.info(steps);
            return steps;
        }
        return addr_info;
    }

    /**
     *测试    韵达查看面单 (暂且直接返回页面)
     * @throws UnsupportedEncodingException
     *
     *
     */
    @SuppressWarnings({ "unused", "unused" })
    @RequestMapping("test-yunda-checkTicket")
    public  ModelAndView TestYUNDACheckTiket() throws UnsupportedEncodingException{
        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_id", GeneralPkCode.getUuId());


        hashMap.put("sender_name", "赵四");
        hashMap.put("sender_city", "上海市，上海市，青浦区");
        hashMap.put("sender_address", "上海市，上海市，青浦区，赵家屯");
        hashMap.put("sender_mobile", "15942425969");

        hashMap.put("receiver_name", "马云");
        hashMap.put("receiver_city", "江苏省，徐州市，新沂市");
        hashMap.put("receiver_address", "江苏省，徐州市，新沂市，阿里巴巴总裁楼");
        hashMap.put("receiver_mobile", "15942425969");

        hashMap.put("cus_area1","产品名称：蒂凡尼的早餐");

        String data = JSONTool.convertCollection2Json(hashMap);

        String result = createOrder(data);

        logger.info("韵达下单回调"+result);
        //回调处理
        Map<String, String> map = Map2Xml.getXML(result);
        logger.info(""+JSONTool.convertCollection2Json(map));

        String mailNo = map.get("mail_no").toString();
        String status = map.get("status").toString();

        if (status.equals("1")) {
            String pdf_info = map.get("pdf_info");
            pdf_info = URLDecoder.decode(pdf_info, "utf-8");
            logger.info("解码之后的pdf_info"+pdf_info);
            pdf_info = pdf_info.substring(1,pdf_info.length()-13)+"]";
            @SuppressWarnings("unchecked")
            HashMap<String, Object> pdf_info_map =   JSONTool.convertJson2Collection(pdf_info, HashMap.class).get(0);
            logger.info("解析后的pdf_info_map"+pdf_info_map);

            String position = pdf_info_map.get("position").toString();
            String position_no = pdf_info_map.get("position_no").toString();
            String package_wdjc = pdf_info_map.get("package_wdjc").toString();

            logger.info("mailNo："+mailNo);
            logger.info("position："+position);
            logger.info("position_no："+position_no);
            logger.info("package_wdjc："+package_wdjc);

            modelAndView.addObject("mailNo", mailNo);
            modelAndView.addObject("position", position);
            modelAndView.addObject("position_no", position_no);
            modelAndView.addObject("package_wdjc",package_wdjc);
        }
        modelAndView.addObject("staticUrl", GetUrl.STATICURL);
        modelAndView.setViewName("/YUNDAticket");

//        HashMap< String, String> data_useagain = JSONTool.convertJson2List(data, HashMap.class).get(0);
        String order_id = hashMap.get("order_id").toString();
        String sender_name = hashMap.get("sender_name").toString();
        String sender_city = hashMap.get("sender_city").toString();
        String sender_address = hashMap.get("sender_address").toString();
        String sender_mobile = hashMap.get("sender_mobile").toString();
        String receiver_name = hashMap.get("receiver_name").toString();
        String receiver_city = hashMap.get("receiver_city").toString();
        String receiver_address = hashMap.get("receiver_address").toString();
        String receiver_mobile = hashMap.get("receiver_mobile").toString();
        String cus_area1 = hashMap.get("cus_area1").toString();

        modelAndView.addObject("order_id",order_id);
        modelAndView.addObject("sender_name",sender_name);
        modelAndView.addObject("sender_city",sender_city);
        modelAndView.addObject("sender_address",sender_address);
        modelAndView.addObject("sender_mobile",sender_mobile);
        modelAndView.addObject("receiver_name",receiver_name);
        modelAndView.addObject("receiver_city",receiver_city);
        modelAndView.addObject("receiver_address",receiver_address);
        modelAndView.addObject("receiver_mobile",receiver_mobile);
        modelAndView.addObject("cus_area1",cus_area1);

//        String path = BarcodeUtil.createBarCode(barcodePath, mailNo,
//                ImageUtil.JPEG);

//        logger.info(path);

        return modelAndView;
    }

    /**
     *测试    韵达下单假数据 (暂且直接返回页面)
     * @throws UnsupportedEncodingException
     */

    @SuppressWarnings({ "unused", "unused" })
    @RequestMapping("test-yunda-creatOrder")
    @ResponseBody
    public  String TestYUNDACreatOrder() throws UnsupportedEncodingException{
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_id", GeneralPkCode.getUuId());

        hashMap.put("sender_name", "赵四");
        hashMap.put("sender_city", "上海市，上海市，青浦区");
        hashMap.put("sender_address", "上海市，上海市，青浦区，赵家屯");
        hashMap.put("sender_mobile", "15942425969");

        hashMap.put("receiver_name", "马云");
        hashMap.put("receiver_city", "江苏省，徐州市，新沂市");
        hashMap.put("receiver_address", "江苏省，徐州市，新沂市，阿里巴巴总裁楼");
        hashMap.put("receiver_mobile", "15942425969");

        hashMap.put("cus_area1","产品名称：蒂凡尼的早餐");

        String data = JSONTool.convertCollection2Json(hashMap);

        String mailNo = createOrder(data);


        return mailNo;
    }

    /**
     * 正式下单
     * @param order_data_map
     * @return
     */
    @RequestMapping("/yunda-creatOrder")
    @ResponseBody
    public static String createOrder(String order_data_map) {
        String xml = YundaXmlUnit(order_data_map);
        String result = null;
        try {
            String data = security(partnerid, password, xml);
            data += "&version=" + version + "&request=data";
            result = executePost(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("韵达下单回调"+result);
        //回调处理
        Map<String, String> map = Map2Xml.getXML(result);
        logger.info(""+JSONTool.convertCollection2Json(map));

        String mailNo = map.get("mail_no").toString();
        String status = map.get("status").toString();

        return mailNo;
    }

    /**
     * 韵达xml生成
     * @return
     */
    public static String YundaXmlUnit(String order_data_map) {
        HashMap<String, String> hashMap = JSONTool.convertJson2List(order_data_map, HashMap.class).get(0);
        String order_id = hashMap.get("order_id").toString();
        String sender_name = hashMap.get("sender_name").toString();
        String sender_city = hashMap.get("sender_city").toString();
        String sender_address = hashMap.get("sender_address").toString();
        String sender_mobile = hashMap.get("sender_mobile").toString();
        String receiver_name = hashMap.get("receiver_name").toString();
        String receiver_city = hashMap.get("receiver_city").toString();
        String receiver_address = hashMap.get("receiver_address").toString();
        String receiver_mobile = hashMap.get("receiver_mobile").toString();
        String cus_area1 = hashMap.get("cus_area1").toString();

        String xmldata =
                " <orders> "
                        + " <order> "
                        + "	<order_serial_no>"+GeneralPkCode.getUuId()+"</order_serial_no> "
                        + "	<khddh>"+order_id+"</khddh> "
                        + "	<sender> "
                        + "		<name>"+sender_name+"</name> "
                        + "		<city>"+sender_city+"</city> "
                        + "		<address>"+sender_address+"</address> "
                        + "		<mobile>"+sender_mobile+"</mobile> "
                        + "	</sender> "
                        + "	<receiver> "
                        + "		<name>"+receiver_name+"</name> "
                        + "		<city>"+receiver_city+"</city> "
                        + "		<address>"+receiver_address+"</address>"
                        + "		<mobile>"+receiver_mobile+"</mobile> "
                        + "	</receiver> "
                        + "	<cus_area1>"+cus_area1+"</cus_area1> "
                        + " </order>"
                        + " </orders>";
        return xmldata;
    }

    /**
     * http发送执行
     * @param xml
     */
    @RequestMapping("YundaExcuteQuery")
    @ResponseBody
    public static String  YUNDAexcuteQuery(String addr,String base64_xml,@RequestParam(value="isRequest",required=false)String isRequest) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(addr+URLEncoder.encode(base64_xml,"utf-8"));
            if(isRequest != ""){
//            	logger.info("isRequest");
//            	httpget.setHeader("version", Version);
//            	httpget.setHeader("authenticate",authenticate);
//    		}
                logger.info("executing request " + httpget.getURI());
                // 执行get请求.
                CloseableHttpResponse response = httpclient.execute(httpget);
                try {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();

                    logger.info("--------------------------------------");
                    // 打印响应状态
                    logger.info(response.getStatusLine());
                    if (entity != null) {
                        // 打印响应内容长度
                        logger.info("Response content length: " + entity.getContentLength());
                        //对响应内容进行解密并解码
                        String decode_rep = URLDecoder.decode(EntityUtils.toString(entity), "utf-8");

                        logger.info("解码后的返回值："+decode_rep);

                        return decode_rep;
                    }
                    logger.info("------------------------------------");
                } finally {
                /*response.close();*/
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "fail";
    }

    /**
     * Post方式请求
     * @param url 请求地址
     * @param data 参数
     * @param contentType 数据类型
     * 		  1 CONTENT_TYPE_FORM
     * 		  2 CONTENT_TYPE_XML
     * 		  3 CONTENT_TYPE_JSON
     * @return xml数据格式
     * @throws Exception
     */
    public static String post(String url, String data, String contentType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        URL getUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)getUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setConnectTimeout(10000);
        connection.connect();
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        out.write(data);
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String line = "";
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        return buffer.toString();

    }

    /***
     *
     * @param
     * @param data
     * @return
     * @throws Exception
     */
    public static String executePost(String data) throws Exception {
        return post(YUNDACreateOrderAddr, data, CONTENT_TYPE_FORM);
    }

    /**
     * 针对参数进行加密
     * @param partnerid
     * @param password
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */

    @SuppressWarnings("static-access")
    public static String security(String partnerid, String password, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        data = new String(new Base64().encode(data.getBytes()));

        // 签名内容 = base64(data) + partnerid + password;
        String validation = data + partnerid + password;
        validation = md5(validation);

        partnerid = URLEncoder.encode(partnerid, "UTF-8");
        data = URLEncoder.encode(data, "UTF-8");
        validation = URLEncoder.encode(validation, "UTF-8");

        return new StringBuffer().append("partnerid").append("=")
                .append(partnerid).append("&").append("xmldata").append("=")
                .append(data).append("&").append("validation").append("=").append(validation).toString();
    }

    /**
     * md5加密方法
     * @param source 源字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String source) throws NoSuchAlgorithmException {

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(source.getBytes());
        byte[] tmp = md.digest();
        char[] str = new char[16 * 2];

        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }

        return new String(str);
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

