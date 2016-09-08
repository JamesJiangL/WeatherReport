package com.james_jiang.weatherreport.Utils.Http;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by JC on 2016/8/7.
 */
public class ContentHandler extends DefaultHandler {
    private final static String TAG = "ContentHandler";
    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;
    @Override
    public void startDocument() throws SAXException{
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException{
        //记录当前结点
        nodeName = localName;
        Log.e(TAG + "localName", localName);
    }
    @Override
    public void characters(char[] ch, int start, int length)throws SAXException{
        //根据结点名判断将内容添加到哪个 StringBuilder 对象中
        if(nodeName.equals("id")){
            id.append(ch, start, length);
        }else if(nodeName.equals("name")){
            name.append(ch, start, length);
        }else if(nodeName.equals("version")){
            version.append(ch,start, length);
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName)throws SAXException{
        Log.i(TAG, "进入endElement");
        if(localName.equals("app")){
            Log.i(TAG, "id is " + id.toString().trim());
            Log.i(TAG, "name is " + name.toString().trim());
            Log.i(TAG, "version is " + version.toString().trim());
            //最后清空 StringBuilder
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }
    @Override
    public void endDocument()throws SAXException{

    }
}
