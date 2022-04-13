package com.example.qanbuy20.Home.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qanbuy20.Home.bean.HomeBanner;
import com.youth.banner.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeBannerAdapter extends BannerAdapter<HomeBanner,HomeBannerAdapter.BannerViewHolder> {
    private List<HomeBanner> list = new ArrayList<>();

    public HomeBannerAdapter(List<HomeBanner> list) {
        super(list);
    }

    //banner适配器
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, HomeBanner data, int position, int size) {
        //图片加载 holder.itemView 上下文
        Glide.with(holder.itemView).load(data.getGoodPic()).into(holder.imageView);
    }

    class BannerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public BannerViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
