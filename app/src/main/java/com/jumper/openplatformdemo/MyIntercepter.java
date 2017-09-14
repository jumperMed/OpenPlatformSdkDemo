package com.jumper.openplatformdemo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.jumper.jumperopenplatform.MacInterceptor;
import com.jumper.jumperopenplatform.bean.ResultInfo;

/**
 * Created by Terry on 2017/9/11 14:27.
 *
 * @author Terry
 * @version [版本号, YYYY-MM-DD]
 * @since [产品/模块版本]
 */

public class MyIntercepter extends MacInterceptor implements Parcelable{


    public MyIntercepter(){};


    public MyIntercepter(String url,String[] args){
        super(url,args);
    }

    public  MyIntercepter(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyIntercepter> CREATOR = new Creator<MyIntercepter>() {
        @Override
        public MyIntercepter createFromParcel(Parcel in) {
            return new MyIntercepter(in);
        }

        @Override
        public MyIntercepter[] newArray(int size) {
            return new MyIntercepter[size];
        }
    };

    @Override
    public ResultInfo doInterceptor(String mac) {
        Log.d("Terry","----mac --- "+ mac);
        Log.d("Terry","----url --- "+ url);

        // 在此处添加你们过滤逻辑，此外已经是子线程了


        ResultInfo info = new ResultInfo();
        if(TextUtils.isEmpty(url)){
            info.msg = 0;
            info.msgbox = "我就不让你用 "+ mac +"。*^*";
        }else{
            info.msg = 1;
        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return info;
    }
}
