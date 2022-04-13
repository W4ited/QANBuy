package com.example.qanbuy20.Shopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.qanbuy20.GoodUtil.Click.ItemsClick;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Shopping.Adapter.ShoppingGoodsAdapter;
import com.example.qanbuy20.Shopping.bean.ShoppingNumBean;
import com.example.qanbuy20.Shopping.bean.ShoppingBean;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShoppingFragment extends Fragment implements View.OnClickListener {

    private final String local = "http://139.224.221.148:301/api/User/";
    private ViewPager2 viewPager2;
    private FileHelper fileHelper;      //用于token

    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;  //刷新
    private LinearLayout empty; //空布局
    private RecyclerView recyclerView;
    private ShoppingGoodsAdapter shoppingGoodsAdapter;
    private List<ShoppingBean> list;
    private MaterialTextView home;

    private MaterialButton tvSettlement;    //结算
    private ImageView ivCheckedAll;//全选
    private TextView tvTotal;//合计价格
    private boolean isAllChecked = false;   //是否全选
    private boolean isHaveGoods = false;    //购物车是否有商品
    private List<Integer> goodIdList = new ArrayList<>();//商品列表
    private double totalPrice = 0.00;//商品总价
    private int totalCount = 0;//商品总数量

    public ShoppingFragment() {
    }

    public ShoppingFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shopping_fragment, container, false);
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        home = view.findViewById(R.id.shoppingToHome);
        home.setOnClickListener(this);
        swipeRefreshLayout = view.findViewById(R.id.shopping_refresh);
        empty = view.findViewById(R.id.shoppingEmpty);
        relativeLayout = view.findViewById(R.id.shoppingOpera);
        fileHelper = new FileHelper(getContext().getApplicationContext());
        progressBar = view.findViewById(R.id.shoppingProgress);
        recyclerView = view.findViewById(R.id.shopping_store);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //结算
        tvSettlement = view.findViewById(R.id.shopping_settlement);
        tvSettlement.setOnClickListener(this);
        //全选
        ivCheckedAll = view.findViewById(R.id.shopping_checked_image);
        ivCheckedAll.setOnClickListener(this);
        //合计
        tvTotal = view.findViewById(R.id.shopping_total);



        //swipeRefreshLayout刷新设置
        //进度条颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    relativeLayout.setVisibility(View.GONE);
                    requestData(fileHelper);
                    shoppingGoodsAdapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            requestData(fileHelper);
        } catch (IOException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    //购物车商品列表
    private void requestData(FileHelper fileHelper) throws IOException {
        GsonRequest.tokenOkHttpRequest(local + "ShoppingCart/getShoppingCart", fileHelper, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                list = new Gson().fromJson(responseText,
                        new TypeToken<List<ShoppingBean>>() {
                        }.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingGoodsAdapter = new ShoppingGoodsAdapter(R.layout.activity_shopping_item_good, list);
                        recyclerView.setAdapter(shoppingGoodsAdapter);
                        relativeLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        if (list.size() != 0){
                            isHaveGoods = true;
                        }else {
                            empty.setVisibility(View.VISIBLE);
                        }

                        shoppingGoodsAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                                //ShoppingBean shoppingBean = (ShoppingBean) adapter.getItem(position);
                                ShoppingBean shoppingBean = list.get(position);
                                switch (view.getId()) {
                                    case R.id.shoppingItem:
                                        //查看商品信息
                                        Intent intent = new Intent(getActivity(), ItemsClick.class);
                                        intent.putExtra("goodID", shoppingBean.getGoodID());
                                        startActivity(intent);
                                        break;
                                    case R.id.shoppingItemDelete:
                                        //删除商品
                                        //弹出一个对话框
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                        dialog.setMessage("确定要删除所选商品吗？")
                                                .setCancelable(false)
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        try {
                                                            requestDelete(fileHelper,shoppingBean);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(getActivity(), "删除商品失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        dialog.show();
                                        break;
                                    case R.id.iv_checked_goods:
                                        //选中商品
                                        shoppingBean.setChecked(!shoppingBean.isChecked());

                                        //记录商品的id 添加到一个list中
                                        if (!goodIdList.contains(shoppingBean.getGoodID()) && shoppingBean.isChecked() == true) {
                                            //如果list中没有这个商品的Id 不过商品处于选中状态 则add
                                            goodIdList.add(shoppingBean.getGoodID());
                                        }else if (goodIdList.contains(shoppingBean.getGoodID())){
                                            //在list里面但是未选中
                                            goodIdList.remove((Integer) shoppingBean.getGoodID() );
                                        }

                                        if (goodIdList.size() == list.size()){
                                            //全选
                                            ivCheckedAll.setImageDrawable(getActivity().getDrawable(R.drawable.ic_checked));
                                            isAllChecked = true;
                                        }else {
                                            //不全选
                                            ivCheckedAll.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check));
                                            isAllChecked = false;
                                        }
                                        //计算
                                        calculationPrice();
                                        //刷新适配器
                                        shoppingGoodsAdapter.notifyDataSetChanged();
                                        break;
                                    case R.id.tv_increase_goods_num:
                                        //增加商品数量
                                        try {
                                            requestIncreaseNum(fileHelper,shoppingBean);
                                            shoppingBean.setNum(shoppingBean.getNum() + 1);
                                            shoppingGoodsAdapter.notifyDataSetChanged();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case R.id.tv_reduce_goods_num:
                                        //减少商品数量
                                        try {
                                            requestDecreaseNum(fileHelper,shoppingBean);
                                            if (shoppingBean.getNum() -1 != 0){
                                                shoppingBean.setNum(shoppingBean.getNum() -1);
                                                shoppingGoodsAdapter.notifyDataSetChanged();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(getActivity(), "减少商品数量", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                            //删除购物车内商品
                            private void requestDelete(FileHelper fileHelper, ShoppingBean shoppingBean) throws IOException {
                                //delete
                                GsonRequest.deleteOkHttpRequest(local + "ShoppingCart/deleteCart?goodID=" + shoppingBean.getGoodID(), fileHelper, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "删除商品失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //删除后刷新
                                                swipeRefreshLayout.setRefreshing(true);
                                                //重新请求
                                                try {
                                                    requestData(fileHelper);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                });
                                //删除
                            }

                            //减少商品数量
                            private void requestDecreaseNum(FileHelper fileHelper, ShoppingBean shoppingBean) throws IOException {
                                MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); //设置格式
                                ShoppingNumBean shoppingNumBean = new ShoppingNumBean();
                                shoppingNumBean.setGoodID(shoppingBean.getGoodID());
                                if (shoppingBean.getNum() -1 != 0){
                                    shoppingNumBean.setNum(shoppingBean.getNum() -1 );

                                    String value = JSON.toJSONString(shoppingNumBean);
                                    RequestBody requestBody = RequestBody.create(mediaType,value);
                                    GsonRequest.postTokenOkHttpRequest(local+"ShoppingCart/editNum" ,requestBody,fileHelper, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity(),"减少数量失败",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            Log.d("222","DecreaseCode:" + response.code());
                                        }
                                    });
                                }else {
                                }
                            }

                            //增加商品数量
                            private void requestIncreaseNum(FileHelper fileHelper,ShoppingBean shoppingBean) throws IOException {
                                MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); //设置格式
                                ShoppingNumBean shoppingNumBean = new ShoppingNumBean();
                                shoppingNumBean.setGoodID(shoppingBean.getGoodID());
                                shoppingNumBean.setNum(shoppingBean.getNum()+1);

                                String value = JSON.toJSONString(shoppingNumBean);
                                RequestBody requestBody = RequestBody.create(mediaType,value);
                                GsonRequest.postTokenOkHttpRequest(local+"ShoppingCart/editNum" ,requestBody, fileHelper, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"增加数量失败",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        Log.d("222","IncreaseCode:" + response.code());
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
                        Toast.makeText(getActivity(), "获取购物车信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shoppingToHome:
                viewPager2.setCurrentItem(0);
                progressBar.setVisibility(View.VISIBLE);
                try {
                    requestData(fileHelper);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.shopping_checked_image:
                if (isAllChecked) {
                    //取消全选
                    isSelectAll(false);
                } else {
                    //全选
                    isSelectAll(true);
                }
                calculationPrice();
                break;
            case R.id.shopping_settlement:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("提示");
                dialog.setMessage("确定购买所选商品？");
                dialog.setCancelable(false);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            requestBuy(fileHelper);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "购买失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    //购物车购买
    private void requestBuy(FileHelper fileHelper) throws IOException {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        String value = JSON.toJSONString(goodIdList);
        RequestBody requestBody = RequestBody.create(mediaType,value);

        //post token
        GsonRequest.postTokenOkHttpRequest(local + "Order/createOrders" ,requestBody,fileHelper, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "购买失败，库存不足", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //购买后更新
                        swipeRefreshLayout.setRefreshing(true);
                        try {
                            requestData(fileHelper);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 是否全选
     *
     * @param state 状态
     */
    @SuppressLint("NewApi")
    private void isSelectAll(boolean state) {
        //更改是否选中图片
        ivCheckedAll.setImageDrawable(getContext().getDrawable(state ? R.drawable.ic_checked : R.drawable.ic_check));

        //没有店铺 就让所有商品都为选中
        for (ShoppingBean shoppingBean : list) {
            //商品是否选中
            shoppingBean.setChecked(state);
            shoppingGoodsAdapter.notifyDataSetChanged();
        }
        isAllChecked = state;
    }

    public void calculationPrice(){
        //每次计算之前先置零
        totalPrice = 0.00;  //商品总价
        totalCount = 0;     //商品总数量
        for (int i=0;i<list.size();i++){
            ShoppingBean shoppingBean = list.get(i);
            if (shoppingBean.isChecked() == true){
                totalCount++;
                totalPrice += shoppingBean.getPrice() * shoppingBean.getNum() + shoppingBean.getPostage();
            }
        }
        tvTotal.setText("￥" + totalPrice);
        tvSettlement.setText(totalCount == 0?"结算" : "结算（" + totalCount +")");
    }
}