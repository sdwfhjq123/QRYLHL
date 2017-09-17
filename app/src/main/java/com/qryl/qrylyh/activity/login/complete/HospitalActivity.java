package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import com.qryl.qrylyh.adapter.HospitalAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HospitalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HospitalActivity";

    private RecyclerView recyclerView;
    private List<DataArea> data = new ArrayList<>();
    private HospitalAdapter adapter = new HospitalAdapter(data);
    private int page = 1;
    private int lastVisibleItemPosition;
    private boolean isLoading;

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
    private void postData(String page) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("page", page);
        builder.add("limit", "3");
        FormBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url("http://192.168.2.134:8080/qryl/common/getHospitals")
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
     * @param result
     */
    private void handleJson(String result) {
        Gson gson = new Gson();
        Hospital hospital = gson.fromJson(result, Hospital.class);
        List<DataArea> data = hospital.getData().getData();
        //增加集合
        for (int i = 0; i < data.size(); i++) {
            Log.i(TAG, "handleJson: 解析dataArea" + data.get(i).getId());
            Log.i(TAG, "handleJson: 解析dataArea" + data.get(i).getId());
            Log.i(TAG, "handleJson: 解析dataArea" + data.get(i).getId());
            //data.get(i).setName("获取到数据" + i);
            data.add(data.get(i));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        });

    }

    private void initView() {
        TextView tvName = (TextView) findViewById(R.id.title_name);
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        tvName.setOnClickListener(this);
        tvReturn.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
                        initData(page);
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
                //intent.putExtra("hospital_id", hospitalList.get(position).getId());
                //intent.putExtra("hospital_name", hospitalList.get(position).getHospitalName());
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
            case R.id.btn_sure:

                break;
        }
    }
}
