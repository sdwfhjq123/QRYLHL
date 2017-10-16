package com.qryl.qrylyh.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.sms.SMSSDK;

/**
 * Created by hp on 2017/9/14.
 */

public class QRYLHLApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化JPush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //初始化极光短信
        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setDebugMode(true);
        SMSSDK.getInstance().setIntervalTime(60000);
    }

    public static Context getContext() {
        return context;
    }
}
