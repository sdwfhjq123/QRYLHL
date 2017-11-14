package com.qryl.qrylyh.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.JavascriptInterface;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;


/**
 * Created by yinhao on 2017/9/26.
 */

public class HgxqAndroidToJs {

    private Activity activity;
    private Context context;

    public HgxqAndroidToJs(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    /**
     * 根布局点击返回销毁页面
     */
    @JavascriptInterface
    public void finishActivity() {
        this.activity.finish();
    }

    public void forceOffline() {
        Intent intent = new Intent("com.qryl.qrylyh.activity.BaseActivity.MustForceOfflineReceiver");
        context.sendBroadcast(intent);
    }

}
