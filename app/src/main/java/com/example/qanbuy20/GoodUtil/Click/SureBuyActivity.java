package com.example.qanbuy20.GoodUtil.Click;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.qanbuy20.GoodUtil.Click.bean.ClickIntroduce;
import com.example.qanbuy20.GoodUtil.Click.bean.SureBuyBean;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SureBuyActivity extends AppCompatActivity implements View.OnClickListener {

    private final String local = "http://139.224.221.148:301/api/User/";
    private FileHelper fileHelper;
    private ImageView sureBuyGoodPic;
    private TextView sureBuyGoodName,sureBuyGoodPrice,sureBuyGoodNum
            ,sureBuyGoodSendAddress,sureBuyGoodAcceptAddress
            ,sureBuyGoodPostage,sureBuyGoodPayment;
    private Button button;

    private ClickIntroduce clickIntroduce;
    private int ID,num;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sure_buy);

        //接受传入的数据
        Intent intent = getIntent();
        clickIntroduce = (ClickIntroduce)intent.getSerializableExtra("clickIntroduce");
        num = intent.getIntExtra("sureBuyNum",0);
        ID = intent.getIntExtra("ID",-1);
        String harvestAddress = intent.getStringExtra("sureBuyHarvestAddress");

        fileHelper = new FileHelper(getApplicationContext());

        sureBuyGoodPic = findViewById(R.id.sureBuyGoodPic);
        sureBuyGoodName = findViewById(R.id.sureBuyGoodName);
        sureBuyGoodPrice = findViewById(R.id.sureBuyGoodPrice);
        sureBuyGoodNum = findViewById(R.id.sureBuyGoodNum);
        //发货
        sureBuyGoodSendAddress = findViewById(R.id.sureBuyGoodSendAddress);
        //收货
        sureBuyGoodAcceptAddress = findViewById(R.id.sureBuyGoodAcceptAddress);
        sureBuyGoodPostage = findViewById(R.id.sureBuyGoodPostage);
        sureBuyGoodPayment = findViewById(R.id.sureBuyGoodPayment);
        button = findViewById(R.id.sureBuy);
        button.setOnClickListener(this);

        Glide.with(SureBuyActivity.this).load(clickIntroduce.getGoodPic()).into(sureBuyGoodPic);
        sureBuyGoodName.setText("商品名称：" + clickIntroduce.getName());
        sureBuyGoodPrice.setText("商品单价：" + String.valueOf(clickIntroduce.getPrice()));
        sureBuyGoodNum.setText("购买数量：" + String.valueOf(num));
        sureBuyGoodAcceptAddress.setText("收货地址：" + harvestAddress);
        sureBuyGoodSendAddress.setText("发货地址：" + clickIntroduce.getShipAddress());
        sureBuyGoodPostage.setText("运费：" + String.valueOf(clickIntroduce.getPostage()));
        sureBuyGoodPayment.setText("应付：￥" + String.valueOf(clickIntroduce.getPrice() * num + clickIntroduce.getPostage()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sureBuy:
                try {
                    requestData(fileHelper);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SureBuyActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    //购买请求
    private void requestData(FileHelper fileHelper) throws IOException {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        SureBuyBean sureBuyBean = new SureBuyBean();
        sureBuyBean.setGoodID(ID);
        sureBuyBean.setNum(num);
        String value = JSON.toJSONString(sureBuyBean);
        RequestBody requestBody = RequestBody.create(mediaType,value);

        //post token
        GsonRequest.postTokenOkHttpRequest(local + "Order/createOrder", requestBody, fileHelper, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SureBuyActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SureBuyActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
                        //确认购买后 返回商品详情页面
                        finish();
                    }
                });
            }
        });
    }
}