package com.james_jiang.weatherreport.Utils.Http;

import android.util.Log;

import com.james_jiang.weatherreport.Utils.File.FileOperateUtils;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JC on 2016/8/7.
 */
public class HttpUtil {
    private final static String TAG = "HttpUtil";
    private final static String https = "" +
            "maps.googleapis.com/maps/api/geocode/json?" +
            "Latlng=40.714224,-73.961452&sensor=true_or_false";
    //使用HttpURLConnection
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
        Log.e(TAG, "进入sendHttpRequest");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream in = null;
                InputStreamReader streamReader = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");         //从服务器获取数据
                    connection.setConnectTimeout(10000);         //连接超时
                    connection.setReadTimeout(10000);            //读取超时
                    connection.setDoInput(true);
                    connection.setDoOutput(true);             //使用这句会报错
                    connection.setUseCaches(false);
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK
                            || responseCode == HttpURLConnection.HTTP_CREATED
                            || responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                        in = connection.getInputStream();
                    }else {
                        in = connection.getErrorStream();
                    }
                    streamReader = new InputStreamReader(in, "utf-8");
                    reader = new BufferedReader(streamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    //成功读完回调listener.onFinish方法得到反馈值
                    if(listener != null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //读取失败则调用listener.onError方法得到错误信息
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection != null){
                        connection.disconnect();
                        try {
                            if (in != null) {
                                in.close();
                            }
                            if (streamReader != null) {
                                streamReader.close();
                            }
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    //使用HttpClient
    public static void sendRequestWithHttpClient(final String address, final HttpCallBackListener listener){
        Log.e(TAG, "进入sendRequestWithHttpClient");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpGet httpGet = new HttpGet(address);
//                    HttpResponse httpResponse = httpClient.execute(httpGet);
//                    if(httpResponse.getStatusLine().getStatusCode() == 200){//请求和响应都成功了
//                        Log.e(TAG, "请求和响应都成功了");
//                        HttpEntity entity = httpResponse.getEntity();
//                        String response = EntityUtils.toString(entity, "utf-8");
//                        if(listener != null) {
//                            listener.onFinish(response);
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    if(listener != null){
//                        listener.onError(e);
//                    }
//                }
//            }
//        }).start();
    }
    //使用百度APIStore里的天气服务
    public static void weatherRequest(final String cityId,
                                      final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = null;
                    String result = null;
                    StringBuilder sbf = new StringBuilder();
                    String httpUrl = "https://api.heweather.com/x3/weather?cityid="
                            + cityId
                            + "&key=fe2331bf76a94a988dc921b87ee71afe";
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);         //连接超时
                    connection.setReadTimeout(8000);            //读取超时
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);
                    // 填入apikey到HTTP header
//                    connection.setRequestProperty("apikey",  "8c92d612e03cf4cb3c9ccaffac4830b5");
//                    connection.connect();
                    InputStream is = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                    String strRead = null;
                    while ((strRead = reader.readLine()) != null) {
                        sbf.append(strRead);
                        sbf.append("\n");
                    }
                    reader.close();
                    result = sbf.toString();
//                    result = convert(result);
                    if(listener != null){   //成功之后写入缓存文件
                        FileOperateUtils.writeFile(cityId, result);
                        listener.onFinish(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener != null){
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
    //Unicode编码转换为中文
    private String convert(String utfString){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=utfString.indexOf("\\u", pos)) != -1){
            sb.append(utfString.substring(pos, i));
            if(i+5 < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }
        return sb.toString();
    }
}
