package com.hybrid.freeopensourceusers.Utility;

import android.content.SharedPreferences;
import android.util.Log;

import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    private static MyApplication myApplication;

    private static final String IPADDRESS = "http://focusvce.com/api/v1/";

    public static String getIPADDRESS() {
        return IPADDRESS;
    }

    //Email Pattern
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }




}

