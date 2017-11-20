package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
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
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LocationActivity";

    private ArrayList<County> counties = new ArrayList<>();
    private ArrayList<ArrayList<Row>> items = new ArrayList<>();

    private Map<String, Integer> getRowsMap = new HashMap<>();

    private LocalExpandableAdapter adapter;

    private int addId;
    private String addName;

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
        HttpUtil.sendOkHttpRequest(ConstantValue.URL + "/common/getAreaByCityId", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "得到的可服务区域: " + result);
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
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText("选择可服务的地址");
        tvReturn.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        ExpandableListView exList = (ExpandableListView) findViewById(R.id.exList);
        adapter = new LocalExpandableAdapter(counties, items, this);
        exList.setAdapter(adapter);
        adapter.setOnChooseItemClickListener(new LocalExpandableAdapter.OnChooseItemClickListener() {
            @Override
            public void onItemClick(View view, int groupPosition, int childPosition, int id) {
                Log.i(TAG, "onItemClick: id :" + items.get(groupPosition).get(childPosition).getId());
//                stringBuffer.append(items.get(groupPosition).get(childPosition).getName());
//                stringBuffer.append(",");
//                Log.i(TAG, "onItemClick: " + stringBuffer.toString());

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
                StringBuilder stringBufferName = new StringBuilder();
                StringBuilder stringBufferId = new StringBuilder();
                for (Map.Entry<String, Integer> entry : getRowsMap.entrySet()) {

                    String key = entry.getKey();
                    stringBufferName.append(key);
                    stringBufferName.append(",");

                    Integer value = entry.getValue();
                    stringBufferId.append(value);
                    stringBufferId.append(",");
                }
                Intent intent = new Intent();
                intent.putExtra("location_name", stringBufferName.toString());
                intent.putExtra("location_id", stringBufferId.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
