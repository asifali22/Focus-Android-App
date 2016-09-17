package com.hybrid.freeopensourceusers.Task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.PostFeedLoadingListener;
import com.hybrid.freeopensourceusers.Logging.L;
import com.hybrid.freeopensourceusers.PojoClasses.Likes;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



/**
 * Created by monster on 10/8/16.
 */

public class TaskLoadPostFeed extends AsyncTask<Void, Void, ArrayList<PostFeed>> {
    PostFeedLoadingListener myComponent;
    private VolleySingleton mVolleySingleton;
    private RequestQueue mRequestQueue;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    DatabaseOperations dop = new DatabaseOperations(MyApplication.getAppContext());

    public TaskLoadPostFeed(PostFeedLoadingListener myComponent) {
        this.myComponent = myComponent;
        mVolleySingleton = VolleySingleton.getInstance();
        mRequestQueue = mVolleySingleton.getRequestQueue();

    }



    @Override
    protected ArrayList<PostFeed> doInBackground(Void... params) {
        JSONObject response = sendJsonrequest();
        JSONObject likeResponse = getPids();

        ArrayList<PostFeed> newsFeedLists = parseJsonResponse(response);
        ArrayList<Likes> likeList = parseLike(likeResponse);
        dop.insertPosts(dop, newsFeedLists, true);
        dop.insertLikes(dop,likeList,true);
        return newsFeedLists;
    }

    @Override
    protected void onPostExecute(ArrayList<PostFeed> newsFeedlists) {
        if (myComponent != null) {
            myComponent.onPostFeedLoaded(newsFeedlists);
        }
    }

    private static String URL = Utility.getIPADDRESS() + "nologin";

    public JSONObject getPids() {
        String URL = Utility.getIPADDRESS() + "getlikebyapi";
        JSONObject response = null;
        String stringResponse;

        RequestFuture<String> requestFuture = RequestFuture.newFuture();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, requestFuture, requestFuture){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                String api_key = sharedPreferences.getString("api_key", null);
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",api_key);
                return params;
            }};

        mRequestQueue.add(stringRequest);

        try {
            stringResponse = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(stringResponse);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
//        {
//            @Override
//            public void onResponse(String response) {
//                JSONObject jsonObject;
//                ArrayList<Likes> l = new ArrayList<>();
//                try{
//                    jsonObject = new JSONObject(response);
//                    l = parseLike(jsonObject);
//                    dop.insertLikes(dop,l,true);
//
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        })
//
//
//        };
//        mRequestQueue.add(stringRequest);
    }
    private ArrayList<Likes> parseLike(JSONObject jsonObject){
        ArrayList<Likes> likes = new ArrayList<>();
        if (jsonObject != null && jsonObject.length() != 0) {



            try {
                JSONArray objectArray = jsonObject.getJSONArray("like");
                for (int i = objectArray.length() - 1; i >= 0; i--) {

                    int pid = objectArray.getJSONObject(i).getInt("pid");
                    int user_id = objectArray.getJSONObject(i).getInt("user_id");
                    int flag = objectArray.getJSONObject(i).getInt("flag");
                    int flagd = objectArray.getJSONObject(i).getInt("flagd");
                    Likes like = new Likes();
                    like.setPid(pid);
                    like.setUser_id(user_id);
                    like.setFlag(flag);
                    like.setFlagd(flagd);
                    likes.add(like);


                }

            } catch (JSONException e) {
                Log.e("ADARSH",e.toString());
                e.printStackTrace();
            }


        }
        return likes;
    }

    private JSONObject sendJsonrequest() {

        JSONObject response = null;

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, requestFuture, requestFuture);

        mRequestQueue.add(jsonObjectRequest);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;

    }

    private ArrayList<PostFeed> parseJsonResponse(JSONObject response) {
        ArrayList<PostFeed> newsFeedsList = new ArrayList<>();
        if (response != null && response.length() != 0) {

            try {
                JSONArray objectArray = response.getJSONArray("posts");
                for (int i = objectArray.length() - 1; i >= 0; i--) {

                    int pid = objectArray.getJSONObject(i).getInt("Post_id");
                    int uid = objectArray.getJSONObject(i).getInt("User_id");
                    String link = objectArray.getJSONObject(i).getString("Link");
                    String title = objectArray.getJSONObject(i).getString("Title");
                    String description = objectArray.getJSONObject(i).getString("Description");
                    int up = objectArray.getJSONObject(i).getInt("Likes");
                    int comment_count = objectArray.getJSONObject(i).getInt("no_comments");
                    String postpic = objectArray.getJSONObject(i).getString("Picture_url");
                    String dop = objectArray.getJSONObject(i).getString("Date_of_post");
                    String user_name = objectArray.getJSONObject(i).getString("User_name");
                    String user_pic = objectArray.getJSONObject(i).getString("User_pic");
                    String user_status = objectArray.getJSONObject(i).getString("User_status");

                    PostFeed postFeedObject = new PostFeed();
                    postFeedObject.setPid(pid);
                    postFeedObject.setUid(uid);
                    postFeedObject.setLink(link);
                    postFeedObject.setTitle(title);
                    postFeedObject.setDescription(description);
                    postFeedObject.setUp(up);
                    postFeedObject.setComment_count(comment_count);
                    postFeedObject.setPostPicUrl(postpic);
                    Date dateOfPost = dateFormat.parse(dop);
                    postFeedObject.setDop(dateOfPost);
                    postFeedObject.setUser_name(user_name);
                    postFeedObject.setUser_pic(user_pic);
                    postFeedObject.setUser_status(user_status);


                    newsFeedsList.add(postFeedObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        return newsFeedsList;
    }
    public int getUid() {
        final String api_key;
        final int[] uid = new int[1];
        uid[0]=0;
        String URL =Utility.getIPADDRESS() + "uidbyapi";
        SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    uid[0] =jsonObject.getInt("uid");

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", api_key);
                return params;
            }


        };
        mRequestQueue.add(stringRequest);
        return uid[0];

    }

}


