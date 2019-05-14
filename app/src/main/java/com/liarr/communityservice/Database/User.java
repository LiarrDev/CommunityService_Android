package com.liarr.communityservice.Database;

public class User {

    private int uid;
    private String userName;
    private String tel;
    private int doneNum;
    private float point;

    public User(int uid, String userName, String tel, int doneNum, float point) {
        this.uid = uid;
        this.userName = userName;
        this.tel = tel;
        this.doneNum = doneNum;
        this.point = point;
    }

    public int getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public String getTel() {
        return tel;
    }

    public int getDoneNum() {
        return doneNum;
    }

    public float getPoint() {
        return point;
    }
}
