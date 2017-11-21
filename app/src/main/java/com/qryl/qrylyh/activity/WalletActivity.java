package com.qryl.qrylyh.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.EncryptionByMD5;
import com.qryl.qrylyh.util.HttpUtil;
import com.qryl.qrylyh.util.RegularUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WalletActivity";

    private TextView tvMoney;
    private String userId;
    private String token;
    private double money;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
        initData();
    }

    private void initData() {
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        token = prefs.getString("token", "");
        Log.i(TAG, "WalletActivity: userId" + userId);

        //初始化余额
        getBalance();
    }

    /**
     * 初始化余额
     */
    private void getBalance() {
        showProgressDialog();
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        byte[] bytes = ("/test/carer/getMyBalance-" + token + "-" + currentTimeMillis).getBytes();
        String sign = EncryptionByMD5.getMD5(bytes);
        Map<String, String> map = new HashMap<>();
        map.put("sign", sign);
        map.put("tokenUserId", userId + "yh");
        map.put("timeStamp", currentTimeMillis);
        map.put("loginId", userId);
        HttpUtil.postAsyn(ConstantValue.URL + "/carer/getMyBalance", map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    final JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("resultCode").equals("200")) {
                        money = jsonObject.getDouble("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                tvMoney.setText(String.valueOf(money));
                            }
                        });
                    } else if (jsonObject.getString("resultCode").equals("400")) {
                        Intent intent = new Intent("com.qryl.qrylyh.activity.BaseActivity.MustForceOfflineReceiver");
                        sendBroadcast(intent);
                    } else if (jsonObject.getString("resultCode").equals("500")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    closeProgressDialog();
                                    Toast.makeText(WalletActivity.this, jsonObject.getString("erroMessage"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示加载中dialog
     */
    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("正在加载...");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    /**
     * 关闭Dialog
     */
    private void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void initView() {
        hiddenSomeView();
        TextView tvBill = (TextView) findViewById(R.id.tv_bill);
        TextView tvWithdrawDeposit = (TextView) findViewById(R.id.tv_withdraw_deposit);
        tvMoney = (TextView) findViewById(R.id.tv_money);
        tvBill.setOnClickListener(this);
        tvWithdrawDeposit.setOnClickListener(this);
    }

    private void hiddenSomeView() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        TextView tvHelp = (TextView) findViewById(R.id.help_name);
        tvTitle.setText("我的钱包");
        tvReturn.setOnClickListener(this);
        tvHelp.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_text:
                //点击返回结束当前页面
                finish();
                break;
            case R.id.tv_bill://点击查看账单
                Intent intent = new Intent(this, WalletRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_withdraw_deposit://点击提现
                View view = View.inflate(this, R.layout.text_item_dialog_text, null);
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_dialog);
                final EditText etMoney = (EditText) view.findViewById(R.id.et_hint_dialog);
                tvTitle.setText("提现");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setCancelable(true);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String money = etMoney.getText().toString();
                        //Toast.makeText(WalletActivity.this, money, Toast.LENGTH_SHORT).show();
                        if (RegularUtil.isNumber(money)) {
                            //withdrawDeposit();//提现 http
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    /**
     * 提现
     */
    private void withdrawDeposit() {
        showProgressDialog();//显示dialog

    }
}
