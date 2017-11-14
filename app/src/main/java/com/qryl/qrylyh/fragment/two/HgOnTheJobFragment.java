package com.qryl.qrylyh.fragment.two;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.MainActivity;
import com.qryl.qrylyh.fragment.BaseFragment;
import com.qryl.qrylyh.util.ConstantValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yinhao on 2017/9/24.
 */

public class HgOnTheJobFragment extends BaseFragment {
    private static final String TAG = "HgOnTheJobFragment";

    private LinearLayout llDisplay;//正常显示的布局
    private LinearLayout llFailed;//未登记的布局
    private String userId;
    private LinearLayout llParticular;
    private View viewline;
    private TextView tvServiceDay;
    private TextView tvPatient;
    private TextView tvMountGuard;
    private TextView tvMountGuardLocation;
    private TextView tvPatientStatus;
    private TextView tvTel;

    @Override
    public void loadData() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        //builder.add("userId", userId);
        builder.add("loginId", String.valueOf(2));
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(ConstantValue.URL+"/order/getShangGangInfo")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "onResponse: 上岗后显示的信息..." + result);
                llDisplay.setVisibility(View.VISIBLE);
                llFailed.setVisibility(View.GONE);
                handleJson(result);
            }
        });
    }

    /**
     * @param result
     */
    private void handleJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            final int serviceOverDays = data.getInt("serviceOverDays");
            final int selfCare = data.getInt("selfCare");//病人状态 0失能 1能够自理
            final long startTime = data.getLong("startTime");
            JSONObject addr = data.getJSONObject("addr");
            final String province = addr.getString("province");
            final String city = addr.getString("city");
            final String district = addr.getString("district");
            final String specificSite = addr.getString("specificSite");
            JSONObject patient = data.getJSONObject("patient");
            final String patientName = patient.getString("name");
            final String mobile = patient.getString("mobile");
            if (getActivity() instanceof MainActivity){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s = long2Date(startTime);
                        tvServiceDay.setText("已服务" + serviceOverDays + "天");
                        tvPatient.setText(patientName);
                        tvMountGuard.setText(s);
                        tvMountGuardLocation.setText(province + " " + city + " " + district + "" + specificSite);
                        if (selfCare == 0) {
                            tvPatientStatus.setText("失能");
                        } else {
                            tvPatientStatus.setText("自理");
                        }
                        tvTel.setText(mobile);
                        llParticular.setVisibility(View.VISIBLE);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把long类型的时间转换成date类型
     *
     * @param startTime
     */
    private String long2Date(long startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //前面的startTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        java.util.Date dt = new Date(startTime );
        String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
        return sDateTime;
    }

    @Override
    public View initView() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        View view = View.inflate(mContext, R.layout.fragment_hg_onthejob, null);
        llDisplay = (LinearLayout) view.findViewById(R.id.ll_display);
        llFailed = (LinearLayout) view.findViewById(R.id.ll_failed);
        llParticular = (LinearLayout) view.findViewById(R.id.ll_particular);
        llParticular.setVisibility(View.INVISIBLE);
        viewline = view.findViewById(R.id.viewline);
        tvServiceDay = (TextView) view.findViewById(R.id.tv_service_day);
        tvPatient = (TextView) view.findViewById(R.id.tv_patient);
        tvMountGuard = (TextView) view.findViewById(R.id.tv_mount_guard);
        tvMountGuardLocation = (TextView) view.findViewById(R.id.tv_mount_guard_location);
        tvPatientStatus = (TextView) view.findViewById(R.id.tv_patient_status);
        tvTel = (TextView) view.findViewById(R.id.tv_tel);
        return view;
    }
}
