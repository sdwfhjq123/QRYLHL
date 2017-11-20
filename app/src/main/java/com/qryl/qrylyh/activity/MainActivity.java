package com.qryl.qrylyh.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.fragment.HomeHgFragment;
import com.qryl.qrylyh.fragment.HomeOtherFragment;
import com.qryl.qrylyh.fragment.MeFragment;
import com.qryl.qrylyh.fragment.MsgFragment;
import com.qryl.qrylyh.fragment.OrderFragment;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    private static final String ME_FRAGMENT = "ME_FRAGMENT";
    private static final String MSG_FRAGMENT = "MSG_FRAGMENT";
    private static final String ORDER_FRAGMENT = "ORDER_FRAGMENT";

    private TextView tvTitle;
    private android.support.v4.app.FragmentManager fm;
    private android.support.v4.app.FragmentTransaction ft;
    private int roleType;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        roleType = prefs.getInt("role_type", 0);
        Log.i(TAG, "onCreate: " + roleType);
        initUI();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        initFragment();
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        setTitleName("亲仁医疗护理");
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        if (roleType == 0) {
            ft.replace(R.id.fl_home, new HomeHgFragment(), HOME_FRAGMENT);
        } else if (roleType == 1 || roleType == 2 || roleType == 3) {
            ft.replace(R.id.fl_home, new HomeOtherFragment(), HOME_FRAGMENT);
        }
        ft.commit();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        hiddenView();
        RadioButton rbHome = (RadioButton) findViewById(R.id.rb_home);
        RadioButton rbOrder = (RadioButton) findViewById(R.id.rb_order);
        RadioButton rbMsg = (RadioButton) findViewById(R.id.rb_msg);
        RadioButton rbMe = (RadioButton) findViewById(R.id.rb_me);
        tvTitle = (TextView) findViewById(R.id.title_name);
        rbHome.setOnClickListener(this);
        rbOrder.setOnClickListener(this);
        rbMsg.setOnClickListener(this);
        rbMe.setOnClickListener(this);
    }

    private void hiddenView() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        tvReturn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        ft = fm.beginTransaction();
        switch (v.getId()) {
            //首页
            case R.id.rb_home:
                setTitleName("亲仁医疗护理");
                //未判断加载哪个网页
                if (roleType == 0) {
                    ft.replace(R.id.fl_home, new HomeHgFragment(), HOME_FRAGMENT);
                } else if (roleType == 1 || roleType == 2 || roleType == 3) {
                    ft.replace(R.id.fl_home, new HomeOtherFragment(), HOME_FRAGMENT);
                }
                break;
            //定单
            case R.id.rb_order:
                setTitleName("订单");
                ft.replace(R.id.fl_home, new OrderFragment(), ORDER_FRAGMENT);
                break;
            //消息
            case R.id.rb_msg:
                setTitleName("消息");
                ft.replace(R.id.fl_home, new MsgFragment(), MSG_FRAGMENT);
                break;
            //我的
            case R.id.rb_me:
                setTitleName("我的");
                ft.replace(R.id.fl_home, new MeFragment(), ME_FRAGMENT);
                break;
        }
        ft.commit();
    }

    /**
     * 点击最下面四个按钮式切换标题的名字
     *
     * @param name 需要传入修改的name
     */
    private void setTitleName(String name) {
        tvTitle.setText(name);
    }
}
