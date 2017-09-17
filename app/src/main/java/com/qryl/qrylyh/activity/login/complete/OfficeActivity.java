package com.qryl.qrylyh.activity.login.complete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.qryl.qrylyh.R;

/**
 * http://192.168.2.134:8080/qryl/manager/getDepartments
 */
public class OfficeActivity extends AppCompatActivity {

    private TextView titleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);
        initView();
        titleName.setText("选择科室");
    }

    private void initView() {
        titleName = (TextView) findViewById(R.id.title_name);
    }
}
