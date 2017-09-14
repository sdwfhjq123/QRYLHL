package com.qryl.qrylyh.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by hp on 2017/9/14.
 */

public class QRYLHLApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }
}
