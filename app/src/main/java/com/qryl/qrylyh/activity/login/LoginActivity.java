package com.qryl.qrylyh.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.activity.MainActivity;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.view.PasswordToggleEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private AppCompatEditText etUser;
    private PasswordToggleEditText etPsd;
    private int id;
    private int roleType;
    private CheckBox cbAuto;
    private SharedPreferences prefs;
    private String registrationID;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        //自动登录逻辑
        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        if (prefs.getBoolean("is_auto_login", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        etUser = (AppCompatEditText) findViewById(R.id.et_user_login);
        etPsd = (PasswordToggleEditText) findViewById(R.id.et_psd_login);
        Button btnRegister = (Button) findViewById(R.id.btn_register_login);
        cbAuto = (CheckBox) findViewById(R.id.cb_auto_login);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btn_login_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAuto.isChecked() && TextUtils.isEmpty(etUser.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "自动登录无法保存您的账号", Toast.LENGTH_SHORT).show();
                } else if (cbAuto.isChecked() || !cbAuto.isChecked()) {
                    //注册极光唯一registrationId
                    registrationID = JPushInterface.getRegistrationID(LoginActivity.this);
                    Log.i(TAG, "registrationID" + registrationID);

                    String psd = etPsd.getText().toString();
                    String user = etUser.getText().toString();
                    if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(user)) {
                        postData(user, psd);
                    }
                }
            }
        });

        //判断是否登录
        cbAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAuto.isChecked()) {
                    cbAuto.setChecked(true);
                    prefs.edit().putBoolean("is_auto_login", cbAuto.isChecked()).apply();
                    Log.i(TAG, "保存checkbox状态:" + cbAuto.isChecked());
                } else {
                    cbAuto.setChecked(false);
                    prefs.edit().clear().apply();
                }

            }
        });
    }

    /**
     * 登录
     *
     * @param user 用户输入的登录的id
     * @param psd  用户输入的登录密码
     */
    private void postData(String user, String psd) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("mobile", user);
        builder.add("password", psd);
        builder.add("registrationId", registrationID);
        FormBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url(ConstantValue.URL + "/login/login")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "run: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        id = data.getInt("loginId");
                        roleType = data.getInt("roleType");
                        token = data.getString("token");
                        Log.i(TAG, "onResponse: " + roleType);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
                                prefs.edit().putString("user_id", String.valueOf(id)).apply();
                                prefs.edit().putInt("role_type", roleType).apply();
                                prefs.edit().putString("token", token).apply();
                                prefs.edit().putBoolean("is_force_offline", false).apply();
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else if (resultCode.equals("500")) {
                        final String errorMessage = jsonObject.getString("erroMessage");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
