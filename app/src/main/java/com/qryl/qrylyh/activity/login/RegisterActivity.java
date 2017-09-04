package com.qryl.qrylyh.activity.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qryl.qrylyh.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        Button btnRegister = (Button) findViewById(R.id.btn_register_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleServer();
                startActivity(new Intent(RegisterActivity.this, FinishActivity.class));
                finish();
            }
        });
    }

    /**
     * 向服务器发送注册信息的post
     */
    private void handleServer() {
    }
}
