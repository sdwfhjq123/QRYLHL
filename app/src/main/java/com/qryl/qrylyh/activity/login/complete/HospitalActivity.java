package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.HospitalVO.DataArea;
import com.qryl.qrylyh.VO.HospitalVO.Hospital;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.adapter.HospitalAdapter;
import com.qryl.qrylyh.util.ConstantValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HospitalActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "HospitalActivity";

    private List<DataArea> data = new ArrayList<>();
    private HospitalAdapter adapter = new HospitalAdapter(data);
    private int page = 1;
    private int lastVisibleItemPosition;
    private boolean isLoading;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        initView();
        initData(page);
    }

    private void initData(int page) {
        //请求网络数据
        postData(String.valueOf(page));
    }

    /**
     * 请求网络数据
     */
    private void postData(final String page) {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("page", page);
        builder.add("limit", "3");
        FormBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url(ConstantValue.URL + "/common/getHospitals")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                handleJson(result);
            }
        });
    }

    /**
     * 处理获取下来的json
     *
     * @param result 解析出来的结果
     */
    private void handleJson(String result) {
        Gson gson = new Gson();
        Hospital hospital = gson.fromJson(result, Hospital.class);
        total = hospital.getData().getTotal();
        List<DataArea> dataAreas = hospital.getData().getData();
        //增加集合
        for (int i = 0; i < dataAreas.size(); i++) {
            Log.i(TAG, "handleJson: " + dataAreas.size());
            Log.i(TAG, "handleJson: 解析dataArea" + dataAreas.get(i).getName());
            Log.i(TAG, "handleJson: 解析dataArea" + total);
            this.data.add(new DataArea(dataAreas.get(i).getId(), dataAreas.get(i).getName(), dataAreas.get(i).getNote()));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setData(HospitalActivity.this.data);
                adapter.notifyDataSetChanged();
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        });

    }

    private void initView() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText("选择医院");
        tvReturn.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    if (!isLoading) {
                        isLoading = true;
                        page += 1;
                        Log.i(TAG, "onScrolled: page=" + page);
                        if (page <= total) {
                            postData(String.valueOf(page));
                        }
                        isLoading = false;
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new HospitalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Log.i(TAG, "onItemClick:获取到的数据 " + hospitalList.get(position).getId());
                //点击后结束页面
                Intent intent = new Intent();
                intent.putExtra("hospital_id", data.get(position).getId());
                intent.putExtra("hospital_name", data.get(position).getName());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onDeleteItemClick(View view, int position) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_text:
                finish();
                break;
        }
    }
}
