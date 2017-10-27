package com.qryl.qrylyh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.qryl.qrylyh.activity.H5.OrderInfoActivity;
import com.qryl.qrylyh.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by yinhao on 2017/9/8.
 */

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.i(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.i(TAG, "[MyReceiver] 接收Registration Id : " + regId);

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            Log.i(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {

            Log.i(TAG, "收到了通知");// 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.i(TAG, "用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            String result = bundle.getString(JPushInterface.EXTRA_EXTRA);
            handleJson(result, context);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 解析通知内容
     *
     * @param result
     */
    private void handleJson(String result, Context context) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int orderId = jsonObject.getInt("orderId");
            int orderType = jsonObject.getInt("orderType");
            // 在这里可以自己写代码去定义用户点击后的行为
            Intent i = new Intent(context, OrderInfoActivity.class);  //自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("orderId", orderId);
            i.putExtra("orderType", orderType);
            context.startActivity(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
