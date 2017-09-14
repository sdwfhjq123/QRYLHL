package com.qryl.qrylyh.activity.login.complete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.Hospital;
import com.qryl.qrylyh.adapter.ChooseHospitalAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalActivity extends AppCompatActivity {

    private static final String TAG = "HospitalActivity";

    private TextView titleName;
    private RecyclerView recyclerView;
    private ChooseHospitalAdapter adapter;
    private List<Hospital> hospitalList = new ArrayList<>();
    private int positionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        initView();
        titleName.setText("选择医院");
        initData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChooseHospitalAdapter(hospitalList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ChooseHospitalAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick:获取到的数据 " + hospitalList.get(position).getId());
                positionId = hospitalList.get(position).getId();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            hospitalList.add(new Hospital(i, "我是数据" + i));
        }
    }

    private void initView() {
        titleName = (TextView) findViewById(R.id.title_name);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
