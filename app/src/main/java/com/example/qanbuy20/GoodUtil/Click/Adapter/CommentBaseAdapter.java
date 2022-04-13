package com.example.qanbuy20.GoodUtil.Click.Adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.qanbuy20.R;
import com.example.qanbuy20.GoodUtil.Click.bean.CommentBean;

import java.util.List;

public class CommentBaseAdapter extends BaseQuickAdapter<CommentBean, BaseViewHolder> {


    private List<CommentBean> commentBean;

    public CommentBaseAdapter(int layoutResId, List<CommentBean> data) {
        super(layoutResId, data);
    }

    //历史评论适配器
    @Override
    protected void convert(BaseViewHolder baseViewHolder, CommentBean commentBean) {
        baseViewHolder.setText(R.id.commentItemName,commentBean.getUserName())
                .setText(R.id.commentItemContent,commentBean.getComment())
                .setText(R.id.commentItemTime,commentBean.getCommentDate().split("T")[0]);

        ImageView goodPic = baseViewHolder.getView(R.id.commentItemGoodPic);
        Glide.with(getContext()).load(commentBean.getHeadPic()).into(goodPic);
    }

}
