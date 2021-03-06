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


    //????????????????????? ????????????
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
            //Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
            progressBar1.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    //?????????????????? ????????????????????????????????????
    private void requestData(FileHelper fileHelper) throws IOException {
        //?????????????????? get
        GsonRequest.getOkHttpRequest(local + "Good/getComments?goodID=" + ID, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                List<CommentBean> list = new Gson().fromJson(responseText, new TypeToken<List<CommentBean>>() {
                }.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //?????????????????????
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
                        Toast.makeText(getActivity(), "????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //????????????????????????????????? token
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
                        //Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //???????????? ??????
            case R.id.commentSubmit:
                try {
                    commentPostRequest(fileHelper, commentText.getText().toString(), ID);
                    //?????????????????????????????? ???????????? ?????????
                    commentText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);//???????????????????????????
                            commentText.setImeOptions(IME_ACTION_DONE);
                            return false;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //??????????????????
    private void commentPostRequest(FileHelper fileHelper, String postComment, int ID) throws IOException {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); //????????????
        //?????????????????????ID ??? ???????????????
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
                    //?????????????????????
                    //???list?????? ????????????
                    //commentBaseAdapter.notifyDataSetChanged();
                    //???????????????????????????????????? ???????????????list?????? ??????????????????????????? ????????????????????????
                    //?????????????????????????????? ?????????????????????
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            Log.d("222", "commentBaseAdapter" + response.code());
                            CommentBean commentBean = new CommentBean(user.headPic, user.userName, ID, postComment, "??????");
                            //????????????
                            commentBaseAdapter.addData(commentBean);
                            //??????????????? ????????????
                            commentBaseAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(commentBaseAdapter);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
