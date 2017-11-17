package com.qryl.qrylyh.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qryl.qrylyh.R;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WalletActivity";

    private TextView tvMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
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
                break;
        }
    }
}
