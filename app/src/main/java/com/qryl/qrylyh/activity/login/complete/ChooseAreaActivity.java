package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qryl.qrylyh.R;

/**
 * 选择职业
 */
public class ChooseAreaActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SKIP_HG = "skip_hg";
    public static final String SKIP_HS = "skip_hs";
    public static final String SKIP_YS = "skip_ys";
    public static final String SKIP_TN = "skip_tn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        initView();
    }

    private void initView() {
        Button btnHg = (Button) findViewById(R.id.btn_choose_hg);
        Button btnHs = (Button) findViewById(R.id.btn_choose_hs);
        Button btnYs = (Button) findViewById(R.id.btn_choose_ys);
        Button btnTn = (Button) findViewById(R.id.btn_choose_tn);
        btnHg.setOnClickListener(this);
        btnHs.setOnClickListener(this);
        btnYs.setOnClickListener(this);
        btnTn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_hg://点击跳转护工注册页面
                Intent intent = new Intent(ChooseAreaActivity.this, CompleteInfoActivity.class);
                intent.putExtra(SKIP_HG, "hg");
                startActivity(intent);
                break;
            case R.id.btn_choose_hs://点击跳转护士注册页面
                Intent intent2 = new Intent(ChooseAreaActivity.this, CompleteInfoActivity.class);
                intent2.putExtra(SKIP_HS, "hs");
                startActivity(intent2);
                break;
            case R.id.btn_choose_ys://点击跳转医生注册页面
                Intent intent3 = new Intent(ChooseAreaActivity.this, CompleteInfoActivity.class);
                intent3.putExtra(SKIP_YS, "ys");
                startActivity(intent3);
                break;
            case R.id.btn_choose_tn://点击跳转推拿注册页面
                Intent intent4 = new Intent(ChooseAreaActivity.this, CompleteInfoActivity.class);
                intent4.putExtra(SKIP_TN, "tn");
                startActivity(intent4);
                break;
        }
    }
}
