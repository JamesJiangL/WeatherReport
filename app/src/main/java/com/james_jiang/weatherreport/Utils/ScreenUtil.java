package com.james_jiang.weatherreport.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by john on 2015/11/20.
 * 屏幕相关辅助类
 */
public class ScreenUtil {
    //获取屏幕宽度，px
    public static int getScreenWidth_px(Context context) {
        int width;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        Log.d("屏幕宽度:", "" + width);
        return width;
    }
    //获取屏幕高度
    public static int getScreenHeight_px(Context context) {
        int height;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        height = outMetrics.heightPixels;
        Log.d("屏幕高度：",""+height);
        return height;
    }
    //获取屏幕宽度，dpi
    public static float getScreenWidth_dpi(Context context) {
        float width;
        width = context.getResources().getDisplayMetrics().xdpi;
        Log.d("屏幕宽度密度值:", "" + width);
        return width;
    }
    //获取屏幕高度
    public static float getScreenHeight_dpi(Context context) {
        float height;
        height = context.getResources().getDisplayMetrics().ydpi;
        Log.d("屏幕高度密度值：",""+height);
        return height;
    }
    //获取屏幕密度(比例密度)
    public static float getScreenDensity(Context context){
        float density;
        density = context.getResources().getDisplayMetrics().density;
        Log.d("屏幕密度(像素比例)：", "" + density);
        return density;
    }
    //获取屏幕密度（每英寸像素密度）
    public static float getScreenDensity_dpi(Context context){
        float density_dpi;
        density_dpi = context.getResources().getDisplayMetrics().densityDpi;
        Log.d("屏幕密度(每英寸像素)：", "" + density_dpi);
        return density_dpi;
    }
    //获取状态栏高度
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusHeight;
    }
    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }
    //获取当前屏幕截图，包含状态栏
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth_px(activity);
        int height = getScreenHeight_px(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }
    //获取当前屏幕截图，不包含状态栏
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth_px(activity);
        int height = getScreenHeight_px(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }
    /**
     * 判断是否是魅族系统
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }
    //获取smartBar高度
    public static int getSmartBarHeight(Context context, ActionBar actionbar)
    {
        if (actionbar != null)
            try {
                Class c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("mz_action_button_min_height");
                int height = Integer.parseInt(field.get(obj).toString());
                return context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
                actionbar.getHeight();
            }
        return 0;
    }
}
