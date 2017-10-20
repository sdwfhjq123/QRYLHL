package com.qryl.qrylyh.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.HttpUtil;

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
 * Created by yinhao on 2017/9/24.
 */

public class HomeOtherFragment extends Fragment {
    private static final String TAG = "HomeOtherFragment";
    private static final String UP = "我要上班接单";
    private static final String DOWN = "我要下班回家";
    private String userId;
    private TextView tvStatus;
    private TextView tvName;
    private TextView tvPatient;
    private TextView tvServiceTimes;
    private TextView tvParticulars;
    private int status;
    private Button button;
    private LinearLayout llPatient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_other, null);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        initView(view);
        initData();
        return view;
    }

    /**
     * 加载数据
     */
    private void initData() {
        //初始化页面时加载的状态
        postData();
    }

    /**
     * 初始化页面时加载的状态
     */
    private void postData() {
        HttpUtil.sendOkHttpRequestInt(ConstantValue.URL + "/order/getHomePageInfo", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "获取医护首页信息" + result);
                handleJson(result);
            }
        }, "loginId", userId);
    }

    private void handleJson(final String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            final JSONObject jo = jsonObject.getJSONObject("data");
            final String serviceTimes = jo.getString("serviceTimes");
            JSONObject doctorNurse = jo.getJSONObject("doctorNurse");
            final String realName = doctorNurse.getString("realName");
            final String patient = jo.getString("patient");
            status = jo.getInt("status");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvServiceTimes.setText(serviceTimes + "");
                    if (tvServiceTimes.getText().toString().equals("null")) {
                        tvServiceTimes.setText(0 + "");
                    } else {
                        tvServiceTimes.setText(serviceTimes + "");
                    }
                    tvName.setText(realName);
                    if (status == 0) {//空闲
                        button.setText(UP);
                        tvStatus.setText("未上班");
                        tvPatient.setText(patient);
                    } else if (status == 1) {//上班未接单
                        button.setText(DOWN);
                        tvStatus.setText("已上班");
                        tvPatient.setText(patient);
                    } else if (status == 2) {
                        button.setText(DOWN);
                        tvStatus.setText("已上班");
                        tvPatient.setText(patient);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvPatient = (TextView) view.findViewById(R.id.tv_patient);
        tvServiceTimes = (TextView) view.findViewById(R.id.tv_service_times);
        tvParticulars = (TextView) view.findViewById(R.id.tv_particulars);
        button = (Button) view.findViewById(R.id.button);
        llPatient = (LinearLayout) view.findViewById(R.id.ll_patient);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals(UP)) {
                    getStatus(1 + "", DOWN);
                } else if (button.getText().toString().equals(DOWN)) {
                    getStatus(0 + "", UP);
                }
            }
        });
    }

    private void getStatus(String status, final String buttonText) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("loginId", userId);//以后修改成userId
        builder.add("status", status);
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(ConstantValue.URL + "/order/getHomePageInfo")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 点击上班时获取的状态" + response.body().string());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText(buttonText);
                    }
                });
            }
        });
    }
}
