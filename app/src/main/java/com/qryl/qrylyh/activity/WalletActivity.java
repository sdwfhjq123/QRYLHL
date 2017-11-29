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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
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
    private double mMoney;
    private ProgressDialog dialog;
    private int roleType;
    private int type = 3;//判断点击支付宝或者微信的标识

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
        roleType = prefs.getInt("role_type", 4);
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
                        mMoney = jsonObject.getDouble("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                tvMoney.setText(String.valueOf(mMoney));
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
                View view = View.inflate(this, R.layout.dialog_withdraw_deposit, null);
                final EditText etMoney = (EditText) view.findViewById(R.id.et_money);
                final EditText etAccount = (EditText) view.findViewById(R.id.et_account);
                final EditText etName = (EditText) view.findViewById(R.id.et_name);
                final RadioButton rbWx = (RadioButton) view.findViewById(R.id.rb_wx);
                final RadioButton rbZfb = (RadioButton) view.findViewById(R.id.rb_zfb);
                rbWx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbWx.isChecked()) {
                            rbZfb.setChecked(false);
                            type = 2;
                            Log.i(TAG, "onClick: type" + type);
                        }
                    }
                });
                rbZfb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbZfb.isChecked()) {
                            rbWx.setChecked(false);
                            type = 1;
                            Log.i(TAG, "onClick: type" + type);
                        }
                    }
                });

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
                        String account = etAccount.getText().toString();
                        String name = etName.getText().toString();
                        if (money == null) {
                            money = "0";
                        }
                        if (money == null) {
                            Toast.makeText(WalletActivity.this, "金额格式错误，请重新编辑", Toast.LENGTH_SHORT).show();
                        } else {
                            if ((RegularUtil.isNumber(money) && Double.valueOf(money) <= mMoney)
                                    && (mMoney != 0)
                                    && (account != null)
                                    && (name != null)
                                    && (rbWx.isChecked() || rbZfb.isChecked())) {
                                withdrawDeposit(account, name, money, type);//提现 http
                            } else {
                                Toast.makeText(WalletActivity.this, "提交信息错误,请重新编辑", Toast.LENGTH_SHORT).show();
                            }
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

    private void withdrawDeposit(String account, String name, String money, int type) {
        //Log.i(TAG, "withdrawDeposit: account" + account + ",name" + name + ",money:" + money + ",type:" + type);
        showProgressDialog();//显示dialog

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        byte[] bytes = ("/test/common/addWithdrawApply-" + token + "-" + currentTimeMillis).getBytes();
        String sign = EncryptionByMD5.getMD5(bytes);

        Map<String, String> map = new HashMap<>();
        map.put("amount", String.valueOf(money));
        map.put("userId", userId);
        map.put("roleType", String.valueOf(roleType));
        map.put("withdrawAccount", account);
        map.put("accountName", name);
        map.put("accountType", String.valueOf(type));
        map.put("sign", sign);
        map.put("tokenUserId", userId + "yh");
        map.put("timeStamp", currentTimeMillis);
        HttpUtil.postAsyn(ConstantValue.URL + "/common/addWithdrawApply", map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "提现通知: " + result);
                try {
                    final JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                Toast.makeText(WalletActivity.this, "提交成功，请等待审核", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (resultCode.equals("500")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                try {
                                    Toast.makeText(WalletActivity.this, jsonObject.getString("erroMessage"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (resultCode.equals("400")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WalletActivity.this, "该账号已长时间未登录，无法加载信息，请重新登录", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent("com.qryl.qrylyh.activity.BaseActivity.MustForceOfflineReceiver");
                                sendBroadcast(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
