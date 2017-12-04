package com.fit.run.bean;

import com.google.firebase.database.IgnoreExtraProperties;



@IgnoreExtraProperties
public class Challenge {
    private String uid;
    private int point;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
