package com.example.qanbuy20.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.qanbuy20.Home.Adapter.SelectorGoodBaseAdapter;
import com.example.qanbuy20.Home.bean.SelectorGood;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.GsonRequest;
import com.example.qanbuy20.GoodUtil.Click.ItemsClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SelectorFragment extends Fragment {
    private final String local = "http://139.224.221.148:301/api/User/";
    private int id;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public SelectorFragment(int id) {
        this.id = id;
    }

    public SelectorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_selector_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.selectorRecyclerView);
        progressBar = view.findViewById(R.id.selectorProgressBar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);

        requestData(id);
    }

    //商品不同类型 商品列表
    private void requestData( int id) {
        //get
        GsonRequest.getOkHttpRequest(local + "Good/getGoodsByType?page=1&size=9&type=" + id, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                List<SelectorGood> list = new Gson().fromJson(responseText,new TypeToken<List<SelectorGood>>(){}.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //进到BaseQuickAdapter源码寻找 kotlin代码
                        SelectorGoodBaseAdapter selectorGoodBaseAdapter = new SelectorGoodBaseAdapter(R.layout.activity_selector_item_introduce,list);
                        //开启动画
                        selectorGoodBaseAdapter.setAnimationEnable(true);
                        //设置动画类型
                        selectorGoodBaseAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);
                        //动画可以重复执行
                        selectorGoodBaseAdapter.setAnimationFirstOnly(false);

                        //上拉加载未实现
                        //selectorGoodBaseAdapter.getLoadMoreModule();
                        //selectorGoodBaseAdapter.getUpFetchModule();
                        recyclerView.setAdapter(selectorGoodBaseAdapter);
                        progressBar.setVisibility(View.GONE);

                        selectorGoodBaseAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                SelectorGood selectorGood = (SelectorGood) adapter.getItem(position);
                                switch (view.getId()){
                                    case R.id.SelectorItemCard:
                                        Intent intent = new Intent(getActivity(), ItemsClick.class);
                                        intent.putExtra("goodID",selectorGood.getId());
                                        startActivity(intent);
                                        break;
                                }
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
                        Toast.makeText(getActivity(), "商品信息获取失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
}
