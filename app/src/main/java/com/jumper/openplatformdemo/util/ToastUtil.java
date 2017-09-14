package com.jumper.openplatformdemo.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.jumper.openplatformdemo.ApplicationContext;
import com.jumper.terry.toastcompatlib.ToastCompat;

/**
 * Created by Terry on 2016/10/24.
 *
 *
 */

public class ToastUtil {


    private static Handler handler = new Handler(Looper.getMainLooper());

    public static ToastCompat toastCompat;


    public static ToastCompat getToastCompat() {
        if (toastCompat == null) {
            toastCompat = new ToastCompat(ApplicationContext.application);
            toastCompat.setDuration(Toast.LENGTH_SHORT);
        }
        return toastCompat;
    }


    public static void show(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getToastCompat().setText(text).show();
            }
        });
    }



    public static  void showError(String msg, String defaultString){
        String toastStr = msg;
        if(TextUtils.isEmpty(msg)){
            toastStr = defaultString;
        }
        show(toastStr);
    }

    public static void showError(String message){
        showError(message,"网络异常");
    }


}
