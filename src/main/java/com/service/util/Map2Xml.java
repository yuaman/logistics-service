package com.service.util;

import java.util.ArrayList;
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.Map;  
import org.dom4j.Attribute;  
import org.dom4j.Document;  
import org.dom4j.DocumentException;  
import org.dom4j.DocumentHelper;  
import org.dom4j.Element;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;


import org.xml.sax.SAXException;  
import javax.xml.parsers.SAXParser;   
import javax.xml.parsers.SAXParserFactory;   
import org.xml.sax.InputSource;   
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @ClassName Map2Xml
 * @Description 键值对与xml格式转化工具类
 * @author lightSmart
 * @Date 2016年12月2日 下午3:21:39
 * @version 1.0.0
 */
public class Map2Xml {
	private static Log logger  = LogFactory.getLog(Map2Xml.class.getName());
	
	public static Map<String, String> getXML(String requestXml){  
        Map<String, String> map = new HashMap<String, String>();  
        // 将字符串转为XML  
        Document doc;  
        try {  
            doc = DocumentHelper.parseText(requestXml);  
            // 获取根节点  
            Element rootElm = doc.getRootElement();//从root根节点获取请求报文  
            Map2Xml xmlIntoMap = new Map2Xml();  
            map = xmlIntoMap.parseXML(rootElm, new HashMap<String, String>());  
        } catch (DocumentException e) {  
            e.printStackTrace();  
        }  
         
          
        return map;  
    }  
    /** 
     * 将xml解析成map键值对 
     * <功能详细描述> 
     * @param ele 需要解析的xml对象 
     * @param map 入参为空，用于内部迭代循环使用 
     * @return 
     * @see [类、类#方法、类#成员] 
     */  
    private  static Map<String, String> parseXML(Element ele, Map<String, String> map)  
    {  
          
        for (Iterator<?> i = ele.elementIterator(); i.hasNext();)  
        {  
            Element node = (Element)i.next();  
            //System.out.println("parseXML node name:" + node.getName());  
            if (node.attributes() != null && node.attributes().size() > 0)  
            {  
                for (Iterator<?> j = node.attributeIterator(); j.hasNext();)  
                {  
                    Attribute item = (Attribute)j.next();  
                      
                    map.put(item.getName(), item.getValue());  
                }  
            }  
            if (node.getText().length() > 0)  
            {  
                map.put(node.getName(), node.getText());  
            }  
            if (node.elementIterator().hasNext())  
            {  
                parseXML(node, map);  
            }  
        }  
        return map;  
    }  
	
	
	public static String callMapToXML(Map map) {  
        logger.info("将Map转成Xml, Map：" + map.toString());  
        StringBuffer sb = new StringBuffer();  
        sb.append("<RequestOrder>"); 
        mapToXMLTest2(map, sb);  
        sb.append("</RequestOrder>");  
        logger.info("将Map转成Xml, Xml：" + sb.toString());  
        try {  
            return sb.toString();  
        } catch (Exception e) {  
            logger.error(e);  
        }  
        return null;  
    }  
  
    private static void mapToXMLTest2(Map map, StringBuffer sb) {  
        Set set = map.keySet();  
        for (Iterator it = set.iterator(); it.hasNext();) {  
            String key = (String) it.next();  
            Object value = map.get(key);  
            if (null == value)  
                value = "";  
            if (value.getClass().getName().equals("java.util.ArrayList")) {  
                ArrayList list = (ArrayList) map.get(key);  
                sb.append("<" + key + ">");  
                for (int i = 0; i < list.size(); i++) {  
                    HashMap hm = (HashMap) list.get(i);  
                    mapToXMLTest2(hm, sb);  
                }  
                sb.append("</" + key + ">");  
  
            } else {  
                if (value instanceof HashMap) {  
                    sb.append("<" + key + ">");  
                    mapToXMLTest2((HashMap) value, sb);  
                    sb.append("</" + key + ">");  
                } else {  
                    sb.append("<" + key + ">" + value + "</" + key + ">");  
                }  
            }  
        }  
    } 
    /**
     * 解析xml
     * @param protocolXML
     */
    public static void parse(String protocolXML) {   
                
            try {   
                 SAXParserFactory saxfac = SAXParserFactory.newInstance();      
                 SAXParser saxparser = saxfac.newSAXParser();   
                 TestSAX   tsax = new TestSAX();   
                 saxparser.parse(new InputSource(new StringReader(protocolXML)),tsax);   
             } catch (Exception e) {   
                 e.printStackTrace();   
             }   
         }   
    }

class TestSAX extends DefaultHandler{

    private StringBuffer buf;
    private String str;
    public TestSAX(){
         super();
     }

    public void startDocument() throws SAXException{
         buf=new StringBuffer();
         System.out.println("*******开始解析XML*******");
     }

    public void endDocument() throws SAXException{
         System.out.println("*******XML解析结束*******");
     }

    public void endElement(String namespaceURI,String localName,String fullName )throws SAXException{
         str = buf.toString();
         System.out.println("节点="+fullName+"\tvalue="+buf+" 长度="+buf.length());
//             System.out.println();
         buf.delete(0,buf.length());
       }

    public void characters( char[] chars, int start, int length )throws SAXException{
        //将元素内容累加到StringBuffer中
         buf.append(chars,start,length);
     }


}
