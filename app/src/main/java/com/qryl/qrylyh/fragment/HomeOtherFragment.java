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
        HttpUtil.sendOkHttpRequestInt(ConstantValue.URL+"/dn/getHomePageInfo", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleJson(response.body().string());
            }
        }, "loginId", userId);//后期改成userId
    }

    private void handleJson(final String result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject jo = jsonObject.getJSONObject("data");
                    String realName = jo.getString("realName");
                    int serviceNum = jo.getInt("serviceNum");
                    JSONObject patient = jo.getJSONObject("patient");
                    String patientName = patient.getString("name");
                    tvServiceTimes.setText(serviceNum + "");
                    tvName.setText(realName);
                    status = jo.getInt("status");
                    if (status == 0) {//空闲
                        button.setText("我要上班接单");
                        tvStatus.setText("未上班");
                        tvPatient.setText(patientName);
                    } else if (status == 1) {//上班未接单
                        button.setText("我要下班回家");
                        tvStatus.setText("已上班");
                        tvPatient.setText(patientName);
                    } else if (status == 2) {//已接单
                        button.setText("我要下班回家");
                        tvStatus.setText("已接单");
                        tvPatient.setText(patientName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        if (button.getText().toString().equals("我要下班回家")) {
            llPatient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击查看详情
                }
            });
        } else if (button.getText().toString().equals("我要上班接单")) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("loginId", userId);//以后修改成userId
                    builder.add("status", String.valueOf(1));
                    FormBody formBody = builder.build();
                    Request request = new Request.Builder()
                            .post(formBody)
                            .url(ConstantValue.URL+"/dn/getHomePageInfo")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            postData();
                        }
                    });
                }
            });
        }
    }
}
