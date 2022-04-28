package com.example.qanbuy20.Util;

import com.example.qanbuy20.Person.bean.User;
import com.example.qanbuy20.Person.bean.UserGood;
import com.example.qanbuy20.Person.bean.UserGoodState;
import com.example.qanbuy20.Person.bean.UserSign;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface PersonService {
    @POST("User/Login")
    Call<ResponseBody> UserSign(@Body UserSign userSign);

    @GET("User/showUserInfo")
    Call<User> getUser(@Header("Authorization") String token);

    @GET("Order/getOrderState")
    Call<List<UserGoodState>> getUserGoodState(@Header("Authorization") String token);

    @GET("Order/getOrderList")
    Call<List<UserGood>> getUserGood(@Query("page") int page,
                                     @Query("size") int size,
                                     @Query("state") int id,
                                     @Header("Authorization") String token);

    @PUT("Order/sureGet?orderId=")
    Call<ResponseBody> putUserGood(@Body int goodID,
                                   @Query("orderId") int id,
                                   @Header("Authorization") String token);
}
