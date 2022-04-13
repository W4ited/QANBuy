package com.example.qanbuy20.Util;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GsonRequest {

    //get
    public static void getOkHttpRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //post
    public static void postOkHttpRequest(String address, RequestBody requestBody, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //token
    public static void tokenOkHttpRequest(String address, FileHelper fileHelper,Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address)
                .header("Authorization", "Bearer " + fileHelper.read("token.txt"))
                .build();
        client.newCall(request).enqueue(callback);
    }

    //post+token
    public static void postTokenOkHttpRequest(String address,RequestBody requestBody,FileHelper fileHelper ,Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).post(requestBody)
                .header("Authorization", "Bearer " + fileHelper.read("token.txt"))
                .build();
        client.newCall(request).enqueue(callback);
    }

    //delete
    public static void deleteOkHttpRequest(String address,FileHelper fileHelper,Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).delete()
                .header("Authorization", "Bearer " + fileHelper.read("token.txt"))
                .build();
        client.newCall(request).enqueue(callback);
    }

    //put
    public static void putOkHttpRequest(String address,RequestBody requestBody,FileHelper fileHelper,Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).put(requestBody)
                .header("Authorization", "Bearer " + fileHelper.read("token.txt"))
                .build();
        client.newCall(request).enqueue(callback);
    }
}
