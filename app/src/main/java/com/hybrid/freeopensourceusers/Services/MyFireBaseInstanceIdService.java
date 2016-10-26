package com.hybrid.freeopensourceusers.Services;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hybrid.freeopensourceusers.Activities.new_session_add;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by monster on 30/8/16.
 */

public class MyFireBaseInstanceIdService extends FirebaseInstanceIdService {


    MyApplication myApplication;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    @Override
    public void onTokenRefresh() {
        String recentToken = FirebaseInstanceId.getInstance().getToken();
        registerToken(recentToken);
    }

    public String registerToken(final String token) {
        myApplication = MyApplication.getInstance();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        String UPLOAD_URL = "http://focusvce.com/api/v1/update_token";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        JSONObject jsonObject;
                        try{
                            jsonObject = new JSONObject(s);
                            String fcm_token=jsonObject.getString("fcm_token");
                            SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", MODE_PRIVATE);
                            sharedPreferences.edit().putString("fcm_token",fcm_token).apply();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog


                        //Showing toast
                        Log.e("Error", volleyError.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", getApiKey() + "");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String





                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters

                params.put("token", token);


                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });


        //Adding request to the queue
        requestQueue.add(stringRequest);
        return token;
    }
    public String getApiKey() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String api_key = sharedPreferences.getString("api_key", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return "";
    }
}
