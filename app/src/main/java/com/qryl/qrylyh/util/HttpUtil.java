package com.qryl.qrylyh.util;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hp on 2017/9/4.
 */

public class HttpUtil {
    /**
     * post
     *
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        //builder.add("xwdoor", "xwdoor");
        //builder.add("xwdoor", "xwdoor");
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
