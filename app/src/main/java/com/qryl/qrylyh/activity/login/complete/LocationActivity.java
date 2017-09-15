package com.qryl.qrylyh.activity.login.complete;

import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.County;
import com.qryl.qrylyh.VO.Row;
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
import okhttp3.Response;

import static android.R.attr.name;
import static android.R.attr.pointerIcon;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";

    private ArrayList<County> counties = new ArrayList<>();
    private ArrayList<ArrayList<Row>> items = new ArrayList<>();

    private Map<String, Integer> getRowsMap = new HashMap<>();

    private LocalExpandableAdapter adapter;

    private ExpandableListView exList;

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
//                    Log.i(TAG, "handleJson: 得到的街道名字" + rowName + " ,街道id" + rowId);
                    Row row = new Row(rowName, rowId);
                    rows.add(row);
                }
                items.add(rows);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exList = (ExpandableListView) findViewById(R.id.exList);
        adapter = new LocalExpandableAdapter(counties, items, this);
        exList.setAdapter(adapter);
        final StringBuffer stringBuffer = new StringBuffer();
        adapter.setOnChooseItemClickListener(new LocalExpandableAdapter.OnChooseItemClickListener() {

            private int addId;
            private String addName;

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
}
