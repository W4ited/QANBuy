package com.example.qanbuy20.Person;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qanbuy20.GoodUtil.Click.ItemsClick;
import com.example.qanbuy20.Person.bean.CheckBean;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CheckGood extends AppCompatActivity {
    private final String local = "http://139.224.221.148:301/api/User/";
    private FileHelper fileHelper;
    private ProgressBar progressBar;

    private MaterialTextView goodState; //状态
    private ImageView goodPic;  //商品图片
    private MaterialTextView goodName,goodPrice,goodNum;    //商品名字 单价 数量
    private MaterialTextView goodOrderID,goodAcceptAddress,goodSendAddress,goodPostage;  //订单编号 收货地址 发货地址 运费
    private MaterialTextView goodBuyTime,goodSendTime,goodFinishTime;   //购买日期 发货日期 完成日期
    private MaterialTextView goodPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_good);
        fileHelper = new FileHelper(getApplicationContext());

        //订单编号
        Intent intent = getIntent();
        int id = intent.getIntExtra("orderID", -1);

        initComponent();

        try {
            requestDate(id,fileHelper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponent() {
        progressBar = findViewById(R.id.goodInformation2ProgressBar);
        goodState = findViewById(R.id.goodState);
        goodPic = findViewById(R.id.goodPic);
        goodName = findViewById(R.id.goodName);
        goodPrice = findViewById(R.id.goodPrice);
        goodNum = findViewById(R.id.goodNum);
        goodOrderID = findViewById(R.id.goodOrderID);
        goodAcceptAddress = findViewById(R.id.goodAcceptAddress);
        goodSendAddress = findViewById(R.id.goodSendAddress);
        goodPostage = findViewById(R.id.goodPostage);
        goodBuyTime = findViewById(R.id.goodBuyTime);
        goodSendTime = findViewById(R.id.goodSendTime);
        goodFinishTime = findViewById(R.id.goodFinishTime);
        goodPayment = findViewById(R.id.goodPayment);
    }

    private void requestDate(int ID,FileHelper fileHelper) throws IOException {
        //token
        GsonRequest.tokenOkHttpRequest(local + "Order/getOrderById?orderID=" + ID, fileHelper, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                CheckBean checkBean = new Gson().fromJson(responseText,CheckBean.class);
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        //状态
                        goodState.setText(checkBean.getState());
                        //图片
                        Glide.with(CheckGood.this).load(checkBean.getGoodPic()).into(goodPic);
                        //商品名字
                        goodName.setText("商品名称：" + checkBean.getGoodName());
                        //商品单价
                        goodPrice.setText("商品单价：" + String.valueOf(checkBean.getGoodPrice()));
                        //商品购买数量
                        goodNum.setText("购买数量：" + String.valueOf(checkBean.getNum()));
                        //订单编号
                        goodOrderID.setText("订单编号：" + String.valueOf(checkBean.getId()));
                        //收货地址
                        goodAcceptAddress.setText("收货地址：" + checkBean.getHarvestAddress());
                        //发货地址
                        goodSendAddress.setText("发货地址：" + checkBean.getShipAddress());
                        //运费
                        goodPostage.setText("运费：" + String.valueOf(checkBean.getPostage()));
                        //购买日期
                        goodBuyTime.setText("购买日期：" + checkBean.getPayDate().split("T")[0]);

                        //发货日期和完成日期
                        //发货日期和完成日期 存在空指针的情况
                        if(checkBean.getSendDate() == null){
                            goodSendTime.setText("发货日期：");
                        }else {
                            goodSendTime.setText("发货日期：" + checkBean.getSendDate().split("T")[0]);
                        }

                        if (checkBean.getHarvestDate() == null){
                            goodFinishTime.setText("完成日期：");
                        }else {
                            goodFinishTime.setText("完成日期：" + checkBean.getHarvestDate().split("T")[0]);
                        }

                        //实际付款
                        goodPayment.setText("实际付款：￥" + String.valueOf(checkBean.getPayment()));

                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CheckGood.this, "查看订单失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}