package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qryl.qrylyh.R;

public class CompleteInfoActivity extends AppCompatActivity {

    private static final String TAG = "CompleteInfoActivity";

    private RelativeLayout myHead, realName, identity, gender, age, workExperience, hospital, beGoodAtWork, localService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);
        initView();
        hiddenSomeView();
    }

    /**
     * 根据传来的Extra值来让一些View隐藏
     */
    private void hiddenSomeView() {
        Intent intent = getIntent();
        String resultHg = intent.getStringExtra("skip_hg");
        String resultHs = intent.getStringExtra("skip_hs");
        String resultTn = intent.getStringExtra("skip_tn");
        String resultYs = intent.getStringExtra("skip_ys");

        if (resultHg.equals("hg")) {
            Log.i(TAG, "护工界面");
        } else if (resultHs.equals("hs")) {
            Log.i(TAG, "护士界面");
        } else if (resultTn.equals("ys")) {
            Log.i(TAG, "医生界面");
        } else if (resultYs.equals("tn")) {
            Log.i(TAG, "推拿界面");
        }
    }

    private void initView() {
        myHead = (RelativeLayout) findViewById(R.id.my_head);
        realName = (RelativeLayout) findViewById(R.id.real_name);
        identity = (RelativeLayout) findViewById(R.id.identity);
        gender = (RelativeLayout) findViewById(R.id.gender);
        age = (RelativeLayout) findViewById(R.id.age);
        workExperience = (RelativeLayout) findViewById(R.id.work_experience);
        hospital = (RelativeLayout) findViewById(R.id.hospital);
        beGoodAtWork = (RelativeLayout) findViewById(R.id.be_good_at_work);
        localService = (RelativeLayout) findViewById(R.id.local_service);
    }
}
