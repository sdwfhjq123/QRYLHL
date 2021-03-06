package com.qryl.qrylyh.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.activity.login.complete.ChooseAreaActivity;

public class FinishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        initView();
    }

    private void initView() {
        TextView tvComplete = (TextView) findViewById(R.id.tv_complete);
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinishActivity.this, ChooseAreaActivity.class));
                finish();
            }
        });
    }
}
