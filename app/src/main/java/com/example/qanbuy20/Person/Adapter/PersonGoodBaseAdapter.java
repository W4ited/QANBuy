package com.example.qanbuy20.Person.Adapter;

import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.qanbuy20.Person.bean.UserGood;
import com.example.qanbuy20.R;

import java.util.List;

public class PersonGoodBaseAdapter extends BaseQuickAdapter<UserGood, BaseViewHolder> {
    private int id;

    public PersonGoodBaseAdapter(int layoutResId, List<UserGood> data, int id) {
        super(layoutResId, data);
        addChildClickViewIds(R.id.GoodCard);
        addChildClickViewIds(R.id.check);
        addChildClickViewIds(R.id.GoodSure);

        this.id = id;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, UserGood userGood) {
        baseViewHolder.setText(R.id.GoodTime, "购买时间：" + userGood.getPayDate().split("T")[0])
                .setText(R.id.GoodOrder, "订单号：" + userGood.getId())
                .setText(R.id.GoodName, userGood.getGoodName())
                .setText(R.id.GoodNum, "购买数量：" + userGood.getNum())
                .setText(R.id.GoodMoney, "实付金额：￥" + userGood.getPayment());

        ImageView goodPic = baseViewHolder.getView(R.id.GoodPic);
        Glide.with(getContext()).load(userGood.getGoodPic()).into(goodPic);

        //更改组件的显示状态 可以查看他的源码阅读
        //当状态为运输中时 确认收货按钮才显示
        if (id != 2){
            baseViewHolder.setVisible(R.id.GoodSure,false);
        }
    }
}
