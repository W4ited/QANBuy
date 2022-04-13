package com.example.qanbuy20.Home.Adapter;

import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.qanbuy20.Home.bean.HomeGood;
import com.example.qanbuy20.R;

import java.util.List;

public class HomeBaseQuickAdapter extends BaseQuickAdapter<HomeGood, BaseViewHolder> {

    public HomeBaseQuickAdapter(int layoutResId) {
        super(layoutResId);
    }

    public HomeBaseQuickAdapter(int layoutResId, List<HomeGood> data) {
        super(layoutResId, data);

        addChildClickViewIds(R.id.home_card);
    }

    //商品列表
    @Override
    protected void convert(BaseViewHolder baseViewHolder, HomeGood homeGood) {

        baseViewHolder.setText(R.id.home_name, homeGood.getName())
                .setText(R.id.home_price, String.valueOf(homeGood.getPrice()))
                .setText(R.id.home_saleNum, String.valueOf(homeGood.getSaleNum()));

        ImageView goodPic = baseViewHolder.getView(R.id.home_goodPic);
        Glide.with(getContext()).load(homeGood.getGoodPic()).into(goodPic);


    }
}
