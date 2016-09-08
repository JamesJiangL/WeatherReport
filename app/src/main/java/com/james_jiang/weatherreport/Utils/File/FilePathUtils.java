package com.james_jiang.weatherreport.Utils.File;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by john on 2015/12/22.
 */
public class FilePathUtils {
    //获取文件扩展名
    public static String getExtensionWithDot(String fileName){     //带.
        if (StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(fileName, "."))
            return StringUtils.EMPTY;
        String ext = StringUtils.substring(fileName,
                StringUtils.lastIndexOf(fileName, "."));
        return StringUtils.trimToEmpty(ext);
    }
    public static String getExtensionWithoutDot(String fileName){    //不带.
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
    }
    //获取文件名，不带后缀名
    public static String getFileNameWithoutExtension(String fileName) {
        String ext = StringUtils.substring(fileName, 0, StringUtils.lastIndexOf(fileName, "."));
        return StringUtils.trimToEmpty(ext);
    }
}
