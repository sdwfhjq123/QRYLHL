package com.qryl.qrylyh.activity.H5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.HgxqAndroidToJs;
import com.qryl.qrylyh.view.ProgressWebview;


public class WritePatientsFileActivity extends AppCompatActivity {

    private static final String TAG = "WritePatientsFileActivity";

    private static final String URL_YS = ConstantValue.URL_H5 + "/medical/doctor_patient_details.html";
    private static final String URL_HS = ConstantValue.URL_H5 + "/medical/nurse_patient_details.html";
    //private static final String URL_AM = ConstantValue.URL_H5 + "/patient/worker_priority_worker_datails_massager.html";
    //private static final String URL_MY = ConstantValue.URL_H5 + "/patient/worker_priority_worker_datails_motherBaby.html";

    private ProgressWebview webview;
    private String userId;
    private int roleType;
    private int pubId;
    private int patientId;
    private String orderId;

    /**
     * @param context
     * @param pubId     医患端登录id
     * @param patientId 病人的id
     * @param orderId   订单的id
     */
    public static void actionStart(Context context, int pubId, int patientId, int orderId) {
        Intent intent = new Intent(context, WritePatientsFileActivity.class);
        intent.putExtra("pub_id", pubId);
        intent.putExtra("patient_id", patientId);
        intent.putExtra("order_id", orderId);
        context.startActivity(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_patients_file);
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roleType = prefs.getInt("role_type", 4);

        Intent intent = getIntent();
        pubId = intent.getIntExtra("pub_id", 0);
        patientId = intent.getIntExtra("patient_id", 0);
        orderId = intent.getStringExtra("order_id");
        Log.i(TAG, "onCreate: " + userId);
        Log.i(TAG, "传给H5的类型: " + roleType);
        initView();
    }

    private void initView() {
        webview = (ProgressWebview) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setBlockNetworkImage(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDatabasePath(WritePatientsFileActivity.this.getApplicationContext().getCacheDir().getAbsolutePath());
        webview.addJavascriptInterface(new HgxqAndroidToJs(this, this), "qrylhg");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.loadUrl("javascript:getId(" + userId + "," + pubId + "," + patientId + "," + orderId + ")");
            }
        });
        if (roleType == 0) {//护工
            //webview.loadUrl(URL_HS);
        } else if (roleType == 1) {//护士
            webview.loadUrl(URL_HS);
        } else if (roleType == 2) {//医生
            webview.loadUrl(URL_YS);
        } else if (roleType == 3) {//按摩师
            //webview.loadUrl(URL_TN);
        }
    }

}
