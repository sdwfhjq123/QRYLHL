package com.qryl.qrylyh.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.login.LoginActivity;
import com.qryl.qrylyh.util.VerificationCountDownTimer;


public class SplashActivity extends BaseActivity {

    private RelativeLayout rlSplash;
    private Handler mHandler = new Handler();
    private VerificationCountDownTimer countDownTimer;
    private TextView mTimerTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //设置全屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
        initAnim();

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
        alphaAnim.setDuration(1000);
        rlSplash.startAnimation(alphaAnim);

        countdown();
    }

    private void countdown() {
        countDownTimer = new VerificationCountDownTimer(3000, 1000) {
            @Override
            public void onFinish() {
                super.onFinish();
                mTimerTextview.setText("倒计时:0");
            }

            @Override
            public void onTick(long millisUntilFinished) {
                super.onTick(millisUntilFinished);
                mTimerTextview.setText("倒计时:" + (millisUntilFinished / 1000));
            }
        };

        countDownTimer.start();
    }


    /**
     * 跳转
     */
    private void splashJump() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void initView() {
        rlSplash = (RelativeLayout) findViewById(R.id.rl_splash);
        mTimerTextview = (TextView) findViewById(R.id.timer_textview);
    }

}
