package com.example.qanbuy20;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.qanbuy20.Home.Adapter.FragmentAdapter;
import com.example.qanbuy20.Home.HomeFragment;
import com.example.qanbuy20.Person.PersonFragment;
import com.example.qanbuy20.Shopping.ShoppingFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private List<Fragment> fragmentList = new ArrayList<>();
    final String[] titleArray = new String[]{"首页", "购物车", "我的"};
    final int[] titleItem = new int[]{R.drawable.fragment_home,R.drawable.fragment_shopping,R.drawable.fragment_person};

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);
        viewPager2 = (ViewPager2) findViewById(R.id.home_viewPager2);

        //传入viewPager2
        fragmentList.add(new HomeFragment(viewPager2));
        fragmentList.add(new ShoppingFragment(viewPager2));
        fragmentList.add(new PersonFragment(viewPager2));

        //viewPager2使用与viewPager使用方法不同
        //viewPager2适配器继承FragmentStateAdapter
        viewPager2.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), fragmentList));
        viewPager2.setOffscreenPageLimit(3);

        //TabLayout 和 Viewpager2 关联
        TabLayoutMediator tab = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //设置图标和名称
                tab.setText(titleArray[position]);
                tab.setIcon(titleItem[position]);

            }
        });
        //注意要加这个
        tab.attach();
    }
}