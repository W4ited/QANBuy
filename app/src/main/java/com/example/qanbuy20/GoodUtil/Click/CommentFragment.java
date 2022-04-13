package com.example.qanbuy20.GoodUtil.Click;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.qanbuy20.GoodUtil.Click.bean.CommentPost;
import com.example.qanbuy20.Person.bean.User;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.FileHelper;
import com.example.qanbuy20.Util.GsonRequest;
import com.example.qanbuy20.GoodUtil.Click.Adapter.CommentBaseAdapter;
import com.example.qanbuy20.GoodUtil.Click.bean.CommentBean;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentFragment extends Fragment implements View.OnClickListener {

    private final String local = "http://139.224.221.148:301/api/User/";
    private RecyclerView recyclerView;
    private CommentBaseAdapter commentBaseAdapter;
    private FileHelper fileHelper;

    private CircleImageView commentGoodPic;
    private ProgressBar progressBar1, progressBar2;
    private Button commentSubmit;
    private TextInputEditText commentText;

    private int ID;
    private User user;

    public CommentFragment(int ID) {
        this.ID = ID;
    }

    public CommentFragment() {
    }


    //商品详细信息中 评论模块
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_click_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.commentRecyclerView);
        commentText = view.findViewById(R.id.commentText);
        commentSubmit = view.findViewById(R.id.commentSubmit);
        commentSubmit.setOnClickListener(this);
        progressBar1 = view.findViewById(R.id.commentProgressBar1);
        progressBar2 = view.findViewById(R.id.commentProgressBar2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentGoodPic = view.findViewById(R.id.commentGoodPic);
        fileHelper = new FileHelper(getContext().getApplicationContext());

        try {
            requestData(fileHelper);
        } catch (IOException e) {
            //Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
            progressBar1.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    //商品历史评论 、用户评论的当前用户信息
    private void requestData(FileHelper fileHelper) throws IOException {
        //商品历史评论 get
        GsonRequest.getOkHttpRequest(local + "Good/getComments?goodID=" + ID, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                List<CommentBean> list = new Gson().fromJson(responseText, new TypeToken<List<CommentBean>>() {
                }.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //历史评论适配器
                        commentBaseAdapter = new CommentBaseAdapter(R.layout.activity_click_comment_item, list);
                        recyclerView.setAdapter(commentBaseAdapter);
                        progressBar2.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "查看历史评论失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //用户评论的当前用户信息 token
        GsonRequest.tokenOkHttpRequest(local + "User/showUserInfo", fileHelper, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                user = new Gson().fromJson(responseText, User.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext()).load(user.headPic).into(commentGoodPic);
                        progressBar1.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar1.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //发表评论 按钮
            case R.id.commentSubmit:
                try {
                    commentPostRequest(fileHelper, commentText.getText().toString(), ID);
                    //输入完成，输入法关闭 ！未实现 未解决
                    commentText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);//这行代码隐藏软键盘
                            commentText.setImeOptions(IME_ACTION_DONE);
                            return false;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "评论失败，请先登陆", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //发表评论请求
    private void commentPostRequest(FileHelper fileHelper, String postComment, int ID) throws IOException {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); //设置格式
        //传入当前商品的ID 和 评论的内容
        CommentPost commentPost = new CommentPost();
        commentPost.setGoodID(ID);
        commentPost.setComment(postComment);

        String value = JSON.toJSONString(commentPost);
        RequestBody requestBody = RequestBody.create(mediaType, value);

        //post token
        GsonRequest.postTokenOkHttpRequest(local + "Good/sentComment", requestBody, fileHelper, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    //刷新适配器列表
                    //新list获取 怎么获取
                    //commentBaseAdapter.notifyDataSetChanged();
                    //当前：使用静态添加的方式 直接添加到list里面 不再次请求历史评论 再次请求浪费资源
                    //下次再进到商品详细时 历史评论再请求
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            Log.d("222", "commentBaseAdapter" + response.code());
                            CommentBean commentBean = new CommentBean(user.headPic, user.userName, ID, postComment, "现在");
                            //静态添加
                            commentBaseAdapter.addData(commentBean);
                            //通知适配器 数据改变
                            commentBaseAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(commentBaseAdapter);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "评论失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
