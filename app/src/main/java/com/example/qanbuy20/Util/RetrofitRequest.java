package com.example.qanbuy20.Util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {
    private static final String BASE_URI = "http://139.224.221.148:301/api/User/";

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URI)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <T> T create(Class<T> serviceClass){
        return retrofit.create(serviceClass);
    }
}
