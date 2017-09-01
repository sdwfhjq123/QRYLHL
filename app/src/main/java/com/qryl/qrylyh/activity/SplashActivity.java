package com.qryl.qrylyh.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.qryl.qrylyh.R;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout rlSplash;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initAnim();
        //设置全屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //new个Handler
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {//延迟多少秒执行Runnable()
                    @Override
                    public void run() {
                        splashJump();
                    }
                }, 3000);//3秒后跳转
            }
        }).start();

    }

    private void initAnim() {
        AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
        alphaAnim.setDuration(2000);
        rlSplash.startAnimation(alphaAnim);
    }


    /**
     * 跳转
     */
    private void splashJump() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void initView() {
        rlSplash = (RelativeLayout) findViewById(R.id.rl_splash);
    }
}
