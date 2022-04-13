package com.example.qanbuy20.GoodUtil.Search.Adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.qanbuy20.GoodUtil.Search.bean.SearchBean;
import com.example.qanbuy20.R;

import java.util.List;

public class SearchBaseQuickAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {

    public SearchBaseQuickAdapter(int layoutResId, List<SearchBean> data) {
        super(layoutResId, data);

        addChildClickViewIds(R.id.search_card);
    }

    //搜索 商品列表
    @Override
    protected void convert(BaseViewHolder baseViewHolder, SearchBean searchBean) {
            baseViewHolder.setText(R.id.search_name,searchBean.getName())
                    .setText(R.id.search_price,String.valueOf(searchBean.getPrice()))
                    .setText(R.id.search_saleNum,String.valueOf(searchBean.getSaleNum()));

        ImageView goodPic = baseViewHolder.getView(R.id.search_goodPic);
        Glide.with(getContext()).load(searchBean.getGoodPic()).into(goodPic);
    }
}
