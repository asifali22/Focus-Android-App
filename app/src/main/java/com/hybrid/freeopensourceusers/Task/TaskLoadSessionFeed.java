package com.hybrid.freeopensourceusers.Task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.PostFeedLoadingListener;
import com.hybrid.freeopensourceusers.Callback.SessionFeedLoadingListener;
import com.hybrid.freeopensourceusers.Logging.L;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations_Session;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by adarsh on 6/9/16.
 */

    public class TaskLoadSessionFeed extends AsyncTask<Void, Void, ArrayList<SessionFeed>> {

    SessionFeedLoadingListener myComponent;
    private VolleySingleton mVolleySingleton;
    private RequestQueue mRequestQueue;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public TaskLoadSessionFeed(SessionFeedLoadingListener myComponent) {
        this.myComponent = myComponent;
        mVolleySingleton = VolleySingleton.getInstance();
        mRequestQueue = mVolleySingleton.getRequestQueue();

    }

    @Override
    protected ArrayList<SessionFeed> doInBackground(Void... voids) {
        JSONObject response = sendJsonrequest();
        DatabaseOperations_Session dop = new DatabaseOperations_Session(MyApplication.getAppContext());
        ArrayList<SessionFeed> newsFeedLists = parseJsonResponse(response);
        dop.insertSessions(dop, newsFeedLists, true);
        return newsFeedLists;
    }

    @Override
    protected void onPostExecute(ArrayList<SessionFeed> sessionFeeds) {
        if(myComponent!=null)
            myComponent.onSessionFeedLoaded(sessionFeeds);
        super.onPostExecute(sessionFeeds);
    }

    private ArrayList<SessionFeed> parseJsonResponse(JSONObject response) {
        ArrayList<SessionFeed> newsFeedsList = new ArrayList<>();
        if (response != null && response.length() != 0) {

            try {
                JSONArray objectArray = response.getJSONArray("posts");
                for (int i = objectArray.length() - 1; i >= 0; i--) {

                    int session_id = objectArray.getJSONObject(i).getInt("session_id");
                    String s_title = objectArray.getJSONObject(i).getString("s_title");
                    String s_description = objectArray.getJSONObject(i).getString("s_description");
                    String s_picurl = objectArray.getJSONObject(i).getString("s_picurl");
                    String s_venue = objectArray.getJSONObject(i).getString("s_venue");
                    String s_coordinator = objectArray.getJSONObject(i).getString("s_coordinator");
                    String s_c_email = objectArray.getJSONObject(i).getString("s_c_email");
                    String s_c_phone = objectArray.getJSONObject(i).getString("s_c_phone");
                    String resource_person = objectArray.getJSONObject(i).getString("resource_person");
                    String rp_desg = objectArray.getJSONObject(i).getString("rp_desg");
                    String date_time = objectArray.getJSONObject(i).getString("date_time");
                    String address = objectArray.getJSONObject(i).getString("address");
                    String room = objectArray.getJSONObject(i).getString("room");
                    String dosp = objectArray.getJSONObject(i).getString("dosp");
                    String user_name = objectArray.getJSONObject(i).getString("user_name");
                    String user_pic = objectArray.getJSONObject(i).getString("user_pic");
                    String user_status = objectArray.getJSONObject(i).getString("user_status");
                    int uid = objectArray.getJSONObject(i).getInt("uid");


                    SessionFeed postFeedObject = new SessionFeed();
                    postFeedObject.setSession_id(session_id);
                    postFeedObject.setSession_title(s_title);
                    postFeedObject.setSession_description(s_description);
                    postFeedObject.setSession_image(s_picurl);
                    postFeedObject.setS_venue(s_venue);
                    postFeedObject.setS_coordinator(s_coordinator);
                    postFeedObject.setS_c_email(s_c_email);
                    postFeedObject.setS_c_phone(s_c_phone);
                    postFeedObject.setResource_person(resource_person);
                    postFeedObject.setRp_desg(rp_desg);
                    postFeedObject.setTime_and_date(date_time);
                    postFeedObject.setAddress(address);
                    postFeedObject.setRoom(room);
                    Date dateOfPost = dateFormat.parse(dosp);
                    postFeedObject.setDosp(dateOfPost);
                    postFeedObject.setUser_name(user_name);
                    postFeedObject.setUser_pic(user_pic);
                    postFeedObject.setUser_status(user_status);
                    postFeedObject.setUid(uid);
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


    private JSONObject sendJsonrequest() {

        String URL= Utility.getIPADDRESS()+"get_session";

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
}
