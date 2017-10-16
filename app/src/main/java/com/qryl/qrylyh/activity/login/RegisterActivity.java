package com.qryl.qrylyh.activity.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.VerificationCountDownTimer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.sms.SMSSDK;
import cn.jpush.sms.listener.SmscheckListener;
import cn.jpush.sms.listener.SmscodeListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    private Button btnVerification;
    private AppCompatEditText etVerification;
    private AppCompatEditText etPsd;
    private AppCompatEditText etPsdComfirm;
    private RelativeLayout rlIsCheck;
    private CheckBox checkBox;
    private TextView tvProtocol;
    private Button btnRegister;
    private AppCompatEditText etTel;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        btnVerification = (Button) findViewById(R.id.btn_verification_register);
        etTel = (AppCompatEditText) findViewById(R.id.et_tel_register);
        etVerification = (AppCompatEditText) findViewById(R.id.et_verification_register);
        etPsd = (AppCompatEditText) findViewById(R.id.et_psd_register);
        etPsdComfirm = (AppCompatEditText) findViewById(R.id.et_psd_confirm_register);
        rlIsCheck = (RelativeLayout) findViewById(R.id.rl_isCheck);
        checkBox = (CheckBox) findViewById(R.id.cb_consent);
        tvProtocol = (TextView) findViewById(R.id.tv_protocol_register);
        btnRegister = (Button) findViewById(R.id.btn_register);
        //根据cb判断是否点击了对号
        rlIsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = checkBox.isChecked();
                //根据checkBox的状态修改样式
                if (checked) {
                    checkBox.setChecked(false);
                    checkBox.setBackgroundResource(R.mipmap.ic_frame_false);
                } else {
                    checkBox.setChecked(true);
                    checkBox.setBackgroundResource(R.mipmap.ic_frame_true);
                }
            }
        });

        //注册
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击注册的时候要确认所有的注册信息不为空
                registerIfNotNull();
            }
        });
        //实现倒计时并发送请求并获取验证码的功能
        receiverSMS();
    }

    /**
     * 点击注册的时候要确认所有的注册信息不为空
     */
    private void registerIfNotNull() {
        if (TextUtils.isEmpty(etTel.getText().toString())) {
            Toast.makeText(this, "请输入正确的手机号!", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(etPsd.getText().toString())) {
                Toast.makeText(this, "请输入注册密码!", Toast.LENGTH_SHORT).show();
            } else {
                if (!etPsd.getText().toString().equals(etPsdComfirm.getText().toString())) {
                    Toast.makeText(this, "确认密码与密码不符!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!checkBox.isChecked()) {
                        Toast.makeText(this, "请同意用户注册协议!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.setMessage("正在注册...");
                        progressDialog.show();
                        checkSMS();
                    }
                }
            }
        }
    }

    /**
     * 验证码通过正确，就注册发送服务器
     */
    private void checkSMS() {
        SMSSDK.getInstance().checkSmsCode(etTel.getText().toString(), etVerification.getText().toString(), new SmscheckListener() {
            @Override
            public void checkCodeSuccess(String s) {
                Log.i(TAG, "checkCodeSuccess: 验证成功：" + s);
                postData();
            }

            @Override
            public void checkCodeFail(int i, String s) {
                Log.i(TAG, "checkCodeSuccess: 验证失败：" + s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "验证码验证失败!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 向服务器发送注册信息的post
     */
    private void postData() {
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("password", etPsdComfirm.getText().toString());
        builder.addFormDataPart("mobile", etTel.getText().toString());
        MultipartBody requestBody = builder.build();
        Request requset = new Request.Builder()
                .url(ConstantValue.URL+"/login/register")
                .post(requestBody)
                .build();
        client.newCall(requset).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 服务器连接失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "注册失败!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 服务器连接成功" + response.body().string());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    int data = jsonObject.getInt("data");
                    SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
                    prefs.edit().putString("user_id", String.valueOf(data)).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, FinishActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    /**
     * 实现倒计时并发送请求并获取验证码的功能
     */
    private void receiverSMS() {
        btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实现倒计时
                countDownTimer();
                //请求验证码
                String tel = etTel.getText().toString();
                SMSSDK.getInstance().getSmsCode(tel, "1", new SmscodeListener() {
                    @Override
                    public void getCodeSuccess(String s) {
                        //获取成功时
                        Log.i(TAG, "获取验证码成功！ " + s);
                    }

                    @Override
                    public void getCodeFail(int i, String s) {
                        //获取失败时
                        Log.i(TAG, "获取验证码失败！ " + s);
                    }
                });
            }
        });
    }

    /**
     * 倒计时功能
     */
    private void countDownTimer() {
        VerificationCountDownTimer timer = new VerificationCountDownTimer(60000, 1000) {
            //倒计时过程
            @Override
            public void onTick(long millisUntilFinished) {
                super.onTick(millisUntilFinished);
                btnVerification.setClickable(false);//防止重新倒计时
                btnVerification.setText(millisUntilFinished / 1000 + "s");
            }

            //计时完毕的方法
            @Override
            public void onFinish() {
                //重新给Button设置文字
                btnVerification.setText("重新获取验证码");
                //设置可点击
                btnVerification.setClickable(true);
            }
        };
        timer.start();
    }

}
