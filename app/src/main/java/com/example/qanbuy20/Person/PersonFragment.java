package com.example.qanbuy20.Person;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qanbuy20.Home.Adapter.FragmentAdapter;
import com.example.qanbuy20.Person.bean.User;
import com.example.qanbuy20.Person.bean.UserGoodState;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonFragment extends Fragment implements View.OnClickListener {

    private final String local = "http://139.224.221.148:301/api/User/";

    private ViewPager2 mainActivityViewPager2;
    //应用栏布局
    private AppBarLayout appBarLayout;
    //折叠工具栏布局
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private BottomSheetBehavior bottomSheetBehavior;
    private TextView Person_Name, Person_Email, Person_Address;   //用户的名字邮箱和地址
    //圆形头像
    private CircleImageView Person_User;
    private TextView tag;
    //背景图
    private KenBurnsView Person_Background;

    //商品状态
    final ArrayList<String> tabName = new ArrayList<>();
    final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private String token = "";
    private String tokenNull = "";
    private FileHelper fileHelper;

    public PersonFragment() {
    }

    public PersonFragment(ViewPager2 mainActivityViewPager2) {
        this.mainActivityViewPager2 = mainActivityViewPager2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_person_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //组件实例化
        appBarLayout = (AppBarLayout) view.findViewById(R.id.Person_AppbarLayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.Person_ToolbarLayout);
        Person_User = (CircleImageView) view.findViewById(R.id.Person_User);
        Person_User.setOnClickListener(this);
        tag = (TextView) view.findViewById(R.id.Person_Tag);
        tag.setOnClickListener(this);
        Person_Name = (TextView) view.findViewById(R.id.Person_Name);
        Person_Email = (TextView) view.findViewById(R.id.Person_Email);
        Person_Address = (TextView) view.findViewById(R.id.Person_Address);
        Person_Background = (KenBurnsView) view.findViewById(R.id.Person_Background);
        tabLayout = (TabLayout) view.findViewById(R.id.Person_Layout);
        viewPager2 = (ViewPager2) view.findViewById(R.id.Person_ViewPager2);

        //检查token 本地有则直接登陆
        fileHelper = new FileHelper(getContext().getApplicationContext());
        try {
            token = fileHelper.read("token.txt");
            //直接登陆
            requestData(fileHelper);
        } catch (IOException e) {
            e.printStackTrace();

        }

        //收缩
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("个人中心");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    //判断token 与token.txt中内容是否相等
    //改进 可以改成判断token.txt是否存在
    //已经改进，使用context方式存储 已经可以判断文件存在和删除文件
    //下一改进： 改成双向绑定 一旦token.txt文件不存在则列表东西都不显示
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Person_User:
            case R.id.Person_Tag:
                //判断文件是否存在
                //不存在则进行登陆
                if (!fileHelper.fileIsExist("token.txt")) {
                    Intent intent = new Intent(v.getContext(), Sign_in.class);
                    //这里谷歌已经废弃这样的传递数据和返回方法
                    //可以学习新的
                    startActivityForResult(intent, 1);
                } else {
                    //存在了再点击则提示是否退出
                    //缺少提示 不能知道退出登陆在哪
                    //缺少退出后刷新界面 这里让他直接退出app
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(" 提示 ");
                    dialog.setMessage(" 是否‘注销’并退出QANbuy ");
                    dialog.setCancelable(false);
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                fileHelper.fileDelete("token.txt");
                                //退出app
                                getActivity().finish();
                                //改进，删除token文件后刷新界面，即只达到退出登陆状态 而不是退出app
                                //改进 双向绑定 token文件存在与否使得界面发生改变
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.show();
                    break;
                }
        }
    }

    //登陆返回
    //谷歌已经废弃 可以学习新的
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    token = data.getStringExtra("token");
                    FileHelper fileHelper = new FileHelper(getContext());
                    try {
                        //保存
                        fileHelper.save("token.txt", token);
                        requestData(fileHelper);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }


    private void requestData(FileHelper fileHelper) throws IOException {

        //用户个人信息
        //token
        GsonRequest.tokenOkHttpRequest(local + "User/showUserInfo", fileHelper, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //List<User>list = new Gson().fromJson(responseText,new TypeToken<List<User>>(){}.getType());
                User user = new Gson().fromJson(responseText, User.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext()).load(user.headPic).into(Person_User);
                        tag.setVisibility(View.GONE);
                        Glide.with(getContext()).load(user.headPic).into(Person_Background);
                        Person_Name.setText(user.userName);
                        Person_Email.setText(user.email);
                        Person_Address.setText(user.address);
                        Person_Name.setVisibility(View.VISIBLE);
                        Person_Email.setVisibility(View.VISIBLE);
                        Person_Address.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //商品三种状态 1未完成 2运输中 3已完成
        GsonRequest.tokenOkHttpRequest(local + "Order/getOrderState", fileHelper, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                List<UserGoodState> list = new Gson().fromJson(responseText, new TypeToken<List<UserGoodState>>() {
                }.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //有三种状态
                        for (UserGoodState userGoodState : list) {
                            tabName.add(userGoodState.getState());
                            //将三种状态 不同编号传入碎片 在里面进行不同状态的商品信息解析
                            GoodFragment goodFragment = new GoodFragment(fileHelper, userGoodState.getId());
                            fragmentList.add(goodFragment);
                        }
                        viewPager2.setAdapter(new FragmentAdapter(getChildFragmentManager(), getLifecycle(), fragmentList));

                        //滑动冲突问题没有解决
                        //这里的viewpager2和折叠式布局和最外层的viewpager2滑动冲突 有明显卡顿现象
                        //子ViewPager2滚动
                        //已经解决 在ViewPager2外使用监听 监听是子ViewPager2还是父布局的
                        // 工具类中NestedScrollableHost
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "商品状态错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}