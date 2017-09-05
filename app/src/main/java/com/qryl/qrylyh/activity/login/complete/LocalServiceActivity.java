package com.qryl.qrylyh.activity.login.complete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.qryl.qrylyh.R;

public class LocalServiceActivity extends AppCompatActivity {

    private TextView titleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_service);
        initView();
        titleName.setText("选择区域");
    }

    private void initView() {
        titleName = (TextView) findViewById(R.id.title_name);
    }
}
