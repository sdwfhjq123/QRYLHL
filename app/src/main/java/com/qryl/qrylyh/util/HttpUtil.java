package com.qryl.qrylyh.util;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public static void sendOkHttpRequest(String address, Callback callback, String params, int cityId) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(params, String.valueOf(cityId));
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * post
     *
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequestInt(String address, Callback callback, String params, String cityId) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(params, cityId);
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void postAsyn(String address, Map<String, String> map, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entrySet : map.entrySet()) {
            builder.add(entrySet.getKey(), entrySet.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static String postSync(String address, Map<String, String> map) throws IOException {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entrySet : map.entrySet()) {
            builder.add(entrySet.getKey(), entrySet.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String result = response.body().string();
            return result;
        }
        return null;

    }

}
