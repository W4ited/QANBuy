package com.example.qanbuy20.GoodUtil.Click.bean;

public class CommentBean {
    private String userName;
    private String headPic;
    private Integer id;
    private Integer goodID;
    private String comment;
    private String commentDate;
    private Integer userID;

    public CommentBean(String headPic, String userName,Integer goodID, String comment, String commentDate) {
        this.headPic = headPic;
        this.userName = userName;
        this.goodID = goodID;
        this.comment = comment;
        this.commentDate = commentDate;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodID() {
        return goodID;
    }

    public void setGoodID(Integer goodID) {
        this.goodID = goodID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
