package com.bysyk.tumblrvideogot.util;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtil {
    private static OkHttpClient client = new OkHttpClient();

    @Nullable
    public static String getPage(@Nullable String url) throws IOException {
        if (url == null) {
            return null;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        if (body != null) {
            return body.string();
        }
        return null;
    }

    @Nullable
    public static String postForm(@Nullable String url, @Nullable Map<String, String> data) throws IOException {
        if (url == null || data == null) {
            return null;
        }
        MultipartBody.Builder reqBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            reqBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(reqBodyBuilder.build())
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        if (body != null) {
            return body.string();
        }
        return null;
    }
}
