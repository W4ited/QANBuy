package com.example.qanbuy20.Util;

import com.example.qanbuy20.GoodUtil.Click.bean.ClickIntroduce;
import com.example.qanbuy20.GoodUtil.Click.bean.CommentBean;
import com.example.qanbuy20.GoodUtil.Click.bean.CommentPost;
import com.example.qanbuy20.GoodUtil.Click.bean.IntoShoppingCarBean;
import com.example.qanbuy20.GoodUtil.Click.bean.SureBuyBean;
import com.example.qanbuy20.Person.bean.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClickService {

    @GET("Good/getGoodDetail")
    Call<ClickIntroduce> getClickIntroduce(@Query("goodID") int goodID);

    @GET("Good/getComments")
    Call<List<CommentBean>> getComment(@Query("goodID") int goodID);

    @GET("User/showUserInfo")
    Call<User> getUser(@Header("Authorization") String token);

    @POST("Good/sentComment")
    Call<ResponseBody> postComment(@Body CommentPost commentPost,
                                   @Header("Authorization") String token);

    @POST("ShoppingCart/addGood")
    Call<ResponseBody> postIntoShoppingCar(@Body IntoShoppingCarBean intoShoppingCarBean,
                                           @Header("Authorization") String token);

    @POST("Order/createOrder")
    Call<ResponseBody> postBuyGood(@Body SureBuyBean sureBuyBean,
                                   @Header("Authorization") String token);


}
