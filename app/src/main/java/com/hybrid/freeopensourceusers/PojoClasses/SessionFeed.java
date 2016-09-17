package com.hybrid.freeopensourceusers.PojoClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adarsh on 6/9/16.
 */

public class SessionFeed implements Parcelable{
    private String session_title;
    private String session_image;
    private String session_description;
    private int session_id;
    private String s_venue;
    private String s_coordinator;
    private String s_c_email;
    private String s_c_phone;
    private String resource_person;
    private String rp_desg;
    private String time_and_date;
    private String address;
    private String room;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private Date Dosp;
    String user_name;
    int uid;
    String user_status;
    String user_pic;



    public SessionFeed(String tit, String img, String desc, int id){
        this.session_title=tit;
        this.session_image=img;
        this.session_description=desc;
        this.session_id=id;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public String getSession_title() {
        return session_title;
    }

    public void setSession_title(String session_title) {
        this.session_title = session_title;
    }

    public String getSession_image() {
        return session_image;
    }

    public void setSession_image(String session_image) {
        this.session_image = session_image;
    }

    public String getSession_description() {
        return session_description;
    }

    public void setSession_description(String session_description) {
        this.session_description = session_description;
    }

    public String getS_venue() {
        return s_venue;
    }

    public void setS_venue(String s_venue) {
        this.s_venue = s_venue;
    }

    public String getS_coordinator() {
        return s_coordinator;
    }

    public void setS_coordinator(String s_coordinator) {
        this.s_coordinator = s_coordinator;
    }

    public String getS_c_email() {
        return s_c_email;
    }

    public void setS_c_email(String s_c_email) {
        this.s_c_email = s_c_email;
    }

    public String getS_c_phone() {
        return s_c_phone;
    }

    public void setS_c_phone(String s_c_phone) {
        this.s_c_phone = s_c_phone;
    }
    public String getResource_person() {
        return resource_person;
    }

    public void setResource_person(String resource_person) {
        this.resource_person = resource_person;
    }

    public String getRp_desg() {
        return rp_desg;
    }

    public void setRp_desg(String rs_desg) {
        this.rp_desg = rs_desg;
    }

    public String getTime_and_date() {
        return time_and_date;
    }

    public void setTime_and_date(String time_and_date) {
        this.time_and_date = time_and_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public Date getDosp() {
        return Dosp;
    }

    public void setDosp(Date dosp) {
        Dosp = dosp;
    }

    public SessionFeed(){
    }

    public SessionFeed(Parcel input){
        session_title = input.readString();
        session_image=input.readString();
        session_description=input.readString();
        session_id=input.readInt();
        s_venue=input.readString();
        s_coordinator=input.readString();
        s_c_email=input.readString();
        s_c_phone=input.readString();
        resource_person=input.readString();
        rp_desg=input.readString();
        time_and_date=input.readString();
        address=input.readString();
        room=input.readString();
        long dateMillis = input.readLong();
        Dosp = new Date(dateMillis);
        user_name=input.readString();
        user_status=input.readString();
        user_pic=input.readString();
        uid=input.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(session_title);
        parcel.writeString(session_image);
        parcel.writeString(session_description);
        parcel.writeInt(session_id);
        parcel.writeString(s_venue);
        parcel.writeString(s_coordinator);
        parcel.writeString(s_c_email);
        parcel.writeString(s_c_phone);
        parcel.writeString(resource_person);
        parcel.writeString(rp_desg);
        parcel.writeString(time_and_date);
        parcel.writeString(address);
        parcel.writeString(room);
        parcel.writeLong(Dosp.getTime());
        parcel.writeString(user_name);
        parcel.writeString(user_status);
        parcel.writeString(user_pic);
        parcel.writeInt(uid);

    }

    public static final Creator<SessionFeed> CREATOR
            = new Creator<SessionFeed>() {
        public SessionFeed createFromParcel(Parcel in) {
            return new SessionFeed(in);
        }

        public SessionFeed[] newArray(int size) {
            return new SessionFeed[size];
        }
    };
}
