package com.example.qanbuy20.Person;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.qanbuy20.Person.bean.UserSign;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Util.GsonRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sign_in extends AppCompatActivity implements View.OnClickListener {

    private final String local = "http://139.224.221.148:301/api/User/";

    private TextInputEditText email, password;   //账号密码
    private MaterialButton submit;  //确认按钮
    private String get_email, get_password;  //账号密码内容
    private String token = null;    //token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initialize();
    }

    private void initialize() {
        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);
        submit = (MaterialButton) findViewById(R.id.submit);
        submit.setOnClickListener(this);

        //保证密码初始时候是隐藏的
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (email.getText() == null || email.getText().length() == 0 || password.getText() == null || password.getText().length() == 0) {
                    Toast.makeText(Sign_in.this, "请完整输入账号密码", Toast.LENGTH_SHORT).show();
                } else {
                    get_email = email.getText().toString();
                    get_password = password.getText().toString();
                    requestData(get_email, get_password);
                }
                break;
        }
    }

    //登陆请求
    private void requestData(String get_email, String get_password) {
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); //设置格式
        UserSign userSign = new UserSign();
        userSign.setEmail(get_email);
        userSign.setPassword(get_password);

        String value = JSON.toJSONString(userSign);
        RequestBody requestBody = RequestBody.create(mediaType, value);

        //post
        GsonRequest.postOkHttpRequest(local + "User/Login", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Sign_in.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                /*
                final String responseText = response.body().string();
                token = responseText;
                */
                token = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("222","code000" + response.code());

                        if (response.code() != 200) {
                            Toast.makeText(Sign_in.this, "账号密码错误", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 200){
                            Intent intent = new Intent();
                            intent.putExtra("token", token);
                            setResult(RESULT_OK, intent);
                            Log.d("222","code111" + response.code());
                            finish();
                        }
                    }
                });

            }
        });
    }
}