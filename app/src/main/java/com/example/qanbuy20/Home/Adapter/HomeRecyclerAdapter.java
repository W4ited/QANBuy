package com.example.qanbuy20.Home.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qanbuy20.Home.bean.HomeGood;
import com.example.qanbuy20.R;

import java.util.List;

//已经改为BaseQuickAdapter
public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private List<HomeGood> list;
    private Context context;

    public HomeRecyclerAdapter(List<HomeGood> list) {
        this.list = list;
    }

    public HomeRecyclerAdapter(List<HomeGood> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_gooditem, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HomeGood homeGood = list.get(position);
        holder.goodPic.setImageBitmap(homeGood.stringToBitmap(homeGood.goodPic));
        holder.name.setText(homeGood.name);
        holder.price.setText(String.valueOf(homeGood.price));
        holder.saleNum.setText(String.valueOf(homeGood.saleNum));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView goodPic;
        TextView name;
        TextView price;
        TextView saleNum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.home_card);
            goodPic = (ImageView) itemView.findViewById(R.id.home_goodPic);
            name = (TextView) itemView.findViewById(R.id.home_name);
            price = (TextView) itemView.findViewById(R.id.home_price);
            saleNum = (TextView) itemView.findViewById(R.id.home_saleNum);
        }
    }
}
