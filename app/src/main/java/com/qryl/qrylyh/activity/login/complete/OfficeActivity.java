package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.HospitalVO.DataArea;
import com.qryl.qrylyh.VO.WorkVO.Work;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.adapter.OfficeAdapter;
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

public class OfficeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "OfficeActivity";

    private RecyclerView recyclerView;
    private List<com.qryl.qrylyh.VO.WorkVO.DataArea> data = new ArrayList<>();
    private OfficeAdapter adapter = new OfficeAdapter(data);
    private boolean isLoading;
    private int page;
    private int lastVisibleItemPosition;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);
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
                .url(ConstantValue.URL+"/manager/getDepartments")//获取擅长的工作
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
        Work work = gson.fromJson(result, Work.class);
        total = work.getData().getTotal();
        List<com.qryl.qrylyh.VO.WorkVO.DataArea> dataAreas = work.getData().getData();
        //增加集合
        for (int i = 0; i < dataAreas.size(); i++) {
            this.data.add(new com.qryl.qrylyh.VO.WorkVO.DataArea(dataAreas.get(i).getId(), dataAreas.get(i).getName(), dataAreas.get(i).getNote()));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setData(OfficeActivity.this.data);
                adapter.notifyDataSetChanged();
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        });

    }

    private void initView() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText("选择科室");
        tvReturn.setOnClickListener(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "onScrollStateChanged: " + newState);
                if (lastVisibleItemPosition + 1 == adapter.getItemCount() && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    Log.i(TAG, "onScrolled: loading excute");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "onScrolled: ");
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    if (!isLoading) {
                        isLoading = true;
                        page += 1;
                        if (page <= total) {
                            postData(String.valueOf(page));
                        }
                        Log.i(TAG, "run: load more complete");
                        isLoading = false;
                    }
                }
            }
        });

        adapter.setOnItemClickListener(new OfficeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick: 点击了" + position);
                Intent intent = new Intent();
                intent.putExtra("office_id", data.get(position).getId());
                intent.putExtra("office_name", data.get(position).getName());
                setResult(RESULT_OK, intent);
                finish();
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
