package com.hybrid.freeopensourceusers.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.freeopensourceusers.Adapters.RecyclerTrendingCommentAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.CommentFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Services.MyFirebaseMessagingService;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations_Session;
import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
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
import java.util.Map;

public class Comment_Actiivity extends AppCompatActivity {


    String URL = Utility.getIPADDRESS() + "commentsbypid";
    private Toolbar newCommentToolbar;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private ArrayList<CommentFeed> commentsFeedsList = new ArrayList<>();
    private RecyclerView commentsFeedRecycler;
    private RecyclerTrendingCommentAdapter recyclerTrendingCommentAdapter;
    private static final String COMMENTS_FEED = "comments_feed";


    public String api_key;
    public String pid;
    int flag_extra;
    EditText commentAdd;
    FloatingActionButton button;
    DatabaseOperations dp;
    DatabaseOperations_Session dops;
    private SharedPrefManager sharedPrefManager;
    public SwipeRefreshLayout swipeRefreshLayoutForCommentsTrending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_actiivity);

        newCommentToolbar = (Toolbar) findViewById(R.id.newCommentToolbar);
        if (newCommentToolbar != null)
            setSupportActionBar(newCommentToolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Discuss");


        Toast toast = Toast.makeText(this,
                "Please refresh to load newer comments", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 250);
        toast.show();

        sharedPrefManager = new SharedPrefManager(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dp = new DatabaseOperations(this);
        dops = new DatabaseOperations_Session(this);
        commentsFeedRecycler = (RecyclerView) findViewById(R.id.comment_recyclerView);
        swipeRefreshLayoutForCommentsTrending = (SwipeRefreshLayout) findViewById(R.id.swiperefreshForTrending);
        commentAdd = (EditText) findViewById(R.id.getComment);
        button = (FloatingActionButton) findViewById(R.id.add_Comment_Button);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        commentsFeedRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentsFeedRecycler.setHasFixedSize(true);
        recyclerTrendingCommentAdapter = new RecyclerTrendingCommentAdapter(this);
        commentsFeedRecycler.setAdapter(recyclerTrendingCommentAdapter);
        Bundle extras = getIntent().getExtras();
        pid = extras.getString("PID_VALUE");
        if(pid==null){
            SharedPreferences sharedPreferences = getSharedPreferences("comment",MODE_PRIVATE);
            pid=sharedPreferences.getString("comment_pid",null);
        }
        api_key = extras.getString("API_KEY");
        flag_extra = extras.getInt("FLAG");
        if (savedInstanceState != null) {
            commentsFeedsList = savedInstanceState.getParcelableArrayList(COMMENTS_FEED);
        } else {

            DatabaseOperations dop = new DatabaseOperations(MyApplication.getAppContext());
            DatabaseOperations_Session dops = new DatabaseOperations_Session(MyApplication.getAppContext());
            if (flag_extra == 0)
                commentsFeedsList = MyApplication.getDatabase().readCommentDataForPost(pid, dop);
            else
                commentsFeedsList = MyApplication.getMsDatabase().readCommentDataForPost(pid, dops);
            if (commentsFeedsList.isEmpty()) {
                sendJsonrequest();
            }

        }

        recyclerTrendingCommentAdapter.setFeed(commentsFeedsList);

        swipeRefreshLayoutForCommentsTrending.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (sharedPrefManager.isOnline()) {
                    swipeRefreshLayoutForCommentsTrending.setRefreshing(true);
                    sendJsonrequest();
                } else {
                    Toast.makeText(Comment_Actiivity.this, "No Network", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayoutForCommentsTrending.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayoutForCommentsTrending.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(COMMENTS_FEED, commentsFeedsList);

    }

    private void sendJsonrequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                commentsFeedsList = parseJsonResponse(response);
                recyclerTrendingCommentAdapter.setFeed(commentsFeedsList);
                if (swipeRefreshLayoutForCommentsTrending.isRefreshing())
                    swipeRefreshLayoutForCommentsTrending.setRefreshing(false);

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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pid", pid);
                params.put("flag", Integer.toString(flag_extra));
                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    private ArrayList<CommentFeed> parseJsonResponse(String response) {
        if (flag_extra == 0)
            dp.delete_commentbyPid(pid, dp);
        else
            dops.delete_commentbyPid(pid, dops);
        ArrayList<CommentFeed> commentsFeedsList = new ArrayList<>();
        if (response != null && response.length() != 0) {
            try {
                JSONObject jObject = new JSONObject(response);
                JSONArray objectArray = jObject.getJSONArray("comments");
                for (int i = objectArray.length() - 1; i >= 0; i--) {

                    int comment_id = objectArray.getJSONObject(i).getInt("comment_id");
                    int user_id = objectArray.getJSONObject(i).getInt("user_id");
                    int pid = objectArray.getJSONObject(i).getInt("pid");
                    String comment = objectArray.getJSONObject(i).getString("comment");
                    String doc = objectArray.getJSONObject(i).getString("date of comment");
                    String user_name = objectArray.getJSONObject(i).getString("user_name");
                    String user_pic = objectArray.getJSONObject(i).getString("user_pic");

                    CommentFeed commentFeedObject = new CommentFeed();
                    commentFeedObject.setComment_id(comment_id);
                    commentFeedObject.setUser_id(user_id);
                    commentFeedObject.setPid(pid);
                    commentFeedObject.setComment(comment);
                    Date dateOfPost = dateFormat.parse(doc);
                    long commentTimeStamp = dateOfPost.getTime();
                    commentFeedObject.setDoc(dateOfPost);
                    commentFeedObject.setUser_name(user_name);
                    commentFeedObject.setUser_pic(user_pic);
                    if (flag_extra == 0)
                        dp.putInfo_Comment(dp, comment_id, user_id, pid, comment, commentTimeStamp, user_name, user_pic);
                    else
                        dops.putInfo_Comment(dops, comment_id, user_id, pid, comment, commentTimeStamp, user_name, user_pic);
                    commentsFeedsList.add(commentFeedObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return commentsFeedsList;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.comment_menu_trending, menu);

        return true;
    }


    public void commentFabClicked(View v) {
        final String commentText = commentAdd.getText().toString().trim();
        if (!commentText.isEmpty() && sharedPrefManager.isOnline()) {
            String POSTCOMMENT_URL = Utility.getIPADDRESS() + "comments";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, POSTCOMMENT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null && response.length() != 0) {
                        try {
                            Log.e("ADARSH", response);
                            JSONObject jObject = new JSONObject(response);
                            int comment_id = jObject.getInt("comment_id");
                            int user_id = jObject.getInt("user_id");
                            int pid = jObject.getInt("pid");
                            String comment = jObject.getString("comment");
                            String doc = jObject.getString("date of comment");
                            String user_name = jObject.getString("user_name");
                            String user_pic = jObject.getString("user_pic");


                            CommentFeed commentFeedObject = new CommentFeed();
                            commentFeedObject.setComment_id(comment_id);
                            commentFeedObject.setUser_id(user_id);
                            commentFeedObject.setPid(pid);
                            commentFeedObject.setComment(comment);
                            Date dateOfPost = dateFormat.parse(doc);
                            long commentTimeStamp = dateOfPost.getTime();
                            commentFeedObject.setDoc(dateOfPost);
                            commentFeedObject.setUser_name(user_name);
                            commentFeedObject.setUser_pic(user_pic);
                            if (flag_extra == 0)
                                dp.putInfo_Comment(dp, comment_id, user_id, pid, comment, commentTimeStamp, user_name, user_pic);
                            else
                                dops.putInfo_Comment(dops, comment_id, user_id, pid, comment, commentTimeStamp, user_name, user_pic);
                            commentsFeedsList.add(0, commentFeedObject);
                            recyclerTrendingCommentAdapter.setNewCommentFeed(commentsFeedsList);
                            commentsFeedRecycler.smoothScrollToPosition(0);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("pid", pid);
                    params.put("comment", commentText);
                    params.put("flag", Integer.toString(flag_extra));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
            commentAdd.setText("");
            // close keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            commentAdd.setText("");
        }
    }


}



