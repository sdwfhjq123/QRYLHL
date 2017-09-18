package com.qryl.qrylyh.activity.login.complete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.BeGoodAtWorkVO.Data;
import com.qryl.qrylyh.adapter.WorkAdapter;
import com.qryl.qrylyh.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * http://192.168.2.134:8080/qryl/manager/getDepartments
 */
public class BeGoodAtWorkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Data> datas = new ArrayList<>();
    private WorkAdapter adapter = new WorkAdapter(datas);
    private int serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_good_at_work);
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
        HttpUtil.sendOkHttpRequest("http://192.168.2.134:8080/qryl/common/getProfessionList", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        }, "serviceId", serviceId);
    }

    private void initView() {
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("选择擅长的工作");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
