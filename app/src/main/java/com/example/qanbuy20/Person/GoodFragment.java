package com.example.qanbuy20.Person;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.qanbuy20.Person.Adapter.PersonGoodBaseAdapter;
import com.example.qanbuy20.Person.bean.UserGood;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;
import com.example.qanbuy20.GoodUtil.Click.ItemsClick;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoodFragment extends Fragment {
    private final String local = "http://139.224.221.148:301/api/User/";

    private View view;
    private int id;
    private FileHelper fileHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PersonGoodBaseAdapter personGoodBaseAdapter;
    private ProgressBar progressBar;
    private MaterialButton check;
    private List<UserGood> list;

    public GoodFragment(FileHelper fileHelper, int id) {
        this.fileHelper = fileHelper;
        this.id = id;
    }

    public GoodFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_person_good, container, false);
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        check = view.findViewById(R.id.check);
        recyclerView = (RecyclerView) view.findViewById(R.id.Person_Good_Recyclerview);
        progressBar = view.findViewById(R.id.Person_ProgressBar);
        swipeRefreshLayout = view.findViewById(R.id.PersonSwipeRefreshLayout);
        //这里如果用getContext会导致碎片重叠问题
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        //下拉刷新
        //谷歌升级到AndroidX 取消了support库 第一行代码上的可能会用不了
        //需要使用新的下拉刷新依赖 用法还是和书上一样
        //查看源码setColorSchemeResources
        swipeRefreshLayout.setColorSchemeResources(R.color.red);
        //监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    //重新请求
                    requestData(fileHelper, id);
                    //通知适配器数据刷新
                    personGoodBaseAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            //进来碎片第一次请求
            requestData(fileHelper, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //三种状态的各个商品信息
    private void requestData(FileHelper fileHelper, int id) throws IOException {
        //token
        GsonRequest.tokenOkHttpRequest(local + "Order/getOrderList?page=1&size=8&state=" + id, fileHelper, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                list = new Gson().fromJson(responseText, new TypeToken<List<UserGood>>() {
                }.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        personGoodBaseAdapter = new PersonGoodBaseAdapter(R.layout.activity_person_gooditem, list,id);
                        recyclerView.setAdapter(personGoodBaseAdapter);
                        progressBar.setVisibility(View.GONE);
                        //
                        swipeRefreshLayout.setRefreshing(false);

                        //点击事件 但是如果里面还有button 则使用下面的方式
//                        personGoodBaseAdapter.setOnItemClickListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                                UserGood userGood = (UserGood) adapter.getItem(position);
//                                switch (view.getId()){
//                                    case R.id.GoodCard:
//                                        Intent intent = new Intent(getActivity(), ItemsClick.class);
//                                        intent.putExtra("goodID",userGood.getGoodID());
//                                        startActivity(intent);
//                                        break;
//                                }
//                            }
//                        });

                        //card里面还有button
                        personGoodBaseAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                            @Override
                            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                                UserGood userGood = list.get(position);
                                //UserGood userGood = (UserGood) adapter.getItem(position);
                                switch (view.getId()) {
                                    case R.id.GoodCard:
                                        //查看商品的信息
                                        Intent intent = new Intent(getActivity(), ItemsClick.class);
                                        intent.putExtra("goodID", userGood.getGoodID());
                                        startActivity(intent);
                                        break;
                                    case R.id.check:
                                        //查看订单
                                        Intent checkIntent = new Intent(getActivity(),CheckGood.class);
                                        //传入订单编号
                                        checkIntent.putExtra("orderID",userGood.getId());
                                        startActivity(checkIntent);
                                        break;
                                    case R.id.GoodSure:
                                        //确认收货
                                        try {
                                            requestPut(fileHelper,userGood);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getActivity(), "确认收货", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                            //问题：OkHttp请求put需要传东西 但是这里是将商品编号 写在url才能生效
                            //确认收货 更改商品状态 运输中到已完成 后端更改
                            private void requestPut(FileHelper fileHelper, UserGood userGood) throws IOException {
                                MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                                //UserGood sureGood = new UserGood();
                                //sureGood.setId(userGood.getId());
                                Log.d("222","sureID:" + userGood.getId());

                                String value = JSON.toJSONString(userGood.getId());
                                RequestBody requestBody = RequestBody.create(mediaType,value);
                                GsonRequest.putOkHttpRequest(local + "Order/sureGet?orderId=" + userGood.getId(), requestBody, fileHelper, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "确认订单失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //打印response 和 code查看错误
                                                    //Log.d("222","sureCode:" + response);
                                                    //Log.d("222","sureCode:" + response.code());
                                                    //刷新
                                                    swipeRefreshLayout.setRefreshing(true);
                                                    try {
                                                        requestData(fileHelper, id);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "商品信息错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
