package com.qryl.qrylyh.util;

import android.app.Activity;
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

    public HgxqAndroidToJs(Activity activity) {
        this.activity = activity;
    }

    /**
     * 根布局点击返回销毁页面
     */
    @JavascriptInterface
    public void finishActivity() {
        this.activity.finish();
    }

}
