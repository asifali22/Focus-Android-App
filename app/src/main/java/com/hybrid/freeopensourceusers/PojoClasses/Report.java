package com.hybrid.freeopensourceusers.PojoClasses;

/**
 * Created by adarsh on 18/10/16.
 */

public class Report {

    int pid;
    int uid;
    public Report(){

    }
    public Report(int pid,int uid){
        this.pid = pid;
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
