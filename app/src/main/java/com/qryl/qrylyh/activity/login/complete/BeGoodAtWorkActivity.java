package com.qryl.qrylyh.activity.login.complete;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.BeGoodAtWorkVO.BeGoodAtWork;
import com.qryl.qrylyh.VO.BeGoodAtWorkVO.Data;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.adapter.WorkAdapter;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BeGoodAtWorkActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BeGoodAtWorkActivity";

    private List<Data> datas = new ArrayList<>();
    private WorkAdapter adapter = new WorkAdapter(datas);
    private int serviceId;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, String> getDataMap = new HashMap<>();
    private int addId;
    private String addName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_good_at_work);
        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", 0);
        initView();
        initData();
    }

    /**
     * 加载数据
     */
    private void initData() {
        postData();
    }

    private void postData() {
        HttpUtil.sendOkHttpRequest(ConstantValue.URL + "/common/getProfessionListByRoleType", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                handlerJson(result);
            }
        }, "roleType", serviceId);
    }

    /**
     * 解析json
     *
     * @param result 解析出来的结果
     */
    private void handlerJson(String result) {
        Gson gson = new Gson();
        BeGoodAtWork beGoodAtWork = gson.fromJson(result, BeGoodAtWork.class);
        List<Data> data = beGoodAtWork.getData();
        for (int i = 0; i < data.size(); i++) {
            datas.add(new Data(data.get(i).getId(), data.get(i).getName()));
        }
        adapter.setData(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("选择擅长的工作");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Button btnSure = (Button) findViewById(R.id.btn_sure);
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        btnSure.setOnClickListener(this);
        tvReturn.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new WorkAdapter.OnItemClickListener() {
            @Override
            public void onAddClickLister(View view, int position) {
                addId = datas.get(position).getId();
                addName = datas.get(position).getName();
                getDataMap.put(addId, addName);
                Log.i(TAG, "onAddClickLister: 增加了擅长的专业" + addName + " ，大小为" + getDataMap.size());
            }

            @Override
            public void onDeleteClickLister(View view, int position) {
                int addId = datas.get(position).getId();
                addName = datas.get(position).getName();
                getDataMap.remove(addId);
                Log.i(TAG, "onDeleteClickLister: 移除了擅长的专业" + addName + " ，大小为" + getDataMap.size());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                StringBuilder stringBufferId = new StringBuilder();
                StringBuilder stringBufferName = new StringBuilder();
                for (Map.Entry<Integer, String> entry : getDataMap.entrySet()) {
                    Integer key = entry.getKey();
                    stringBufferId.append(key);
                    stringBufferId.append(",");
                    String value = entry.getValue();
                    stringBufferName.append(value);
                    stringBufferName.append(",");
                }
                Intent intent = new Intent();
                intent.putExtra("work_name", stringBufferName.toString());
                intent.putExtra("work_id", stringBufferId.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.return_text:
                finish();
                break;
        }
    }
}
