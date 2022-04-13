package com.example.qanbuy20.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.qanbuy20.GoodUtil.Search.SearchActivity;
import com.example.qanbuy20.Home.Adapter.HomeBannerAdapter;
import com.example.qanbuy20.Home.Adapter.HomeBaseQuickAdapter;

import com.example.qanbuy20.Home.bean.HomeBanner;
import com.example.qanbuy20.Home.bean.HomeGood;
import com.example.qanbuy20.MainActivity;
import com.example.qanbuy20.Util.*;

import com.example.qanbuy20.R;
import com.example.qanbuy20.GoodUtil.Click.ItemsClick;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private final String local = "http://139.224.221.148:301/api/User/";

    //Toolbar
    private MaterialToolbar toolbar;
    private ViewPager2 viewPager2;
    //banner
    private ProgressBar BannerProgressBar;
    private Banner banner;
    private ArrayList<Bitmap> img = new ArrayList<>();      //banner img
    private ArrayList<String> title = new ArrayList<>();    //banner title

    //recyclerview
    private ProgressBar recyclerViewProgressBar;
    private RecyclerView recyclerView;
    private HomeBaseQuickAdapter homeBaseQuickAdapter;

    public HomeFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //调用 setHasOptionsMenu() 来指出fragment愿意添加item到选项菜单
        // (否则, fragment将接收不到对 onCreateOptionsMenu()的调用).
        setHasOptionsMenu(true);

        banner = (Banner) view.findViewById(R.id.Home_Banner);
        BannerProgressBar = view.findViewById(R.id.Home_BannerProgressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.Home_Goods);
        recyclerViewProgressBar = view.findViewById(R.id.Home_GoodsProgressBar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        toolbar = view.findViewById(R.id.HomeToolbar);
        toolbar.setTitle("");
        //将得到的activity强转
        //设置左上角的默认图标
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //导航按钮显示
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置导航图标
            //Toolbar 最左侧的这个按钮就叫作 HomeAsUp 按钮，它默认的图标是一个返回的箭头，含义是返
            //回上一个活动
            actionBar.setHomeAsUpIndicator(R.drawable.selector);
        }

        requestData();
    }

    //加载菜单
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    //菜单点击
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.person:
                viewPager2.setCurrentItem(3);
                break;
            case R.id.search:
                Intent search = new Intent(getActivity(), SearchActivity.class);
                startActivity(search);
                break;
            case android.R.id.home:
                Intent intent = new Intent(getActivity(), Selector.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void requestData() {

        //首页商品列表
        GsonRequest.getOkHttpRequest(local + "Good/getGoodsByType?page=1&size=6&type=1", new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                List<HomeGood> list = new Gson().fromJson(responseText, new TypeToken<List<HomeGood>>() {
                }.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //传统recyclerView适配器
                        //HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter(list, getContext());
                        //recyclerView.setAdapter(homeRecyclerAdapter);

                        //BaseQuickAdapter
                        homeBaseQuickAdapter = new HomeBaseQuickAdapter(R.layout.activity_home_gooditem, list);
                        recyclerView.setAdapter(homeBaseQuickAdapter);
                        recyclerViewProgressBar.setVisibility(View.GONE);

                        //点击事件
                        //homeBaseQuickAdapter.setOnItemChildClickListener();
                        homeBaseQuickAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                HomeGood homeGood = (HomeGood) adapter.getItem(position);
                                switch (view.getId()) {
                                    //商品详情界面
                                    case R.id.home_card:
                                        Intent intent = new Intent(getActivity(), ItemsClick.class);
                                        intent.putExtra("goodID", homeGood.id);
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
                        Toast.makeText(getActivity(), "错误Gson", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //首页banner 轮播图
        GsonRequest.getOkHttpRequest(local + "Good/getGoodsTop?topNum=4", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "banner错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                List<HomeBanner> list = new Gson().fromJson(responseText, new TypeToken<List<HomeBanner>>() {
                }.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //这里的适配器只写了图片 没有写标题
                        banner.setAdapter(new HomeBannerAdapter(list))
                                .addBannerLifecycleObserver(getActivity())
                                .setIndicator(new CircleIndicator(getActivity()))
                                .setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    //banner点击
                                    public void OnBannerClick(Object data, int position) {
                                        Intent intent = new Intent(getActivity(), ItemsClick.class);
                                        intent.putExtra("goodID", list.get(position).getId());
                                        startActivity(intent);
                                    }
                                });
                        BannerProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}