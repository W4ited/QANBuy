package com.example.qanbuy20.Util;

import com.example.qanbuy20.Home.bean.SelectorGood;
import com.example.qanbuy20.Home.bean.SelectorType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SelectorService {

    @GET("Good/getType")
    Call<List<SelectorType>> getSelectorType();

    @GET("Good/getGoodsByType?page=1&size=9")
    Call<List<SelectorGood>> getSelectorGood(@Query("type") int type);
}
