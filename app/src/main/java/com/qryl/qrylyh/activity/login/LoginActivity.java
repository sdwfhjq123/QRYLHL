package com.qryl.qrylyh.activity.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.MainActivity;
import com.qryl.qrylyh.util.HttpUtil;
import com.qryl.qrylyh.view.PasswordToggleEditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private AppCompatEditText etUser;
    private PasswordToggleEditText etPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        etUser = (AppCompatEditText) findViewById(R.id.et_user_login);
        etPsd = (PasswordToggleEditText) findViewById(R.id.et_psd_login);
        Button btnRegister = (Button) findViewById(R.id.btn_register_login);
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
                String psd = etPsd.getText().toString();
                String user = etUser.getText().toString();
                if (TextUtils.isEmpty(psd) && TextUtils.isEmpty(user)) {
                    postData(user, psd);
                }
            }
        });
    }

    /**
     * 登录
     *
     * @param user
     * @param psd
     */
    private void postData(String user, String psd) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("mobile", user);
        builder.add("password", psd);
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url("http://192.168.2.134:8080/qryl/login/login")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i(TAG, "run: " + response.body().string());
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
