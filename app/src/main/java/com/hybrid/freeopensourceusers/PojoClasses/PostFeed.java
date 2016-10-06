package com.hybrid.freeopensourceusers.PojoClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class PostFeed implements Parcelable{

    private int pid;
    private int uid;
    private String link;
    private String title;
    private String description;
    private int up;
    private int comment_count;
    private String postPicUrl;
    private Date dop;
    private String user_name;
    private String user_pic;
    private String user_status;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");



    public PostFeed(String title, String link, String description, String dop, String user_name, int sr_key, int up, int comment_count, int uid, String post_pic, String user_pic, String user_status){
        this.title=title;
        this.link=link;
        this.description=description;
        try{
            this.dop= dateFormat.parse(dop);}catch (ParseException p){
            p.printStackTrace();
        }
        this.user_name=user_name;
        this.postPicUrl=post_pic;
        this.pid=sr_key;
        this.up=up;
        this.comment_count=comment_count;
        this.uid=uid;
        this.user_pic=user_pic;
        this.user_status = user_status;

    }



    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getPostPicUrl() {
        return postPicUrl;
    }

    public void setPostPicUrl(String postPicUrl) {
        this.postPicUrl = postPicUrl;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDop() {
        return dop;
    }

    public void setDop(Date dop) {
        this.dop = dop;
    }


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public PostFeed(){

    }

    public PostFeed(Parcel input){

        pid = input.readInt();
        uid = input.readInt();
        link = input.readString();
        title = input.readString();
        description = input.readString();
        up = input.readInt();
        comment_count = input.readInt();
        postPicUrl = input.readString();
        long dateMillis = input.readLong();
        dop = new Date(dateMillis);
        user_name = input.readString();
        user_pic = input.readString();
        user_status = input.readString();


    }

    @Override
    public String toString() {
        return pid + " " + uid + " " + link + " " + title + " " + description + " " + up + " " + comment_count + " " + postPicUrl + " " + dop + " " + user_name + " " + user_pic + "\n \n ";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(pid);
        parcel.writeInt(uid);
        parcel.writeString(link);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(up);
        parcel.writeInt(comment_count);
        parcel.writeString(postPicUrl);
        parcel.writeLong(dop.getTime());
        parcel.writeString(user_name);
        parcel.writeString(user_pic);
        parcel.writeString(user_status);


    }



    public static final Parcelable.Creator<PostFeed> CREATOR
            = new Parcelable.Creator<PostFeed>() {
        public PostFeed createFromParcel(Parcel in) {
            return new PostFeed(in);
        }

        public PostFeed[] newArray(int size) {
            return new PostFeed[size];
        }
    };






}
