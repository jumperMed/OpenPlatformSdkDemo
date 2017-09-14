package com.jumper.openplatformdemo;

import android.app.Application;

/**
 * Created by Administrator on 2017/3/27.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.application = this;
    }
}
