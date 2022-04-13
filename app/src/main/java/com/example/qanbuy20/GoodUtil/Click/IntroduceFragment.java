package com.example.qanbuy20.GoodUtil.Click;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qanbuy20.R;

public class IntroduceFragment extends Fragment {

    private String description;
    private TextView textView;

    public IntroduceFragment(String description) {
        this.description = description;
    }

    public IntroduceFragment() {
    }

    //商品介绍
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_click_introduce,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.introduce);
        //用html写的 识别html
        textView.setText(Html.fromHtml(description));
    }
}
