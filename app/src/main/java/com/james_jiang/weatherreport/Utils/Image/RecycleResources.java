package com.james_jiang.weatherreport.Utils.Image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by john on 2016/1/8.
 */
public class RecycleResources {
    //回收背景图片
    public static void releaseBackgroundWhenDestroy(RelativeLayout relativeLayout){        //不能回收自己绘制的图片
        try {
            ColorDrawable colorDrawable = (ColorDrawable) relativeLayout.getBackground();
            relativeLayout.setBackgroundResource(0);
            colorDrawable.setCallback(null);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("背景资源不存在", "已释放");
        }
    }
    //回收ImageView图片
    public static void releaseImageViewWhenDestroy(ImageView imageView){
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            imageView.setImageDrawable(null);
            bitmapDrawable.setCallback(null);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if(bitmap != null && ! bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("图片资源不存在", "已释放");
        }
    }
    //显示系统内存
    public static void showSystemMemory(){
        Log.d("maxMemory", "" + Runtime.getRuntime().maxMemory() / 1024.0f / 1024.0f + "MB");
        Log.d("freeMemory", "" + Runtime.getRuntime().freeMemory() / 1024.0f /1024.0f + "MB");
        Log.d("totalMemory", "" + Runtime.getRuntime().totalMemory() / 1024.0f /1024.0f + "MB");
    }
}
