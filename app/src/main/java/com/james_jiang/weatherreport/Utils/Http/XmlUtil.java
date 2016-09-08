package com.james_jiang.weatherreport.Utils.Http;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by JC on 2016/8/13.
 */
public class XmlUtil {
    private final static String TAG = "XmlUtil";
    //使用Pull方式解析xml文件，都可以使用回调接口传递解析的数据
    public static void parseXMLWithPull(String xmlData){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = null;
            String name = null;
            String version = null;
            while (eventType != XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();      //节点名称
                switch (eventType){     //开始解析某个节点
                    case XmlPullParser.START_TAG:
                        if(nodeName.equals("id")){
                            id = xmlPullParser.nextText();
                        }else if(nodeName.equals("name")){
                            name = xmlPullParser.nextText();
                        }else if(nodeName.equals("version")){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(nodeName.equals("app")){
                            Log.i(TAG, "id is " + id);
                            Log.i(TAG, "name is " + name);
                            Log.i(TAG, "version is " + version);
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    //使用SAX方式解析xml文件
    public static void parseXMLWithSAX(String xmlData){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new ContentHandler();
            xmlReader.setContentHandler(handler);       //将 ContentHandler 的实例设置到 XMLReader 中
            //开始解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
