package com.james_jiang.weatherreport.Utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.james_jiang.weatherreport.R;

/**
 * Created by JC on 2015/11/16.
 * 解决Toast重复消息导致显示时间过长
 */
public class ToastUtil {
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };
    public static void showToast(Context mContext, String text, int duration) {//上下文环境，内容，显示时间

        mHandler.removeCallbacks(r);
//        if (mToast != null){
//            Log.d("mToast", "不为空");
//            mToast.setText(text);
//            customToast(mContext);
//        }
//        else{
//            Log.d("mToast", "为空");
//            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
//            customToast(mContext);
//        }
        customToast(mContext, text);
        mHandler.postDelayed(r, duration);

        mToast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {//读取strings.xml文件里提示信息
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }
    private static void customToast(Context context, String text){
        if(mToast != null){    //不为空，直接显示就OK
            mToast.setText(text);
        }else {     //为空，即第一次初始化
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            LinearLayout linearLayout = (LinearLayout) mToast.getView();
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.clear_white));
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50,50);
            layoutParams.setMargins(0, 0 , 10, 0);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(R.drawable.warning);
            linearLayout.addView(imageView, 0);
//            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            linearLayout.setLayoutParams(layoutParams1);
            TextView message = (TextView) linearLayout.getChildAt(1);
            message.setTextSize(22f);
            message.setTextColor(context.getResources().getColor(R.color.white));
        }
    }
}
