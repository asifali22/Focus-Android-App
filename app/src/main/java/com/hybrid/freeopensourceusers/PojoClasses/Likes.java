package com.hybrid.freeopensourceusers.PojoClasses;

/**
 * Created by adarsh on 13/8/16.
 */

public class Likes {
    private int user_id;
    private int pid;
    private int flag;
    private int flagd;

    public Likes() {
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlagd() {
        return flagd;
    }

    public void setFlagd(int flagd) {
        this.flagd = flagd;
    }
}
