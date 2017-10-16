package com.qryl.qrylyh.activity.H5;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationManagerActivity extends BaseActivity {

    private static final String TAG = "LocationManagerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);
        initView();
        initData();
    }

    private void initData() {
        postData();
    }

    /**
     * 加载网络
     */
    private void postData() {
        HttpUtil.sendOkHttpRequest("http://192.168.2.134:8080/qryl/serviceArea/getServiceAreaById", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 获取服务区域失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 获取服务区域成功");
                String result = response.body().string();
                handleJson(result);
            }
        }, "id", 1);
    }

    /**
     * 解析json
     *
     * @param result
     */
    private void handleJson(String result) {
        Log.i(TAG, "handleJson: ");
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jb = jsonObject.getJSONObject("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        hiddenView();
        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        Button btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationManagerActivity.this, LocationAddActivity.class);
            }
        });
    }

    /**
     * 隐藏并修改某些网页
     */
    private void hiddenView() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        TextView tvHelp = (TextView) findViewById(R.id.help_name);
        tvReturn.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvHelp.setVisibility(View.GONE);
        tvTitle.setText("管理常用服务区域");
        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
