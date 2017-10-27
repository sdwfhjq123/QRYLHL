package com.qryl.qrylyh.activity.H5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.HgxqAndroidToJs;

public class OrderInfoActivity extends BaseActivity {
    private static final String TAG = "XzxqActivity";
    private static final String URL_HG = ConstantValue.URL_H5 + "/medical/order_details_carer.html";
    private static final String URL_XZ = ConstantValue.URL_H5 + "/medical/order_details_medicalStaff.html";
    private static final String URL_AM = ConstantValue.URL_H5 + "/medical/order_details_massager.html";
    private static final String URL_MY = ConstantValue.URL_H5 + "/medical/order_details_motherBaby.html";

    private WebView webview;
    private String userId;
    private int orderId;
    private int orderType;

    public static void actionStart(Context context, int params, int params2) {
        Intent intent = new Intent(context, OrderInfoActivity.class);
        intent.putExtra("orderId", params);
        intent.putExtra("orderType", params2);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", 0);
        orderType = intent.getIntExtra("orderType", 0);
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        Log.i(TAG, "onCreate: " + userId);
        initView();
    }

    private void initView() {
        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(OrderInfoActivity.this.getApplicationContext().getCacheDir().getAbsolutePath());
        webview.addJavascriptInterface(new HgxqAndroidToJs(this), "qrylhg");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.loadUrl("javascript:getId(" + orderId + ")");
            }
        });
        if (orderType == 0) {
            webview.loadUrl(URL_HG);
        } else if (orderType == 1) {
            webview.loadUrl(URL_XZ);
        } else if (orderType == 2) {
            webview.loadUrl(URL_AM);
        }
        // webview.loadUrl(URL_XZ);

    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KEYCODE_BACK) && webview.canGoBack()) {
//            webview.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
