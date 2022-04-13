package com.example.qanbuy20.Shopping.bean;

public class ShoppingBean {

    private String name;
    private int goodID;
    private String goodPic;
    private int num;
    private int postage;
    private int price;
    private int userID;
    private boolean isChecked;      //是否选中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGoodID() {
        return goodID;
    }

    public void setGoodID(int goodID) {
        this.goodID = goodID;
    }

    public String getGoodPic() {
        return goodPic;
    }

    public void setGoodPic(String goodPic) {
        this.goodPic = goodPic;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
