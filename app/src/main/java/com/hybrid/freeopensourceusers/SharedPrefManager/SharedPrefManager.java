package com.hybrid.freeopensourceusers.SharedPrefManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.BooleanResult;
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by adarsh on 14/10/16.
 */

public class SharedPrefManager {

    MyApplication myApplication;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesFirstRun;
    Context context;

    public SharedPrefManager(Context context){
        this.context=context;
        myApplication = MyApplication.getInstance();
        sharedPreferences = context.getSharedPreferences("user_details", MODE_PRIVATE);
        sharedPreferencesFirstRun = context.getSharedPreferences("com.hybrid.freeopensourceusers", MODE_PRIVATE);
    }
    public boolean getFirstRunStatus(){
        return sharedPreferencesFirstRun.getBoolean("firstrun",true);
    }

    public boolean getLoggedinStatus(){
        return sharedPreferences.getBoolean("logged_in",false);
    }
    public void setUserImage(String image){
        sharedPreferences.edit().putString("user_pic",image).apply();
    }
    public void setFirstRunStatus(boolean status){
        sharedPreferencesFirstRun.edit().putBoolean("firstrun",status).apply();
    }

    public void setLoggedInStatus(Boolean status){
        sharedPreferences.edit().putBoolean("logged_in", status).apply();
    }

    public void setUserStatusOnLogin(String user_name,String user_email,String api_key,String fcm_token,String su_user,String user_pic,int user_id, String user_status,String about,String area_of_interest,String organisation, int not_post, int not_sess){

        sharedPreferences.edit().putString("user_name", user_name).apply();
        sharedPreferences.edit().putString("user_email", user_email).apply();
        sharedPreferences.edit().putString("api_key", api_key).apply();
        sharedPreferences.edit().putString("fcm_token", fcm_token).apply();
        sharedPreferences.edit().putBoolean("logged_in", true).apply();
        sharedPreferences.edit().putString("su_user",su_user).apply();
        sharedPreferences.edit().putString("user_pic",user_pic).apply();
        sharedPreferences.edit().putInt("user_id",user_id).apply();
        sharedPreferences.edit().putString("user_status",user_status).apply();
        sharedPreferences.edit().putString("about_user",about).apply();
        sharedPreferences.edit().putString("area_of_interest",area_of_interest).apply();
        sharedPreferences.edit().putString("organisation",organisation).apply();
        sharedPreferences.edit().putInt("not_post",not_post).apply();
        sharedPreferences.edit().putInt("not_sess",not_sess).apply();
    }

    public int getNotPost(){
        return sharedPreferences.getInt("not_post",0);
    }

    public int getNotSess(){
        return sharedPreferences.getInt("not_sess",0);
    }

    public void setNotSess(int not_sess){
        sharedPreferences.edit().putInt("not_sess",not_sess).apply();
    }

    public void setNotPost(int not_post){
        sharedPreferences.edit().putInt("not_post",not_post).apply();
    }

    public void updateUserDesc(String about){
        sharedPreferences.edit().putString("about_user",about).apply();
    }

    public void updateUserInterest(String aoi){
        sharedPreferences.edit().putString("area_of_interest",aoi).apply();
    }

    public void updateUserOrganisation(String org){
        sharedPreferences.edit().putString("organisation",org).apply();
    }

    public void updateUserStatus(String status){
        sharedPreferences.edit().putString("user_status",status).apply();
    }


    public int getUser_id(){
        int user_id = sharedPreferences.getInt("user_id",0);
        return user_id;
    }
    public String getAboutUser(){
        String userEmail = sharedPreferences.getString("about_user", null);
        if (!userEmail.isEmpty())
            return userEmail;
        else
            return null;
    }

    public String getAreaOfInterest(){
        String userEmail = sharedPreferences.getString("area_of_interest", null);
        if (!userEmail.isEmpty())
            return userEmail;
        else
            return null;
    }

    public String getOrganisation(){
        String userEmail = sharedPreferences.getString("organisation", null);
        if (!userEmail.isEmpty())
            return userEmail;
        else
            return null;
    }

    public String getUserEmail(){
        String userEmail = sharedPreferences.getString("user_email", null);
        if (!userEmail.isEmpty())
            return userEmail;
        else
            return null;
    }

    public String getUserStatus(){
        String userStatus = sharedPreferences.getString("user_status", null);
        if (!userStatus.isEmpty())
            return userStatus;
        else
            return null;
    }


    public String getUserImage(){
        String userImage = sharedPreferences.getString("user_pic", null);
        if (!userImage.isEmpty())
            return userImage;
        else
            return null;
    }

    public String getSu_User(){
        String su_user = sharedPreferences.getString("su_user",null);
        if(!su_user.isEmpty())
            return su_user;
        else
            return null;
    }

    public String getFcmToken() {

        String api_key = sharedPreferences.getString("fcm_token", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }

    public String getApiKey() {

        String api_key = sharedPreferences.getString("api_key", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }

    public String getUserName() {

        String user_name_from_sf = sharedPreferences.getString("user_name", null);

        if (!user_name_from_sf.isEmpty()) {
            return user_name_from_sf;
        } else
            return null;
    }

    public boolean isLoggedIn() {
        boolean status = sharedPreferences.getBoolean("logged_in", false);
        return status;

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void showAlertDialog(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Sign up?")
                .setMessage("Join us to explore more!")
                .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
                        context.startActivity(myIntent);
                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }


}
