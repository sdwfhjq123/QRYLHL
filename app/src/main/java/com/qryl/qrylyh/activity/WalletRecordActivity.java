package com.qryl.qrylyh.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.qryl.qrylyh.R;

public class WalletRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_record);
        initView();
    }

    private void initView() {
        hiddenSomeView();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //FrecyclerView.setAdapter(adapter);
    }

    private void hiddenSomeView() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        TextView tvHelp = (TextView) findViewById(R.id.help_name);
        tvTitle.setText("我的消费记录");
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

        }
    }
}
