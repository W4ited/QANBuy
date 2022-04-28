package com.example.qanbuy20.Util;

import com.example.qanbuy20.Home.bean.HomeBanner;
import com.example.qanbuy20.Home.bean.HomeGood;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeService {

    @GET("Good/getGoodsByType?page=1&size=6&type=1")
    Call<List<HomeGood>> getHomeList();

    @GET("Good/getGoodsTop?topNum=4")
    Call<List<HomeBanner>> getHomeBanner();
}
