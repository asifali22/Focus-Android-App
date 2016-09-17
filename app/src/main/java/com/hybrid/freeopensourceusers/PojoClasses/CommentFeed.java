package com.hybrid.freeopensourceusers.PojoClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by monster on 30/7/16.
 */

public class CommentFeed implements Parcelable {

    private int comment_id;
    private int user_id;
    private int pid;
    private String comment;
    private Date doc;
    private String user_name;
    private String user_pic;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public CommentFeed(Parcel input){

        comment_id = input.readInt();
        user_id = input.readInt();
        pid = input.readInt();
        comment = input.readString();
        long dateMillis = input.readLong();
        doc = new Date(dateMillis);
        user_name = input.readString();
        user_pic = input.readString();


    }

    @Override
    public String toString() {
        return "CommentFeed{" +
                "comment='" + comment + '\'' +
                ", comment_id=" + comment_id +
                ", user_id=" + user_id +
                ", pid=" + pid +
                ", doc=" + doc +
                ", user_name='" + user_name + '\'' +
                ", user_pic='" + user_pic + '\'' +
                ", dateFormat=" + dateFormat +
                '}';
    }

    public CommentFeed(int comment_id,int user_id,int pid, String comment,  String doc,  String user_name, String user_pic) {
        this.comment = comment;
        this.comment_id = comment_id;
        try{
            this.doc= dateFormat.parse(doc);}catch (ParseException p){
            p.printStackTrace();
        }
        this.pid = pid;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_pic = user_pic;
    }

    public CommentFeed(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public Date getDoc() {
        return doc;
    }

    public void setDoc(Date doc) {
        this.doc = doc;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(comment_id);
        parcel.writeInt(user_id);
        parcel.writeInt(pid);
        parcel.writeString(comment);
        parcel.writeLong(doc.getTime());
        parcel.writeString(user_name);
        parcel.writeString(user_pic);

    }

    public static final Parcelable.Creator<CommentFeed> CREATOR
            = new Parcelable.Creator<CommentFeed>() {
        public CommentFeed createFromParcel(Parcel in) {
            return new CommentFeed(in);
        }

        public CommentFeed[] newArray(int size) {
            return new CommentFeed[size];
        }
    };


}
