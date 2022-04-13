package com.example.qanbuy20.Home.Adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreStatus;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.module.UpFetchModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.qanbuy20.Home.bean.SelectorGood;
import com.example.qanbuy20.R;

import java.util.List;

public class SelectorGoodBaseAdapter extends BaseQuickAdapter<SelectorGood, BaseViewHolder> implements UpFetchModule,LoadMoreModule {

    public SelectorGoodBaseAdapter(int layoutResId, List<SelectorGood> data) {
        super(layoutResId, data);
        addChildClickViewIds(R.id.SelectorItemCard);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, SelectorGood selectorGood) {
        baseViewHolder
                .setText(R.id.SelectorItemName,selectorGood.getName())
                .setText(R.id.SelectorItemSaleNum,"销量：" + String.valueOf(selectorGood.getSaleNum()))
                .setText(R.id.SelectorItemPrice,"￥ " + String.valueOf(selectorGood.getPrice()));

        ImageView imageView = baseViewHolder.getView(R.id.SelectorItemGoodPic);
        Glide.with(getContext()).load(selectorGood.getGoodPic()).into(imageView);
    }
}
