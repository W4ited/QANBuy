package com.example.qanbuy20.Util;

import com.example.qanbuy20.GoodUtil.Search.bean.SearchBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {

    @GET("Good/searchGoods?page=1&size=9&searchStr=")
    Call<List<SearchBean>> getSearchGood(@Query("searchStr") String searchStr);
}
