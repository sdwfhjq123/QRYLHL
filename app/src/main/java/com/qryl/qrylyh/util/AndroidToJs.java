package com.qryl.qrylyh.util;

import android.app.Activity;
import android.webkit.JavascriptInterface;


/**
 * Created by yinhao on 2017/9/26.
 */

public class AndroidToJs {

    private Activity activity;

    public AndroidToJs(Activity activity) {
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
