package com.example.qanbuy20.GoodUtil.Click;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.qanbuy20.GoodUtil.Click.bean.IntoShoppingCarBean;
import com.example.qanbuy20.GoodUtil.Search.SearchActivity;
import com.example.qanbuy20.Home.Adapter.FragmentAdapter;
import com.example.qanbuy20.Person.bean.User;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;
import com.example.qanbuy20.GoodUtil.Click.bean.ClickIntroduce;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemsClick extends AppCompatActivity implements View.OnClickListener {

    private final String local = "http://139.224.221.148:301/api/User/";
    private int ID;
    private FileHelper fileHelper;
    private User user;
    private String harvestAddress;
    private ShapeableImageView goodPic;  //商品图片
    //商品名字,价格，销量，库存，发货地址，邮费
    private MaterialTextView goodName, goodPrice, goodPriceNum, goodNum, goodAddress, goodPostage;

    final String[] titleArray = new String[]{"详情", "评论"};
    private List<Fragment> fragmentList = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MaterialToolbar toolbar;

    private Button shoppingCar,buy; //购买和加入购物车
    private int count = 1;
    private TextView num ;  //数量
    private TextView increase,decrease; //增加和减少数量
    private ClickIntroduce clickIntroduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_click);

        //点击商品后 将商品的ID传进来
        Intent intent = getIntent();
        ID = intent.getIntExtra("goodID", -1);

        //初始化组件
        initComponent();

        try {
            requestData(ID,fileHelper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponent() {
        fileHelper = new FileHelper(getApplicationContext());
        goodPic = findViewById(R.id.Clicks_Image);
        goodName = findViewById(R.id.Clicks_Name);
        goodPrice = findViewById(R.id.Clicks_Price);
        goodPriceNum = findViewById(R.id.Clicks_PriceNum);
        goodNum = findViewById(R.id.Clicks_Num);
        goodAddress = findViewById(R.id.Clicks_Address);
        goodPostage = findViewById(R.id.Clicks_Postage);
        tabLayout = findViewById(R.id.GoodLayout);
        viewPager2 = findViewById(R.id.GoodViewPager2);
        shoppingCar = findViewById(R.id.Clicks_ShoppingCar);
        buy = findViewById(R.id.Clicks_Buy);
        shoppingCar.setOnClickListener(this);
        buy.setOnClickListener(this);
        num = findViewById(R.id.tv_goods_num);
        num.setText("1");
        increase = findViewById(R.id.tv_increase_goods_num);
        increase.setOnClickListener(this);
        decrease = findViewById(R.id.tv_reduce_goods_num);
        decrease.setOnClickListener(this);
        toolbar = findViewById(R.id.Clicks_Search);
        //标题设为空 否则使用项目名称
        toolbar.setTitle("");
        //setSupportActionBar()方法并将 Toolbar 的实例传入，这样我们就做到既使用了 Toolbar，又
        //让它的外观与功能都和 ActionBar 一致了
        setSupportActionBar(toolbar);
    }

    //加载菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        return true;
    }

    //菜单中按钮点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Intent search = new Intent(ItemsClick.this, SearchActivity.class);
                startActivity(search);
                break;
        }
        return true;
    }

    //商品详情信息 用传入的ID请求
    private void requestData(int ID,FileHelper fileHelper) throws IOException {
        //get
        GsonRequest.getOkHttpRequest(local + "Good/getGoodDetail?goodID="+ID , new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                clickIntroduce = new Gson().fromJson(responseText, ClickIntroduce.class);
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        Glide.with(ItemsClick.this).load(clickIntroduce.getGoodPic()).into(goodPic);
                        goodName.setText(clickIntroduce.getName());
                        goodPrice.setText("售价：￥" + String.valueOf(clickIntroduce.getPrice()));
                        goodPriceNum.setText("销量：" + String.valueOf(clickIntroduce.getSaleNum()));
                        goodNum.setText("库存：" + String.valueOf(clickIntroduce.getInventory()));
                        goodAddress.setText("发货地址：" + clickIntroduce.getShipAddress());
                        goodPostage.setText("邮费：" + String.valueOf(clickIntroduce.getPostage()));

                        //商品介绍和评论碎片
                        fragmentList.add(new IntroduceFragment(clickIntroduce.getDescription()));
                        fragmentList.add(new CommentFragment(ID));

                        viewPager2.setAdapter(new FragmentAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList));

                        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                tab.setText(titleArray[position]);
                            }
                        });
                        tabLayoutMediator.attach();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ItemsClick.this, "商品信息错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        //用户信息请求，获取用户设置的地址，传入购买
        GsonRequest.tokenOkHttpRequest(local + "User/showUserInfo", fileHelper, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                user = new Gson().fromJson(responseText, User.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        harvestAddress = user.getAddress();
                        //Log.d("222","user:" + user.getAddress());
                        //Log.d("222","user:" + harvestAddress);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //商品加入购物车
            case R.id.Clicks_ShoppingCar:
                try {
                    requestIntoShoppingCar(fileHelper,ID);
                    Toast.makeText(ItemsClick.this, "加入购物车成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(ItemsClick.this, "请先登陆", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.Clicks_Buy:
                //传对象
                //由于解析出的商品图片 为base64解码 大小太大 intent.putExtra不能传 会闪退
                Intent sureBuy = new Intent(ItemsClick.this,SureBuyActivity.class);
                Bundle bundle = new Bundle();
                //ClickIntroduce数据类使用接口Serializable
                bundle.putSerializable("clickIntroduce",clickIntroduce);
                sureBuy.putExtras(bundle);
                sureBuy.putExtra("sureBuyNum",count);
                sureBuy.putExtra("ID",ID);
                sureBuy.putExtra("sureBuyHarvestAddress",harvestAddress);
                startActivity(sureBuy);
                break;
            case  R.id.tv_increase_goods_num:
                //商品数量相加
                count++;
                num.setText(String.valueOf(count));
                break;
            case R.id.tv_reduce_goods_num:
                //商品数量减少
                if (count-1 != 0 ){
                    count--;
                    num.setText(String.valueOf(count));
                }else {
                }
                break;
        }
    }

    //加入购物车申请
    private void requestIntoShoppingCar(FileHelper fileHelper,int ID) throws IOException {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); //设置格式
        IntoShoppingCarBean intoShoppingCarBean = new IntoShoppingCarBean();
        //传入商品的ID和数量
        intoShoppingCarBean.setGoodID(ID);
        intoShoppingCarBean.setNum(count);
        String value = JSON.toJSONString(intoShoppingCarBean);
        RequestBody requestBody = RequestBody.create(mediaType,value);
        //postToken
        GsonRequest.postTokenOkHttpRequest(local + "ShoppingCart/addGood", requestBody, fileHelper, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ItemsClick.this, "加入购物车失败,请先登陆", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("222","ShoppingCarCode:" + response.code());
            }
        });
    }
}