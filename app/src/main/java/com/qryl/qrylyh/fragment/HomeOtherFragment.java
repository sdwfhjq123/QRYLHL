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
import android.widget.Toast;

import com.google.gson.Gson;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.HomeOtherVO.HomeOther;
import com.qryl.qrylyh.activity.H5.WritePatientsFileActivity;
import com.qryl.qrylyh.activity.MainActivity;
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

    private static final String STATUS_DOWN = "未上班";
    private static final String STATUS_UP = "已上班";
    private static final String STATUS_ALREADY = "已接单,点击查看病人详情";

    private String userId;
    private TextView tvStatus;
    private TextView tvName;
    private TextView tvClickable;
    private TextView tvServiceTimes;
    private int status;
    private Button button;
    private LinearLayout llPatient;
    private String orderId;
    private int patientId;
    private int puId;
    private int serviceNum;
    private String name;
    private int roleType;
    private String address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_other, null);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roleType = prefs.getInt("role_type", 0);
        initView(view);
        initData();
        return view;
    }

    /**
     * 加载数据
     */
    private void initData() {
        //初始化页面时加载的状态
        if (roleType == 3) {
            address = ConstantValue.URL + "/order/getMassagerHomePageInfo";
        } else {
            address = ConstantValue.URL + "/order/getHomePageInfo";
        }
        postData(address);
    }

    /**
     * 初始化页面时加载的状态
     *
     * @param address 根据不同的角色请求不同的url
     */
    private void postData(String address) {
        HttpUtil.sendOkHttpRequestInt(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "获取医护首页信息" + result);
                try {
                    final JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("500")) {
                        if (getActivity() instanceof MainActivity) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getActivity(), jsonObject.getString("erroMessage"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else if (resultCode.equals("200")) {//有订单
                        handleJson(result);
                    } else if (resultCode.equals("201")) {//没有订单
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONObject resultObject = data.getJSONObject("result");
                        final String realName = resultObject.getString("realName");
                        final int serviceNum = resultObject.getInt("serviceNum");
                        final int status = resultObject.getInt("status");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvName.setText(realName);
                                tvServiceTimes.setText(serviceNum + "");
                                tvStatus.setText(status == 0 ? STATUS_DOWN : STATUS_UP);
                                button.setText(status == 0 ? UP : DOWN);
                                tvClickable.setText(status == 0 ? STATUS_DOWN : STATUS_UP);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "loginId", userId);
    }

    /**
     * 有订单时解析此类
     *
     * @param result
     */
    private void handleJson(String result) {
        Gson gson = new Gson();
        HomeOther homeOther = gson.fromJson(result, HomeOther.class);
        final String resultCode = homeOther.getResultCode();
        status = homeOther.getData().getDoctorNurse().getStatus();
        serviceNum = homeOther.getData().getDoctorNurse().getServiceNum();
        //订单id
        orderId = homeOther.getData().getId();
        //病人id
        patientId = homeOther.getData().getPatient().getId();
        //病人的名字
        name = homeOther.getData().getPatient().getName();
        //病患端用户登录id
        puId = homeOther.getData().getPatient().getPuId();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvServiceTimes.setText(serviceNum + "");
                tvName.setText(name);
                button.setText(DOWN);
                tvStatus.setText("已上班");
                tvClickable.setText(STATUS_ALREADY);
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
        tvClickable = (TextView) view.findViewById(R.id.tv_clickable);
        tvServiceTimes = (TextView) view.findViewById(R.id.tv_service_times);
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

        llPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvClickable.getText().toString().equals(STATUS_ALREADY)) {
                    //点击查看详情
                    WritePatientsFileActivity.actionStart(getActivity(), puId, patientId, orderId);
                } else if ((tvClickable.getText().toString().equals(STATUS_DOWN)) ||
                        (tvClickable.getText().toString().equals(STATUS_UP))) {
                    Toast.makeText(getActivity(), "未接到单子，无法点击查看患者详情", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getStatus(final String status, final String buttonText) {
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
                if (getActivity() instanceof MainActivity) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                            button.setText(buttonText);
                            tvClickable.setText((Integer.parseInt(status)) == 0 ? STATUS_DOWN : STATUS_UP);
                        }
                    });
                }
            }
        });
    }
}
