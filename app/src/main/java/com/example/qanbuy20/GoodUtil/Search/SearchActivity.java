package com.example.qanbuy20.GoodUtil.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.qanbuy20.GoodUtil.Click.ItemsClick;
import com.example.qanbuy20.GoodUtil.Search.Adapter.SearchBaseQuickAdapter;
import com.example.qanbuy20.GoodUtil.Search.bean.SearchBean;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.GsonRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private final String local = "http://139.224.221.148:301/api/User/";

    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SearchBaseQuickAdapter searchBaseQuickAdapter;
    private TextInputEditText searchName;
    private List<SearchBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.SearchToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        searchName = findViewById(R.id.SearchEditText);
        progressBar = findViewById(R.id.SearchProgress);
        progressBar.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.SearchRecyclerView);

        //出现问题：recyclerview布局写在这 会导致再搜索商品时候 从网格布局变成线性
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this,2);
        //recyclerView.setLayoutManager(gridLayoutManager);
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
                progressBar.setVisibility(View.VISIBLE);
                requestDate(searchName.getText().toString());
                break;
        }
        return true;
    }

    //搜索请求
    private void requestDate(String searchStr){
        //get
        GsonRequest.getOkHttpRequest(local + "Good/searchGoods?page=1&size=9&searchStr=" + searchStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchActivity.this, "查询商品失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                //线性布局会改变
                if (list != null){
                    list.clear();
                }
                 list = new Gson().fromJson(responseText,new TypeToken<List<SearchBean>>(){}.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //改为在更新主线程页面的时候 每次设定布局
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this,2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        searchBaseQuickAdapter = new SearchBaseQuickAdapter(R.layout.activity_search_item,list);
                        recyclerView.setAdapter(searchBaseQuickAdapter);
                        progressBar.setVisibility(View.GONE);

                        //点击事件 点击商品后将ID传入进到商品详情页面
                        searchBaseQuickAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                SearchBean searchBean = list.get(position);
                                switch (view.getId()){
                                    case R.id.search_card:
                                        Intent intent = new Intent(SearchActivity.this, ItemsClick.class);
                                        intent.putExtra("goodID",searchBean.getId());
                                        startActivity(intent);
                                        break;
                                }
                            }
                        });
                    }
                });

            }
        });
    }
}