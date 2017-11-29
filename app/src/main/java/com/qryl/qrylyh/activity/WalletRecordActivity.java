package com.qryl.qrylyh.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.OrderVO.Order;
import com.qryl.qrylyh.VO.OrderVO.OrderInfoArea;
import com.qryl.qrylyh.VO.WalletRecordVO.Data;
import com.qryl.qrylyh.VO.WalletRecordVO.WalletRecord;
import com.qryl.qrylyh.adapter.OrderUnderwayAdapter;
import com.qryl.qrylyh.adapter.WalletRecordAdapter;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.EncryptionByMD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalletRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WalletRecordActivity";

    private SwipeRefreshLayout swipeRefresh;

    private List<Data> datas = new ArrayList<>();
    private WalletRecordAdapter adapter = new WalletRecordAdapter(datas);
    private int lastVisibleItemPosition;
    private boolean isLoading;
    private String userId;
    private String token;
    private SharedPreferences prefs;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_record);
        initView();
        //请求网络数据
        postData();
    }

    /**
     * 请求网络数据
     */
    private void postData() {
        Log.i(TAG, "postData: userId" + userId);
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        byte[] bytes = ("/test/common/getMyApplyList-" + token + "-" + currentTimeMillis).getBytes();
        String sign = EncryptionByMD5.getMD5(bytes);

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("loginId", userId);//动态获取，需要写缓存
        builder.add("sign", sign);
        builder.add("tokenUserId", userId + "yh");
        builder.add("timeStamp", currentTimeMillis);
        FormBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url(ConstantValue.URL + "/common/getMyApplyList")
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
                Log.i(TAG, "onResponse: " + result);
                //判断data里面resultCode是否有500然后判断是否有数据或者
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("200")) {
                        handleJson(result);
                    } else if (resultCode.equals("400")) {//错误时
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WalletRecordActivity.this, "该账号已长时间未登录，无法加载信息，请重新登录", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent("com.qryl.qrylyh.activity.BaseActivity.MustForceOfflineReceiver");
                                sendBroadcast(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 处理获取下来的json
     *
     * @param result
     */
    private void handleJson(String result) {
        Log.i(TAG, "handleJson: 进行中" + result);
        Gson gson = new Gson();
        WalletRecord walletRecord = gson.fromJson(result, WalletRecord.class);
        List<Data> data = walletRecord.getData();
        for (int i = 0; i < data.size(); i++) {
            datas.add(data.get(i));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                adapter.notifyItemRemoved(adapter.getItemCount());
                swipeRefresh.setRefreshing(false);
            }
        });

    }


    private void initView() {
        hiddenSomeView();
        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        token = prefs.getString("token", "");
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                postData();
            }
        });
    }

    private void hiddenSomeView() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        TextView tvHelp = (TextView) findViewById(R.id.help_name);
        tvTitle.setText("我的消费记录");
        tvReturn.setOnClickListener(this);
        tvHelp.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_text:
                //点击返回结束当前页面
                finish();
                break;

        }
    }
}
