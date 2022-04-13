package com.example.qanbuy20.Util;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    private Context context;

    //拦截器
    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        FileHelper fileHelper = new FileHelper(context);
        Request updateRequest = request.newBuilder()
                .header("Authorization", "Bearer " + fileHelper.read("token.txt"))
                .build();
        return chain.proceed(updateRequest);
    }
}
