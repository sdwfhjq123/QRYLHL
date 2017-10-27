package com.qryl.qrylyh.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.compile.HgCompileInfoActivity;
import com.qryl.qrylyh.activity.compile.HsCompileInfoActivity;
import com.qryl.qrylyh.activity.compile.TnCompileInfoActivity;
import com.qryl.qrylyh.activity.compile.YsCompileInfoActivity;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by hp on 2017/8/16.
 */

public class MeFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "MeFragment";

    private TextView tvInfo;
    private ImageView imageView;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvProfession;
    private TextView tvTel;
    private String userId;
    private int roleType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = UIUtils.inflate(R.layout.fragment_me);
        //取登录成功后传过来的id及类型
        SharedPreferences prefs = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roleType = prefs.getInt("role_type", 0);
        Log.i(TAG, "onCreateView: 登录的用户userId:" + userId + ",类型是" + roleType);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        //在这里根据登录用户的类型判断不同的请求地址
        if (roleType == 0) {//护工
            postData(ConstantValue.URL + "/carer/getMyInfo");
            Log.i(TAG, "initData: 0");
            tvProfession.setText("护工");
        } else if (roleType == 1 || roleType == 2) {
            postData(ConstantValue.URL + "/dn/getMyInfo");
            Log.i(TAG, "initData: 1,2");
            if (roleType == 1) {
                tvProfession.setText("医生");
            } else if (roleType == 2) {
                tvProfession.setText("护士");
            }
        } else if (roleType == 3) {
            postData(ConstantValue.URL + "/massager/getMyInfo");
            Log.i(TAG, "initData: 3");
            tvProfession.setText("推拿师");
        }

    }

    private void postData(String address) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("loginId", String.valueOf(userId));
        FormBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "onResponse: " + result);
                handleJson(result);
            }
        });
    }

    /**
     * 解析json
     *
     * @param result
     */
    private void handleJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            final String realName = data.getString("realName");
            final int gender = data.getInt("gender");
            final String headshotImg = data.getString("headshotImg");
            JSONObject loginBean = data.getJSONObject("loginBean");
            final String mobile = loginBean.getString("mobile");
            Log.i(TAG, "handleJson: 获取的头像url:" + headshotImg);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvInfo.setText(realName);
                    Glide.with(getActivity()).load(ConstantValue.URL + headshotImg).into(imageView);
                    tvName.setText(realName);
                    tvGender.setText(gender == 0 ? "男" : "女");
                    //tvProfession.setText(healthCareNum);
                    tvTel.setText(mobile);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化控件
     *
     * @param view 绑定的layout
     */
    private void initView(View view) {
        //修改标头
        View viewTitle = View.inflate(UIUtils.getContext(), R.layout.title, null);
        TextView helpName = (TextView) viewTitle.findViewById(R.id.help_name);
        TextView tvHelp = (TextView) viewTitle.findViewById(R.id.return_text);
        helpName.setVisibility(View.VISIBLE);
        tvHelp.setVisibility(View.GONE);
        //需要刷新的列表
        tvInfo = (TextView) view.findViewById(R.id.tv_id);
        imageView = (ImageView) view.findViewById(R.id.iv_head);
        LinearLayout llCompile = (LinearLayout) view.findViewById(R.id.ll_compile);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvGender = (TextView) view.findViewById(R.id.tv_gender);
        tvProfession = (TextView) view.findViewById(R.id.tv_profession);
        tvTel = (TextView) view.findViewById(R.id.tv_tel);
        llCompile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据类型跳转不同的注册界面
                if (roleType == 0) {
                    Intent intent = new Intent(getActivity(), HgCompileInfoActivity.class);
                    startActivity(intent);
                } else if (roleType == 1) {
                    Intent intent = new Intent(getActivity(), YsCompileInfoActivity.class);
                    startActivity(intent);
                } else if (roleType == 2) {
                    Intent intent = new Intent(getActivity(), HsCompileInfoActivity.class);
                    startActivity(intent);
                } else if (roleType == 3) {
                    Intent intent = new Intent(getActivity(), TnCompileInfoActivity.class);
                    startActivity(intent);
                }
            }
        });
        Button btnExit = (Button) view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.qryl.qrylyh.activity.BaseActivity.ForceOfflineReceiver");
                getActivity().sendBroadcast(intent);

            }
        });
    }

}
