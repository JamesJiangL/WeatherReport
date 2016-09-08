package com.james_jiang.weatherreport.Utils.Http;

import android.util.Log;

import com.james_jiang.weatherreport.DataBase.Areas;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JC on 2016/8/13.
 */
public class HtmlUtil {
    private final static String TAG = "HtmlUtil";
    //使用JSoup解析Html文件
    public static void parseHtmlWithJSoup(final String htmlData, final HtmlParseListener listener){
        Log.e(TAG, "开始解析");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder builder = new StringBuilder();
                    Document doc = Jsoup.parse(htmlData);
                    Element table = doc.select("table.table_tr").first();   //这样才对
                    Elements trLists = table.getElementsByTag("tr");
                    int size = trLists.size();
                    for (int i = 0; i < size; i++) {
                        Element trs = trLists.get(i);
                        Elements tdList = trs.getElementsByTag("td");
                        int num = tdList.size();
                        for (int j = 0; j < num; j++) {
                            Element value = tdList.get(j);
                            builder.append(value.text());
                            builder.append(",");
                        }
                        builder.append("\n");
                    }
                    List<Areas> areasList = splitString(builder.toString());
                    Log.e(TAG, "解析完成");
                    if(listener != null)
                        listener.onFinish(areasList);
                }catch (Exception e){
                    e.printStackTrace();
                    if(listener != null)
                        listener.onError(e);
                }
            }
        }).start();
    }
    //拆分字符串
    private static List<Areas> splitString(String src){
        if(src != null) {
            List<Areas> areasList = new ArrayList<>();
            String[] strings =  src.split("\n");
//            System.out.println("strings[0]" + strings[0]);
            int length = strings.length;
            for (int i = 1; i < length; i++) {
                String[] temp = strings[i].split(",");
                Areas areas = new Areas();
                areas.setAreaId(temp[0]);
                areas.setAreaNameEN(temp[1]);
                areas.setAreaNameCH(temp[2]);
                areas.setCity(temp[3]);
                areas.setProvince(temp[4]);
                areasList.add(areas);
            }
            return areasList;
        }else {
            Log.e(TAG, "splitString is null !!!");
            return null;
        }

    }
}
