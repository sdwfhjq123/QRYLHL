package com.qryl.qrylyh.fragment.two;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.OrderVO.Order;
import com.qryl.qrylyh.VO.OrderVO.OrderInfoArea;
import com.qryl.qrylyh.activity.MainActivity;
import com.qryl.qrylyh.adapter.OrderFinishedAdapter;
import com.qryl.qrylyh.fragment.BaseFragment;
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

/**
 * Created by hp on 2017/9/12.
 */

public class OrderUnderwayFragment extends BaseFragment {

    private static final String TAG = "OrderUnderwayFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private List<OrderInfoArea> datas = new ArrayList<>();
    private OrderFinishedAdapter adapter = new OrderFinishedAdapter(datas);
    private int page = 1;
    private int lastVisibleItemPosition;
    private boolean isLoading;
    private String userId;
    private int roleType;
    private String token;
    private SharedPreferences prefs;

    @Override
    public void loadData() {
        //请求网络数据
        postData(String.valueOf(page));
    }

    /**
     * 请求网络数据
     */
    private void postData(final String page) {
        int orderType = 4;
        if (roleType == 0) {//护工登录的订单
            orderType = 0;
        } else if (roleType == 1 || roleType == 2) {
            orderType = 1;
        } else if (roleType == 3) {
            orderType = 2;
        }
        Log.i(TAG, "postData: userId" + userId);
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        byte[] bytes = ("/order/getOrderListByStatus-" + token + "-" + currentTimeMillis).getBytes();
        String sign = EncryptionByMD5.getMD5(bytes);

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("status", "2");
        builder.add("orderType", String.valueOf(orderType));
        builder.add("userId", userId);//动态获取，需要写缓存
        builder.add("page", page);
        builder.add("limit", "2");
        builder.add("sign", sign);
        builder.add("tokenUserId", userId + "yh");
        builder.add("timeStamp", currentTimeMillis);
        FormBody formBody = builder.build();
        final Request request = new Request.Builder()
                .url(ConstantValue.URL + "/order/getOrderListByStatus")
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
                Log.i(TAG, "onResponse: 页数" + page);
                //判断data里面resultCode是否有500然后判断是否有数据或者
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("500")) {
                        return;
                    } else if (resultCode.equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data != null) {
                            handleJson(result);
                            Log.i(TAG, "onResponse: " + result);
                        }
                    }else if (resultCode.equals("400")) {//错误时
                        prefs.edit().putBoolean("is_force_offline",true).apply();
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
        Gson gson = new Gson();
        Order order = gson.fromJson(result, Order.class);
        List<OrderInfoArea> data = order.getData().getData();
        for (int i = 0; i < data.size(); i++) {
            datas.add(data.get(i));
        }
        if (getActivity() instanceof MainActivity) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(datas);
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRemoved(adapter.getItemCount());
                    swipeRefresh.setRefreshing(false);
                }
            });
        }

    }


    @Override
    public View initView() {
        prefs = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roleType = prefs.getInt("role_type", 4);
        token = prefs.getString("token", "");
        View view = View.inflate(getActivity(), R.layout.fragment_order_container, null);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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

                    boolean isRefreshing = swipeRefresh.isRefreshing();
                    if (isRefreshing) {
                        page = 1;
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        swipeRefresh.setRefreshing(false);
                    }
                    if (!isLoading) {
                        isLoading = true;
                        page += 1;
                        Log.i(TAG, "onScrolled: page=" + page);
                        postData(String.valueOf(page));
                        isLoading = false;
                    }
                }
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                datas.clear();
                postData(String.valueOf(1));
            }
        });
        return view;
    }

}
