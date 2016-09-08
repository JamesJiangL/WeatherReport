package com.james_jiang.weatherreport.Utils.File;

import android.content.Context;
import android.util.Log;

import com.james_jiang.weatherreport.Common.MyApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by john on 2015/12/18.
 */
public class FileOperateUtils {
    //得到文件时间
    public static String getFileDate(File file){
        String time = "1970-1-1 00:00:00";
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(file.lastModified());
            time = sDateFormat.format(date);

        }catch (Exception e){
            Log.e("获取文件时间", "获取失败");
        }
        return time;
    }
    //得到文件或者文件夹大小
    public static String getFileOrFilesSize(File file){
        String size = "0";
        long blockSize = 0;
        try {
            if(file.isDirectory())
                blockSize = getFileSizes(file);
            else
                blockSize = getFileSize(file);
        }catch (Exception e){
            Log.e("获取文件大小", "获取失败");
        }
        blockSize = blockSize /1024;
        size = Long.toString(blockSize);
        return size;
    }
    //获取文件大小
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }
    //获取文件夹大小
    private static long getFileSizes(File file) throws Exception {
        long size = 0;
        File flist[] = file.listFiles();
        for (File aFlist : flist) {
            if (aFlist.isDirectory()) {
                size = size + getFileSizes(aFlist);
            } else {
                size = size + getFileSize(aFlist);
            }
        }
        return size;
    }
    //过滤隐藏文件
    public static boolean isHideFile(File fileName){
        if(!fileName.getName().startsWith(".") && !fileName.getName().startsWith("_"))
            return true;
        else
            return false;
    }
    public static File[] fileFilter(File[] mFiles) throws Exception{
        List<File> tempFiles = new ArrayList<>();
        for(int i = 0; i < mFiles.length; i++){
            if(!mFiles[i].getName().startsWith(".") && !mFiles[i].getName().startsWith("_"))
                tempFiles.add(mFiles[i]);
        }
        return tempFiles.toArray(new File[tempFiles.size()]);
    }
    //文件排序，按字母顺序排
    public static File[] sortFile(File[] files){
        try {
            Collections.sort(Arrays.asList(files), new compareFile());
        }catch (Exception e){
            Log.d("排序失败", "");
        }
        return files;
    }
    //重写Comparator方法
    private static class compareFile implements Comparator<File> {
        //1.先比较文件夹（文件夹在文件的顺序之上）2.以A-Z的字典排序3.比较文件夹和文件4.比较文件和文件夹
        @Override
        public int compare(File pFile1, File pFile2) {
            if (pFile1.isDirectory() && pFile2.isDirectory()) {
                return pFile1.getName().compareToIgnoreCase(pFile2.getName());
            } else {
                if (pFile1.isDirectory() && pFile2.isFile()) {
                    return -1;
                } else if (pFile1.isFile() && pFile2.isDirectory()) {
                    return 1;
                } else {
                    return pFile1.getName().compareToIgnoreCase(pFile2.getName());
                }
            }
        }
    }
    //获取MIME类型
    public static String getMIMEType(File file){
        String type = "";
        String fileName = file.getName();
        //取出文件后缀名并转成小写
        String  fileEnds = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();
        if(fileEnds.equals("m4a")||fileEnds.equals("mp3")||fileEnds.equals("mid")||fileEnds.equals("xmf")||fileEnds.equals("ogg")||fileEnds.equals("wav")){
            type = "audio/*";// 系统将列出所有可能打开音频文件的程序选择器
        }else if(fileEnds.equals("3gp")||fileEnds.equals("mp4")){
            type = "video/*";// 系统将列出所有可能打开视频文件的程序选择器
        }else if(fileEnds.equals("jpg")||fileEnds.equals("gif")||fileEnds.equals("png")||fileEnds.equals("jpeg")||fileEnds.equals("bmp")){
            type = "image/*";// 系统将列出所有可能打开图片文件的程序选择器
        }else{
            type = "*/*"; // 系统将列出所有可能打开该文件的程序选择器
        }
        return type;
    }
    //删除文件
    public static void deleteFile(File file){
        if(file.exists()){    //先判断文件是否存在
            if(file.isFile())    //如果是文件，直接删
                file.delete();
            if(file.isDirectory()) {  //如果是文件夹
                File[] files = file.listFiles();
                for(int i = 0; i < files.length; i++){
                    deleteFile(files[i]);
                }
                file.delete();
            }
        }else
            Log.d("文件不存在","文件不存在");
    }
    //判断文件夹是否为空
    public static boolean isEmptyDirectory(File file){
        if(file.exists() && file.isDirectory()){
            if(file.list().length > 0)
                return false;
        }
        return true;
    }
    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int CopySdcardFile(String fromFile, String toFile) {
        InputStream fosfrom = null;
        OutputStream fosto = null;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }finally{
            try {
                if (fosfrom != null) {
                    fosfrom.close();
                }
                if (fosto != null) {
                    fosto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //复制文件夹和文件
    public static int copyFile(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如果不存在则 return出去
        if(!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();
        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if(!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for(int i= 0;i<currentFiles.length;i++) {
            if(currentFiles[i].isDirectory()){   //如果当前项为子目录 进行递归
                copyFile(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");
            }else{     //如果当前项为文件则进行文件拷贝
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }
    //文件读写  /data/data/<packageName>/files/目录
    public static void writeFile(Context context, String fileName, String data){
        FileOutputStream os = null;
        BufferedWriter writer = null;
        try {
            os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(data);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
                if(os != null){
                    os.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static String readFile(Context context, String fileName){
        FileInputStream is = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            is = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(reader != null){
                    reader.close();
                }
                if(is != null){
                    is.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
    //其余位置的文件读写,此处指定/Android/data/com.james_jiang.weatherreport/wCache，天气数据默认缓存位置
    public static void writeFile(String fileName, String data){
        File file = new File(MyApplication.WCACHE + "/" + fileName);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {       //流关闭顺序与流开启顺序相反
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
                if(fileWriter != null){
                    fileWriter.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static String readFile(String fileName){
        File file = new File(MyApplication.WCACHE + "/" + fileName);
        if(!file.exists()){
            return null;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder builder = new StringBuilder();
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                builder.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
                if(fileReader != null){
                    fileReader.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
