package com.fit.run.bean;

/**
 * Created on 17/11/13 14:51
 *
 *
 */

public class Account {
    private String account;

    private boolean isFriend;

    private String lover;

    private Integer integral;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }


    public String getLover() {
        return lover;
    }

    public void setLover(String lover) {
        this.lover = lover;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }
}
