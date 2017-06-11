package com.service.controller;

import com.service.Constant;
import com.service.domain.LogisticsAddrNodeInfo;
import com.service.repository.LogisAddtNodeInfoRepository;
import com.service.util.*;
import com.service.GetUrl;
import com.service.util.Base64;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.ParseException;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yutinglin on 2017/5/18.
 */

/**
 * EMS物流对接工具类
 *
 * @author 于霆霖
 *         2017/05/03
 */

@Controller
@RequestMapping("/ems-joint")
public class EmsJointController {
    private static Log logger = LogFactory.getLog(EmsJointController.class.getName());

    @Inject
    private LogisAddtNodeInfoRepository logisAddtNodeInfoRepository;

    private static Map<String, String> actionMap = new HashMap<>();
    static {
        actionMap.put("00", "收寄");
        actionMap.put("10", "妥投");
        actionMap.put("20", "未妥投");
        actionMap.put("30", "经转过程中");
        actionMap.put("40", "离开处理中心");
        actionMap.put("41", "到达处理中心");
        actionMap.put("50", "安排投递");
        actionMap.put("51", "正在投递");
        actionMap.put("60", "揽收");
    }

    @Autowired
    static Constant constant;
    //EMS请求单号文档地址
    public static String getBillNumAddr = "";
    //EMS打印信息回传地址
    public static String updatePrintDatasAddr = "";
    //EMS包裹主动查询地址
    public static String queryAddr = "";
    //账号（大客户号）
    public static String sysAccount = "";
    //密码（对接密码）
    public static String passWord = "";
    //对接授权码
    public static String appKey = "";
    //主动查询授权码
    public static String authenticate = "";
    //主动查询授权码
    public static String Version = "";
    //条形码生成地址(包括运单号及订单号，暂时为win下localhost目录，布置到生产环境之后应使用linux目录 )
    public static String barcodePath = "";
    //public static String barcodePath = "/workspace/Barcode/EMS/";

    //EMS请求单号文档地址
//    static String getBillNum_addr = constant.getGetBillNum_addr();
    //EMS打印信息回传地址
//    static String updatePrintDatas_addr = constant.getUpdatePrintDatas_addr();
    //EMS包裹主动查询地址
//    static String query_addr = constant.getQuery_addr();
    //账号（大客户号）
//    static String sysAccount = constant.getSysAccount();
    //密码（对接密码）
//    static String passWord = constant.getPassWord();
    //对接授权码
//    static String appKey = constant.getAppKey();
    //主动查询授权码
//    static String authenticate = constant.getAuthenticate();
    //主动查询授权码
//    static String Version = constant.getVersion();
    //条形码生成地址(包括运单号及订单号，暂时为win下localhost目录，布置到生产环境之后应使用linux目录 )
//    static String barcode_path = constant.getBarcode_path();
    //public static String barcode_path = "/workspace/Barcode/EMS/";

    @RequestMapping("/test-ems-RequestExpressAddr")
    @ResponseBody
    public static String TestEMSrequestExpressAddr() throws NoSuchAlgorithmException {
        String req = EMSrequestExpressAddr("9703802627104");

        return req;
    }


    @RequestMapping("/ems-requestExpressAddr")
    @ResponseBody
    public static String EMSrequestExpressAddr(String mailNo) throws NoSuchAlgorithmException {
        String req = EmsExcuteSending(queryAddr, mailNo, "TRUE");
        logger.info("主动查询信息" + req);

        //数据处理
        /*String json = StringUtils.substringBetween(req, "[", "]");
    	json = "["+json+"]";
    	HashMap<String, String> map = JSONTool.convertJson2List(json, HashMap.class).get(0);


    	String acceptTime = map.get("acceptTime").toString();
    	String acceptAddress = map.get("acceptAddress").toString();
    	String remark = map.get("remark").toString();
    	logger.info("主动请求道德"+map);*/
        return req;
    }

    /**
     * 测试EMS路由推送接口
     */
//    @RequestMapping("/test-ems-accept-express-mail")
//    @ResponseBody
//    public static String TestEMSacceptExpressmail() throws NoSuchAlgorithmException {
//        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
//                + "<listexpressmail>"
//                + "<expressmail>"
//                + "<serialnumber>00000000000000000001</serialnumber>"
//                + "<mailnum>LK434266003CN</mailnum>"
//                + "<procdate>20130702</procdate>"
//                + "<proctime>000100</proctime>"
//                + "<orgfullname>所在地名称</orgfullname>"
//                + "<action>00</action>"
//                + "<description>描述信息</description>"
//                + "<effect>有效、无效</effect>"
//                + "<properdelivery>妥投使用</properdelivery>"
//                + "<notproperdelivery>未妥投使用</notproperdelivery>"
//                + "</expressmail>"
//                + "</listexpressmail>";
//
//        String req = EMSacceptExpressmail(xml);
//
//        logger.info("推送回返信息"+req);
//        return req;
//    }


    /**
     * EMS路由推送接口       该接口是由速递主动发起请求把邮件的收寄、投递、封发、开拆等信息推送到客户方
     */
    @RequestMapping(value = "/ems-acceptExpressmail", method = RequestMethod.POST)
    @ResponseBody
    public String EMSacceptExpressmail(@RequestBody String listexpressmail, HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        logger.info("listexpressmail======" + listexpressmail);

        List<Object> list = getContext(listexpressmail);
        String mailNum = list.get(1).toString();
        String date = list.get(2).toString();
        String time = list.get(3).toString();
        String location = list.get(4).toString();
        String action = list.get(5).toString();

        String actionStr = actionMap.get(action).toString();

        String description = list.get(6).toString();
        String effect = list.get(7).toString();
        if (effect.equals("0")) {
            effect = "有效";
        } else if (effect.equals("1")) {
            effect = "无效";
        }
    	/*String properdelivery = list.get(7).toString();
    	String notproperdelivery = list.get(8).toString();*/

        HashMap<String, String> map = new HashMap();
        map.put("mailNum", mailNum);
        map.put("date", date);
        map.put("time", time);
        map.put("location", location);
        map.put("action", action);
        map.put("description", description);
        map.put("effect", effect);

        //路由节点信息持久化
        LogisticsAddrNodeInfo logisticsAddrNodeInfo = new LogisticsAddrNodeInfo();
        logisticsAddrNodeInfo.setMailNo(mailNum);
        logisticsAddrNodeInfo.setOccurTime(YMDTools.timestampToDate(date + time));
        logisticsAddrNodeInfo.setAction(effect);
        logisticsAddrNodeInfo.setRemark(description);
        logisticsAddrNodeInfo.setStation(location);
        logisticsAddrNodeInfo.setCompany("ems");
        //       logisticsAddrNodeInfo.setTradeSn(orderid);

        logger.info("logisticsAddrNodeInfo===" + JacksonHelper.toJSON(logisticsAddrNodeInfo));
        logisAddtNodeInfoRepository.save(logisticsAddrNodeInfo);

        //回馈
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<response>");
        sb.append("<success>1</success>");
        sb.append("<failmailnums></failmailnums>");
        sb.append("<remark></remark>");
        sb.append("</response>");
        //发送回执
        return sb.toString();
    }

    /**
     * 测试 假数据  查看面单
     */

    @RequestMapping("test-check-ticket")
    public static String TestCheckTicket(Model model) {
        HashMap<String, String> hashMap = new HashMap<>();
        //单号
        hashMap.put("billNo", "9703800134504");
        //订单号
        hashMap.put("orderId", "970380013560");
        //寄件人信息
        hashMap.put("scontactor", "赵四");
        hashMap.put("scustMobile", "13149869087");
        hashMap.put("scustAddr", "辽宁省铁岭市三十里铺象牙山");
        //收件人信息
        hashMap.put("tcontactor", "尼古拉斯");
        hashMap.put("tcustMobile", "18904280777");
        hashMap.put("tcustAddr", "美国纽约市皇后区中央大道88号");
        //商品信息
        hashMap.put("cargoName", "蒂凡尼珠宝");
        hashMap.put("cargoAmount", "3");
        hashMap.put("cargoWeight", "1.5");

        List<HashMap<String, String>> info = new ArrayList<>();
        info.add(hashMap);

        String printData = JSONTool.convertCollection2Json(info);

        ModelAndView modelAndView = checkTicket(printData);
        return "";
    }



    /**
     * 查看面单
     * @RequestParam(value = "printData") String printData,
     */
//    @org.junit.Test
    @SuppressWarnings("unchecked")
    @RequestMapping("/check-ticket")
    public static ModelAndView checkTicket(String printData) {

        ModelAndView modelAndView = new ModelAndView();

//        HashMap<String, String> hashMap = new HashMap<>();
//        //单号
//        hashMap.put("billNo", "9703800134504");
//        //订单号
//        hashMap.put("orderId", "970380013560");
//        //寄件人信息
//        hashMap.put("scontactor", "赵四");
//        hashMap.put("scustMobile", "13149869087");
//        hashMap.put("scustAddr", "辽宁省铁岭市三十里铺象牙山");
//        //收件人信息
//        hashMap.put("tcontactor", "尼古拉斯");
//        hashMap.put("tcustMobile", "18904280777");
//        hashMap.put("tcustAddr", "美国纽约市皇后区中央大道88号");
//        //商品信息
//        hashMap.put("cargoName", "蒂凡尼珠宝");
//        hashMap.put("cargoAmount", "3");
//        hashMap.put("cargoWeight", "1.5");
//
//        List<HashMap<String, String>> info = new ArrayList<>();
//        info.add(hashMap);
//
//        String printData = JSONTool.convertCollection2Json(info);

        //解析回传数据
        HashMap<String, String> data = JSONTool.convertJson2List(printData, HashMap.class).get(0);

        String billno = data.get("billNo").toString();
        String orderId = data.get("orderId").toString();
        String scontactor = data.get("scontactor").toString();
        String scustMobile = data.get("scustMobile").toString();
        String scustAddr = data.get("scustAddr").toString();
        String tcontactor = data.get("tcontactor").toString();
        String tcustMobile = data.get("tcustMobile").toString();
        String tcustAddr = data.get("tcustAddr").toString();
        //收件人所在区截取
        String area = "";
        area = StringUtils.substringBetween(tcustAddr, "市", "区");
        area = area + "区";

        String cargoName = data.get("cargoName").toString();
        String cargoAmount = data.get("cargoAmount").toString();
        String cargoWeight = data.get("cargoWeight").toString();

        //生成订单号条形码
        Barcode128a.getCode128APicture(orderId, barcodePath+"orderId_"+orderId+".jpg");

        modelAndView.addObject("billno", billno);
        modelAndView.addObject("orderId", orderId);
        modelAndView.addObject("scontactor", scontactor);
        modelAndView.addObject("scustMobile", scustMobile);
        modelAndView.addObject("scustAddr", scustAddr);
        modelAndView.addObject("tcontactor", tcontactor);
        modelAndView.addObject("tcustMobile", tcustMobile);
        modelAndView.addObject("tcustAddr", tcustAddr);
        modelAndView.addObject("area", area);
        modelAndView.addObject("cargoName", cargoName);
        modelAndView.addObject("cargoAmount", cargoAmount);
        modelAndView.addObject("cargoWeight", cargoWeight);
        modelAndView.addObject("orderId_barcode_name", "orderId_" + orderId + ".jpg");
        modelAndView.addObject("staticUrl", GetUrl.STATICURL);
        modelAndView.setViewName("EMSticket");
        return modelAndView;
    }

    /**
     * 测试假数据    将详情单打印信息更新到自助服务系统
     */
    @RequestMapping("/test-ems-updatePrintDatas")
    @ResponseBody
    public static String TestEMSupdatePrintDatas(String billno) throws NoSuchAlgorithmException {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("billno", billno);
        hashMap.put("scontactor", "尼古拉斯赵四");
        hashMap.put("scustMobile", "13149869087");
        hashMap.put("scustAddr", "辽宁省铁岭市");
        hashMap.put("tcontactor", "尼古拉斯凯奇");
        hashMap.put("tcustMobile", "18904280777");
        hashMap.put("tcustAddr", "美国洛杉矶");

        List<HashMap<String, String>> info = new ArrayList<>();
        info.add(hashMap);

        String printData = JSONTool.convertCollection2Json(info);

        String rep = EMSupdatePrintDatas(printData);

        return rep;
    }

    /**
     * 将详情单打印信息更新到自助服务系统
     */
    @RequestMapping("ems-updatePrintDatas")
    @ResponseBody
    public static String EMSupdatePrintDatas(@RequestParam(value = "printData", required = true) String printData) throws NoSuchAlgorithmException {
        String base64_xml = Base64.getBase64(EMSupdatePrintDatasXmlUnit(printData));
        String decode_rep = EmsExcuteSending(updatePrintDatasAddr, base64_xml, "");

        //解析xml为map
        List list = getContext(decode_rep);

        logger.info("解析xml" + list);

        //判断是否成功返回单号
        if (list.get(2).equals("E000")) {
            return "UpdatePrintDatas Succeed";
        } else {
            return "UpdatePrintDatas Failed";
        }
    }

    /**
     * @param @return 设定文件
     * @return String    返回类型
     * @throws NoSuchAlgorithmException
     * @throws
     * @Title: EMSCreatOrder
     * @Description: TODO[EMS信息回传报文拼接]
     */
    @RequestMapping("ems-updatePrintDatasXmlUnit")
    @ResponseBody
    public static String EMSupdatePrintDatasXmlUnit(@RequestParam(value = "printData", required = true) String printData
    ) throws NoSuchAlgorithmException {
        //密码处理(MD532位加密)
        String decode_pwd = MD5.MD5_32bit(passWord);

        //解析回传数据
        HashMap<String, String> data = JSONTool.convertJson2List(printData, HashMap.class).get(0);

        String billno = data.get("billno").toString();
        String scontactor = data.get("scontactor").toString();
        String scustMobile = data.get("scustMobile").toString();
        String scustAddr = data.get("scustAddr").toString();
        String tcontactor = data.get("tcontactor").toString();
        String tcustMobile = data.get("tcustMobile").toString();
        String tcustAddr = data.get("tcustAddr").toString();

        /**
         * 请求报文order
         * 账号密码报文等正式下来之后都写死，以便前台调用，业务类型写死，“快递包裹”选9，需传参数仅为单号量
         */
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");//xml标记头
        sb.append("<XMLInfo>");
        sb.append("<sysAccount>" + sysAccount + "</sysAccount>");//账号（大客户号）
        sb.append("<passWord>" + decode_pwd + "</passWord>");//密码（对接密码）
        sb.append("<printKind>2</printKind>");//数据类型  热敏式
        sb.append("<appKey>" + appKey + "</appKey>");//对接授权码

        sb.append("<printDatas>");//邮件数据结构标签开始
        sb.append("<printData>");//单条数据标签开始
        sb.append("<bigAccountDataId>" + GeneralPkCode.getMillies() + "</bigAccountDataId>");//邮件数据唯一标识
        sb.append("<businessType>9</businessType>");//业务类型
        sb.append("<billno>" + billno + "</billno>");//邮件号  必须为13位
        sb.append("<scontactor>" + scontactor + "</scontactor>");//寄件人姓名
        sb.append("<scustMobile>" + scustMobile + "</scustMobile>");//寄件人电话
        sb.append("<scustAddr>" + scustAddr + "</scustAddr>");//寄件人地址（全地址）
        sb.append("<tcontactor>" + tcontactor + "</tcontactor>");//收件人姓名
        sb.append("<tcustMobile>" + tcustMobile + "</tcustMobile>");//收件人电话
        sb.append("<tcustAddr>" + tcustAddr + "</tcustAddr>");//收件人地址
        sb.append("</printData>");//单条数据标签结束
        sb.append("</printDatas>");//邮件数据结构标签结束
        sb.append("</XMLInfo>");//xml标记尾

        return sb.toString();
    }


    /**
     * EMS获取订单号
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/ems-getBillNum")
    @ResponseBody
    public static List<String> EmsGetBillNum(@RequestParam(value = "billNoAmount", required = true) int billNoAmount) throws NoSuchAlgorithmException {

        //将报文进行Base64加密
        String base64_xml = Base64.getBase64(EmsGetBillNumXmlUnit(billNoAmount));
        String decode_rep = EmsExcuteSending(getBillNumAddr, base64_xml, "");

        //解析xml为map
        List list = getContext(decode_rep);

        logger.info("解析xml" + list);

        //判断是否成功返回单号
        if (!list.get(2).equals("E000")) {
            List<String> error = new ArrayList<>();
            error.add("0");
            return error;
        } else {
            //请求单号成功，暂时假设获取单号量为1，直接返回
            for (int i = 3; i > 0; i--) {
                list.remove(0);
            }
        }
        logger.info("请求的单号集合===============" + list);

        //为这些单号生成二维码
        for (Object object : list) {
            Barcode128a.getCode128APicture("97038003015040", barcodePath + "billNo_" + object.toString() + ".jpg");
        }

        return list;
    }

    /**
     * @param @return 设定文件
     * @return String    返回类型
     * @throws NoSuchAlgorithmException
     * @throws
     * @Title: EMSCreatOrder
     * @Description: TODO[EMS请求单号报文拼接]
     */
    @RequestMapping(value = "/ems-getBillNumXmlUnit")
    @ResponseBody
    public static String EmsGetBillNumXmlUnit(@RequestParam(value = "billNoAmount", required = true) int billNoAmount
    ) throws NoSuchAlgorithmException {
        //密码处理(MD532位加密)
        String decode_pwd = MD5.MD5_32bit(passWord);

        /**
         * 请求报文order
         * 账号密码报文等正式下来之后都写死，以便前台调用，业务类型写死，“快递包裹”选9，需传参数仅为单号量
         */
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<XMLInfo>");
        sb.append("<sysAccount>" + sysAccount + "</sysAccount>");
        sb.append("<passWord>" + decode_pwd + "</passWord>");
        sb.append("<appKey>" + appKey + "</appKey>");
        sb.append("<businessType>9</businessType>");
        sb.append("<billNoAmount>" + billNoAmount + "</billNoAmount>");
        sb.append("</XMLInfo>");

        return sb.toString();
    }

    /**
     * http发送执行
     *
     * @param
     */
    @RequestMapping("ems-excuteSending")
    @ResponseBody
    public static String EmsExcuteSending(String addr, String base64_xml, @RequestParam(value = "isRequest", required = false) String isRequest) {


        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(addr + URLEncoder.encode(base64_xml, "utf-8"));
            if (isRequest != "") {
                logger.info("isRequest");
                httpget.setHeader("version", Version);
                httpget.setHeader("authenticate", authenticate);
            }
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
                    if (isRequest != "") {
                        return EntityUtils.toString(entity);
                    }
                    //对响应内容进行解密并解码
                    String decode_rep = URLDecoder.decode(Base64.getFromBase64(EntityUtils.toString(entity)), "utf-8");

                    logger.info("解码后的返回值：" + decode_rep);

                    return decode_rep;
                }
                logger.info("------------------------------------");
            } finally {
                /*response.close();*/
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
     * XML转map   非常好用
     *
     * @param html
     * @return
     */
    public static List getContext(String html) {
        List resultList = new ArrayList();
        Pattern p = Pattern.compile(">([^</]+)</");//正则表达式 commend by danielinbiti
        Matcher m = p.matcher(html);//
        while (m.find()) {
            resultList.add(m.group(1));//
        }
        return resultList;
    }
}
