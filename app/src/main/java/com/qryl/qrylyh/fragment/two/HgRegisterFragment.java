package com.qryl.qrylyh.fragment.two;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.fragment.BaseFragment;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.DoubleDatePickerDialog;
import com.qryl.qrylyh.util.HttpUtil;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yinhao on 2017/9/24.
 */

public class HgRegisterFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "HgRegisterFragment";

    private TextView tvServiceDay;
    private TextView tvStatus;
    private Button button;
    private CheckBox cbEightHours;
    private CheckBox cbTwelveHours;
    private CheckBox cbTwentyFourHours;
    private Map<String, Integer> map;
    private String chooseServiceTimes;
    private String userId;
    private int status;
    private TextView tvServiceTimes;

    @Override
    public void loadData() {
        //把可服务的时间区域上传到服务器
        chooseServiceTimes = getChooseServiceTimes();
    }

    @Override
    public View initView() {
        SharedPreferences prefs = mContext.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        View view = View.inflate(mContext, R.layout.fragment_hg_register, null);
        LinearLayout llChooseTime = (LinearLayout) view.findViewById(R.id.ll_choose_time);
        cbEightHours = (CheckBox) view.findViewById(R.id.cb_eight_hours);
        cbTwelveHours = (CheckBox) view.findViewById(R.id.cb_twelve_hours);
        cbTwentyFourHours = (CheckBox) view.findViewById(R.id.cb_twenty_four_hours);
        tvServiceDay = (TextView) view.findViewById(R.id.tv_service_day);
        button = (Button) view.findViewById(R.id.button);
        tvServiceTimes = (TextView) view.findViewById(R.id.tv_service_times);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        LinearLayout llStatus = (LinearLayout) view.findViewById(R.id.ll_status);
        llChooseTime.setOnClickListener(this);
        cbEightHours.setOnClickListener(this);
        cbTwelveHours.setOnClickListener(this);
        cbTwentyFourHours.setOnClickListener(this);
        button.setOnClickListener(this);
        if (tvStatus.getText().equals("已接单,点击查看详情")) {
            llStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点开详情
                }
            });
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_choose_time:
                TimeSelector timeSelectorStart = new TimeSelector(getActivity(), new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        tvServiceDay.setText(time + ":00");
                    }
                }, getCurrentTime(), "2018-12-31 00:00");
                timeSelectorStart.setTitle("请选择可开始时间");
                timeSelectorStart.show();
                break;
            case R.id.button:
                if (button.getText().toString().equals("发布")) {
                    if (cbEightHours.isChecked() || cbTwelveHours.isChecked() || cbTwentyFourHours.isChecked()) {
                        if (!tvServiceDay.getText().toString().equals("请选择")) {
                            //发布选择的信息
                            postData();
                        } else {
                            Toast.makeText(getActivity(), "请选择可以开始服务的时间", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "您还未选择可服务时长", Toast.LENGTH_SHORT).show();
                    }
                }
                if (button.getText().toString().equals("撤销")) {
                    //撤销登记信息
                    revocationInfo();
                }
                break;
        }
    }

    /**
     * 撤销登记信息
     */
    private void revocationInfo() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", "1");//有疑问，id
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(ConstantValue.URL+"/publishCarer/delPublish")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 撤销的消息");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("登记");
                    }
                });
            }
        });
    }

    /**
     * 登记
     */
    private void postData() {
        OkHttpClient client = new OkHttpClient();
        final FormBody.Builder builder = new FormBody.Builder();
        builder.add("serviceTime", tvServiceDay.getText().toString());
        builder.add("serviceHours", chooseServiceTimes);
        builder.add("carerId", userId);
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(ConstantValue.URL+"/publishCarer/addPublish")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 登记成功");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "登记成功", Toast.LENGTH_LONG).show();
                        if (button.getText().toString().equals("发布")) {
                            button.setText("撤销");
                        }
                    }
                });
            }
        });
    }

    public String getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        Toast.makeText(getActivity(), date, Toast.LENGTH_SHORT).show();
        return date;
    }

    public String getChooseServiceTimes() {
        map = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        if (cbEightHours.isChecked()) {
            map.put("8", 8);
            sb.append(map.get("8"));
        } else {
            map.remove("8");
        }
        if (cbTwelveHours.isChecked()) {
            map.put("12", 12);
        } else {
            map.remove("12");
        }
        if (cbTwentyFourHours.isChecked()) {
            map.put("24", 24);
        } else {
            map.remove("24");
        }
        //循环map
        for (Map.Entry<String, Integer> map : this.map.entrySet()) {
            sb.append(map.getValue() + ",");
        }
        return sb.toString();
    }
}
