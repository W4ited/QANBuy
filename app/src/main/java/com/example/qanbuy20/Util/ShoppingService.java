package com.example.qanbuy20.Util;

import com.example.qanbuy20.Shopping.bean.ShoppingBean;
import com.example.qanbuy20.Shopping.bean.ShoppingNumBean;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ShoppingService {
    @GET("ShoppingCart/getShoppingCart")
    Call<List<ShoppingBean>> getShoppingCarGoods(@Header("Authorization") String token);

    @DELETE("ShoppingCart/deleteCart")
    Call<ResponseBody> deleteShoppingCarGood(@Query("goodID") int goodID,
                                             @Header("Authorization") String token);

    @POST("ShoppingCart/editNum")
    Call<ResponseBody> editNum(@Body ShoppingNumBean shoppingNumBean,
                               @Header("Authorization") String token);

    @POST("Order/createOrders")
    Call<ResponseBody> buyGoods(@Body int[] goodList,
                                @Header("Authorization") String token);
}
