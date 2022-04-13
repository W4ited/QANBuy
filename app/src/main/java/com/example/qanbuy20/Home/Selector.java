package com.example.qanbuy20.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.qanbuy20.GoodUtil.Search.SearchActivity;
import com.example.qanbuy20.Home.Adapter.FragmentAdapter;
import com.example.qanbuy20.Home.bean.SelectorType;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.GsonRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Selector extends AppCompatActivity implements View.OnClickListener {
    private final String local = "http://139.224.221.148:301/api/User/";

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    final ArrayList<String> tabName = new ArrayList<>();
    final ArrayList<Fragment> fragmentList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private MaterialTextView home;

    //筛选
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        tabLayout = findViewById(R.id.selectorTabLayout);
        viewPager2 = findViewById(R.id.selectorViewPager2);
        home = findViewById(R.id.selectorToHome);
        home.setOnClickListener(this);
        toolbar = findViewById(R.id.selectorToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        requestData(local);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Intent search = new Intent(Selector.this, SearchActivity.class);
                startActivity(search);
                break;
        }
        return true;
    }

    private void requestData(String local) {
        //商品类型解析
        //get
        GsonRequest.getOkHttpRequest(local + "Good/getType", new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                List<SelectorType> list = new Gson().fromJson(responseText,
                        new TypeToken<List<SelectorType>>() {
                        }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //id 0 全部类型
                        tabName.add("全部类型");
                        fragmentList.add(new SelectorFragment(0));

                        //不同id 不同类型商品 传入碎片后别分解析各个类型的商品信息
                        for (SelectorType selectorType : list) {
                            tabName.add(selectorType.getType());
                            //传入商品类型编号
                            fragmentList.add(new SelectorFragment(selectorType.getId()));
                        }

                        viewPager2.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), fragmentList));
                        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                tab.setText(tabName.get(position));
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
                        Toast.makeText(Selector.this, "筛选商品错误，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //左上角 点击后回到首页
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.selectorToHome:
                finish();
                break;
        }
    }
}