package com.example.qanbuy20.Shopping.Adapter;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.qanbuy20.R;
import com.example.qanbuy20.Shopping.bean.ShoppingBean;

import java.util.List;

public class ShoppingGoodsAdapter extends BaseQuickAdapter<ShoppingBean, BaseViewHolder> {

    public ShoppingGoodsAdapter(int layoutResId, List<ShoppingBean> data) {
        super(layoutResId, data);
        addChildClickViewIds(R.id.shoppingItem);    //查看商品
        addChildClickViewIds(R.id.shoppingItemDelete);  //删除商品
        addChildClickViewIds(R.id.iv_checked_goods);      //选中商品
        addChildClickViewIds(R.id.tv_increase_goods_num);   //增加商品
        addChildClickViewIds(R.id.tv_reduce_goods_num);     //减少商品

    }

    @SuppressLint("NewApi")
    @Override
    protected void convert(BaseViewHolder baseViewHolder, ShoppingBean shoppingBean) {
        baseViewHolder.setText(R.id.tv_good_name,shoppingBean.getName())
                .setText(R.id.tv_goods_price,String.valueOf(shoppingBean.getPrice()))
                .setText(R.id.tv_goods_num,String.valueOf(shoppingBean.getNum()));

        ImageView imageView = baseViewHolder.getView(R.id.iv_goods);
        Glide.with(getContext()).load(shoppingBean.getGoodPic()).into(imageView);

        ImageView checkedGoods = baseViewHolder.getView(R.id.iv_checked_goods);
        if (shoppingBean.isChecked()){
            checkedGoods.setImageDrawable(getContext().getDrawable(R.drawable.ic_checked));
        }else {
            checkedGoods.setImageDrawable(getContext().getDrawable(R.drawable.ic_check));
        }
    }
}
