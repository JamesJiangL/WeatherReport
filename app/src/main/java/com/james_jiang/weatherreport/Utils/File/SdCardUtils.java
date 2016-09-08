package com.james_jiang.weatherreport.Utils.File;

import android.os.StatFs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2015/12/14.
 */
public class SdCardUtils {
    private static String InnerSdCardPath = "/mnt/sdcard";
    private static String ExternalSdCardPath = null;
    private static String UDiskPath = null;
    //内置SD卡、外置SD卡、U盘会根据插入顺序发生变化
    //重启挂载顺序：U盘>外置SD卡>内置SD卡
    //通过循环判断是否存在？
    public static boolean isExternalSdCardExist(){
        for(int i = 0; i < getAllSdCardPath().size(); i++){
            if(getAllSdCardPath().get(i).equals("/mnt/sdcard1")){
                ExternalSdCardPath = getAllSdCardPath().get(i);
                return true;
            }
        }
        return false;
    }
    //获得外置SD卡路径
    public static String getExternalSdCardPath(){
        if(isExternalSdCardExist())
            return ExternalSdCardPath;
        else
            return null;
    }
    //判断U盘是否存在
    public static boolean isUDiskExist(){
        for(int i = 0; i < getAllSdCardPath().size(); i++){
            if(getAllSdCardPath().get(i).equals("/mnt/udisk1/disk-1")){
                UDiskPath = getAllSdCardPath().get(i);
                return true;
            }
        }
        return false;
    }
    //获得U盘路径
    public static String getUDiskPath(){
        if(isUDiskExist()){
            return UDiskPath;
        }else
            return null;
    }
    //获得内置SD卡路径
    public static String getInnerSdCardPath(){
        return InnerSdCardPath;
    }
    //查看SD卡剩余容量, path:SD卡挂载路径
    public static long getFreeStorage(String path){
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long availableBlock = statFs.getAvailableBlocks();
        return blockSize * availableBlock / 1024 / 1024;   //容量单位MB
    }
    //查看SD卡总容量, path:SD卡挂载路径
    public static long getTotalStorage(String path){
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long blockCounts = statFs.getBlockCount();
        return blockSize * blockCounts / 1024 / 1024;   //容量单位MB
    }
    //读取当前所有SD卡路径，内外SD卡、U盘
    //内置SD卡目录：/mnt/sdcard0
    //外置SD卡目录：/mnt/sdcard1
    //外接U盘目录： /mnt/udisk1/disk-1
    //判断外置sd卡是否卸载不能直接判断得到外置sd卡路径是否为空
    //即使外置sd卡卸载时，得到的路径，在拔出sd卡0.8秒内仍然可能不为空
    public static List<String> getAllSdCardPath() {
        List<String> SdList = new ArrayList<String>();
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {    //循环读取
                // 将常见的linux分区过滤掉
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("media"))
                    continue;
                if (line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data")
                        || line.contains("tmpfs") || line.contains("shell")
                        || line.contains("root") || line.contains("acct")
                        || line.contains("proc") || line.contains("misc")
                        || line.contains("obb")) {
                    continue;
                }
                if (line.contains("fat") || line.contains("fuse") || line.contains("ntfs")) {
                    String[] columns = line.split(" ");
                    if (columns != null && columns.length > 1) {
                            SdList.add(columns[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SdList;
    }
}
