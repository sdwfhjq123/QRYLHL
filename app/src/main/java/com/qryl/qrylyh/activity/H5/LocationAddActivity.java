package com.qryl.qrylyh.activity.H5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.County;
import com.qryl.qrylyh.VO.Row;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.adapter.LocalExpandableAdapter;
import com.qryl.qrylyh.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationAddActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LocationAddActivity";

    private ArrayList<County> counties = new ArrayList<>();
    private ArrayList<ArrayList<Row>> items = new ArrayList<>();

    private Map<String, Integer> getRowsMap = new HashMap<>();

    private LocalExpandableAdapter adapter;

    private ExpandableListView exList;

    private int addId;
    private String addName;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //数据准备
        initView();
        initData();
        //添加组名


    }

    private void initData() {
        postData();
    }

    private void postData() {
        HttpUtil.sendOkHttpRequest("http://192.168.2.134:8080/qryl/common/getAreaByCityId", new Callback() {
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
        }, "cityId", 370100);
    }

    private void handleJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int j = 0; j < data.length(); j++) {
                JSONObject county = data.getJSONObject(j);
                String countyName = county.getString("name");
                int countyId = county.getInt("id");
                Log.i(TAG, "handleJson:区:" + countyName + " ,id:" + countyId);
                counties.add(new County(countyName, countyId));
                JSONArray areaListArray = county.getJSONArray("areaList");
                ArrayList<Row> rows = new ArrayList<>();
                for (int x = 0; x < areaListArray.length(); x++) {
                    JSONObject areaListObject = areaListArray.getJSONObject(x);
                    String rowName = areaListObject.getString("name");
                    int rowId = areaListObject.getInt("id");
                    Log.i(TAG, "handleJson: 得到的街道名字" + rowName + " ,街道id" + rowId);
                    Row row = new Row(rowName, rowId);
                    rows.add(row);
                }
                items.add(rows);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        Button btnSure = (Button) findViewById(R.id.btn_sure);
        tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText("选择可服务的地址");
        tvReturn.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        exList = (ExpandableListView) findViewById(R.id.exList);
        adapter = new LocalExpandableAdapter(counties, items, this);
        exList.setAdapter(adapter);
        adapter.setOnChooseItemClickListener(new LocalExpandableAdapter.OnChooseItemClickListener() {
            @Override
            public void onItemClick(View view, int groupPosition, int childPosition, int id) {
                Log.i(TAG, "onItemClick: id :" + items.get(groupPosition).get(childPosition).getId());
                addName = items.get(groupPosition).get(childPosition).getName();
                addId = items.get(groupPosition).get(childPosition).getId();
                getRowsMap.put(addName, addId);
                Log.i(TAG, "onItemClick: " + getRowsMap.size());
            }

            @Override
            public void onDeleteItemClick(View view, int groupPosition, int childPosition) {
                addName = items.get(groupPosition).get(childPosition).getName();
                addId = items.get(groupPosition).get(childPosition).getId();
                getRowsMap.remove(addName);
                Log.i(TAG, "onDeleteItemClick: 移除了" + getRowsMap.get(addName) + " ,集合大小" + getRowsMap.size());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_text:
                finish();
                break;
            case R.id.btn_sure:
                StringBuffer sb = new StringBuffer();
                for (Map.Entry<String, Integer> map : getRowsMap.entrySet()) {
                    sb.append(map.getValue() + ",");
                }
                //点击确定将地址发到服务器上
                addLocationFromServer(sb);
                finish();
                break;
        }
    }

    private void addLocationFromServer(StringBuffer sb) {
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("userId", userId);
        builder.add("roleType", "1");
        builder.add("regionIds", sb.toString());
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url("http://192.168.2.134:8080/qryl/serviceArea/addServiceArea")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 获取成功返回的信息" + response.body().string());
            }
        });
    }
}
