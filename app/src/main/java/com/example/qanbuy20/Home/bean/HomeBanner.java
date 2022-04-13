package com.example.qanbuy20.Home.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class HomeBanner {
    private int id;
    private String name;
    private String goodPic;
    private int price;
    private int saleNum;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodPic() {
        return goodPic;
    }

    public void setGoodPic(String goodPic) {
        this.goodPic = goodPic;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }

    public Bitmap stringToBitmap(String pic){
        byte[] bitmapByte = Base64.decode(pic.split(",")[1], Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
        return bitmap;
    }
}
