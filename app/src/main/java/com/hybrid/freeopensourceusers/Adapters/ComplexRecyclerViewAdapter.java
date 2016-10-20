package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hybrid.freeopensourceusers.Activities.Comment_Actiivity;
import com.hybrid.freeopensourceusers.Activities.WebViewActivity;
import com.hybrid.freeopensourceusers.Activities.session_details;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.Feeds;
import com.hybrid.freeopensourceusers.PojoClasses.Likes;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.RecyclerHeader;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SearchStuffs.ViewHolder1;
import com.hybrid.freeopensourceusers.SearchStuffs.ViewHolder2;
import com.hybrid.freeopensourceusers.SearchStuffs.ViewHolder3;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfile;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by monster on 25/9/16.
 */

public class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<Feeds> feedsArrayList = new ArrayList<>();
    private LayoutInflater layoutInflater = null;
    private DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private MyApplication myApplication;
    private SharedPrefManager sharedPrefManager;
    private DatabaseOperations dop;
    private final int POSTFEED = 0, SESSIONFEED = 1, HEADER = 3;
    private Context context;


    public ComplexRecyclerViewAdapter(Context context, ArrayList<Feeds> feedsArrayList) {

        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.feedsArrayList = feedsArrayList;

        dop = new DatabaseOperations(MyApplication.getAppContext());
    }


    public void setFeed(ArrayList<Feeds> items) {
        this.feedsArrayList = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case POSTFEED:
                View v1 = inflater.inflate(R.layout.trending_row_layout, parent, false);
                viewHolder = new ViewHolder1(v1);
                break;
            case HEADER:
                View v3 = inflater.inflate(R.layout.header_layout, parent, false);
                viewHolder = new ViewHolder3(v3);
                break;
            case SESSIONFEED:
                View v2 = inflater.inflate(R.layout.session_row_layout, parent, false);
                viewHolder = new ViewHolder2(v2);
                break;


        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case POSTFEED:
                ViewHolder1 vh1 = (ViewHolder1) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case HEADER:
                ViewHolder3 vh3 = (ViewHolder3) viewHolder;
                configureViewHolder3(vh3, position);
                break;
            case SESSIONFEED:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, position);
                break;

        }
    }

    private void configureViewHolder3(ViewHolder3 vh3, int position) {

        final RecyclerHeader header = feedsArrayList.get(position).getRecyclerHeader();

        vh3.headerTVforHeader.setText(header.getmHeader());

    }


    private void configureViewHolder1(final ViewHolder1 holder, int position) {

        final MyTextDrawable myTextDrawable = new MyTextDrawable();

        final PostFeed postFeed = feedsArrayList.get(position).getPostFeed();

        holder.user_name.setText(postFeed.getUser_name());
        Date gotDate = postFeed.getDop();
        String formatedDate = dateFormat.format(gotDate);
        holder.user_share_time.setText(formatedDate);
        holder.post_title.setText(postFeed.getTitle());
        if (postFeed.getDescription() != null || postFeed.getDescription().isEmpty()) {
            holder.post_description.setText(postFeed.getDescription());
            holder.postDescriptionNOImage.setText(postFeed.getDescription());
            holder.post_descriptionCustom.setText(postFeed.getDescription());
            holder.post_descriptionBelowImage.setText(postFeed.getDescription());
        }
        else {
            holder.post_description.setText(postFeed.getTitle());
            holder.postDescriptionNOImage.setText(postFeed.getTitle());
            holder.post_descriptionCustom.setText(postFeed.getTitle());
            holder.post_descriptionBelowImage.setText(postFeed.getTitle());
        }
        holder.like_count.setText(postFeed.getUp() + "");
        holder.comment_count.setText(postFeed.getComment_count() + "");
        holder.postTitleNoImage.setText(postFeed.getTitle());
        final String avatar = postFeed.getUser_pic();
        final String postpic = postFeed.getPostPicUrl();
        if (button(postFeed.getPid()) == -1) {
            holder.plus_like.setLiked(false);
            holder.minus_dislike.setLiked(false);
        } else if (button(postFeed.getPid()) == 2) {
            holder.plus_like.setLiked(true);
            holder.minus_dislike.setLiked(false);
        } else if (button(postFeed.getPid()) == 1) {
            holder.plus_like.setLiked(false);
            holder.minus_dislike.setLiked(true);
        } else {
            holder.plus_like.setLiked(false);
            holder.minus_dislike.setLiked(false);
        }
        if (!avatar.isEmpty()) {

            Glide.with(MyApplication.getAppContext())
                    .load(avatar)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.blank_person_final)
                    .error(myTextDrawable.setTextDrawable(postFeed.getUser_name()))
                    .into(holder.circleImageView);

        } else {
            // default
            holder.circleImageView.setImageDrawable(myTextDrawable.setTextDrawable(postFeed.getUser_name()));

        }
        if (!postpic.isEmpty()) {

            Glide.with(MyApplication.getAppContext())
                    .load(postpic)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading)
                    .error(myTextDrawable.setTextDrawableForError("Error!"))
                    .crossFade()
                    .into(holder.post_pic);

            holder.post_body.setVisibility(View.VISIBLE);
            holder.postBodyNoImage.setVisibility(View.GONE);


        } else if (postpic.isEmpty()){
//            holder.post_pic.setVisibility(View.GONE);
//             default
            //holder.post_pic.setImageDrawable(myTextDrawable.setTextDrawableForPost(postFeed.getTitle(), "No Image!"));
            holder.post_body.setVisibility(View.GONE);
            holder.postBodyNoImage.setVisibility(View.VISIBLE);
        }

        if (!postFeed.getLink().isEmpty()) {
            if (postFeed.getLink().equals("abc")) {
                holder.post_description.setVisibility(View.GONE);
                holder.post_descriptionCustom.setVisibility(View.VISIBLE);
            }else {
                holder.post_description.setVisibility(View.VISIBLE);
                holder.post_descriptionCustom.setVisibility(View.GONE);
            }
        }

        if (postFeed.getUid() == 64){
            holder.post_header.setVisibility(View.GONE);
            holder.post_description.setVisibility(View.GONE);
            holder.post_descriptionBelowImage.setVisibility(View.VISIBLE);
        }else{
            holder.post_header.setVisibility(View.VISIBLE);
            holder.post_description.setVisibility(View.VISIBLE);
            holder.post_descriptionBelowImage.setVisibility(View.GONE);
        }

        holder.post_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isOnline()) {
                if (!postFeed.getLink().isEmpty()) {
                    if (postFeed.getLink().equals("abc")){
                        startDialogForNewImage(postFeed.getPostPicUrl());

                    } else{
                        Intent myIntent = new Intent(myApplication.getApplicationContext(), WebViewActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        myIntent.putExtra("LINK", postFeed.getLink() + "");
                        myIntent.putExtra("TITLE", postFeed.getTitle() + "");
                        myApplication.getApplicationContext().startActivity(myIntent);
                    }
                }

//                } else if (!isOnline())
//                    Toast.makeText(MyApplication.getAppContext(), "No Network", Toast.LENGTH_SHORT).show();
            }
        });


        holder.post_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedPrefManager.isLoggedIn())
                    sharedPrefManager.showAlertDialog(v);
                else {
                    Intent myIntent = new Intent(MyApplication.getAppContext(), UserProfile.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("UID",postFeed.getUid());
                    myIntent.putExtra("NAME",postFeed.getUser_name());
                    myIntent.putExtra("PIC", postFeed.getUser_pic());
                    myIntent.putExtra("STATUS", postFeed.getUser_status());
                    MyApplication.getAppContext().startActivity(myIntent);

                }
            }
        });

        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!sharedPrefManager.isLoggedIn())
                    sharedPrefManager.showAlertDialog(view);
                else {
                    String api_key = sharedPrefManager.getApiKey();
                    Intent myIntent = new Intent(myApplication.getApplicationContext(), Comment_Actiivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("PID_VALUE", postFeed.getPid() + "");
                    myIntent.putExtra("API_KEY", api_key);
                    myIntent.putExtra("FLAG",0);
                    myApplication.getApplicationContext().startActivity(myIntent);
                }
            }
        });

        holder.plus_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!sharedPrefManager.isLoggedIn())
                    sharedPrefManager.showAlertDialog(view);
                else {


                    int count = Integer.parseInt(holder.like_count.getText().toString());
//                    holder.plus_like.setIcon(IconType.Star);
//                    holder.plus_like.setIconSizeDp(40);
//                    holder.plus_like.setLikeDrawableRes(R.drawable.teal_like);
//                    holder.plus_like.setUnlikeDrawableRes(R.drawable.likeplus);
//                    holder.plus_like.setCircleEndColorRes(R.color.colorAccent);
//                    holder.plus_like.setCircleStartColorRes(R.color.colorPrimary);
//                    holder.plus_like.setExplodingDotColorsRes(R.color.colorAccent, R.color.colorPrimary);
//                    holder.plus_like.setAnimationScaleFactor(1);

                    if (button(postFeed.getPid()) == 2) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count - 1));
                        dop.setCommentCount(dop,postFeed.getPid(),count-1);
                        dop.setflagandflagd(dop, 0, 0, postFeed.getPid());
                        up_net(postFeed);
                    } else if (button(postFeed.getPid()) == 1) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(true);
                        holder.like_count.setText(Integer.toString(count + 2));
                        dop.setCommentCount(dop,postFeed.getPid(),count+2);
                        dop.setflagandflagd(dop, 1, 0, postFeed.getPid());
                        up_net(postFeed);
                    } else if (button(postFeed.getPid()) == 0) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(true);
                        holder.like_count.setText(Integer.toString(count + 1));
                        dop.setCommentCount(dop,postFeed.getPid(),count+1);
                        dop.setflagandflagd(dop, 1, 0, postFeed.getPid());
                        up_net(postFeed);
                    } else if (button(postFeed.getPid()) == -1) {
                        if (isOnline()) {
                            holder.plus_like.setLiked(true);
                            up_net_first(postFeed, holder);
                            Log.e("ADARSH", "BUTTON " + Integer.toString(button(postFeed.getPid())));
                            getPids();
                        } else
                            Toast.makeText(MyApplication.getAppContext(), "No Network", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


        holder.minus_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!sharedPrefManager.isLoggedIn())
                    sharedPrefManager.showAlertDialog(view);
                else {
                    int count = Integer.parseInt(holder.like_count.getText().toString());
                    if (button(postFeed.getPid()) == 2) {
                        holder.minus_dislike.setLiked(true);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count - 2));
                        dop.setCommentCount(dop,postFeed.getPid(),count-2);
                        dop.setflagandflagd(dop, 0, 1, postFeed.getPid());
                        down_net(postFeed);
                    } else if (button(postFeed.getPid()) == 1) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count + 1));
                        dop.setflagandflagd(dop, 0, 0, postFeed.getPid());
                        dop.setCommentCount(dop,postFeed.getPid(),count+1);
                        down_net(postFeed);
                    } else if (button(postFeed.getPid()) == 0) {
                        holder.minus_dislike.setLiked(true);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count - 1));
                        dop.setCommentCount(dop,postFeed.getPid(),count-1);
                        dop.setflagandflagd(dop, 0, 1, postFeed.getPid());
                        down_net(postFeed);
                    } else if (button(postFeed.getPid()) == -1) {
                        if (isOnline()) {
                            holder.minus_dislike.setLiked(true);
                            down_net_first(postFeed, holder);
                            Log.e("ADARSH", "BUTTON " + Integer.toString(button(postFeed.getPid())));
                            getPids();
                        } else
                            Toast.makeText(MyApplication.getAppContext(), "No Network", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


        holder.postBodyNoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    if(postFeed.getLink().equals("abc"));

                    else if (!postFeed.getLink().isEmpty()) {
                        Intent myIntent = new Intent(myApplication.getApplicationContext(), WebViewActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        myIntent.putExtra("LINK", postFeed.getLink() + "");
                        myIntent.putExtra("TITLE", postFeed.getTitle() + "");
                        myApplication.getApplicationContext().startActivity(myIntent);
                    }
                } else if (!isOnline())
                    Toast.makeText(MyApplication.getAppContext(), "No Network", Toast.LENGTH_SHORT).show();
            }
        });


        holder.post_body.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteReportDialog(v,postFeed.getUid(),postFeed.getPid(),postFeed);
                return true;
            }
        });
        holder.postBodyNoImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteReportDialog(view,postFeed.getUid(),postFeed.getPid(),postFeed);
                return true;
            }
        });



    }




    public void startDialogForNewImage(String image) {

        MyTextDrawable myTextDrawable = new MyTextDrawable();
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogMainView = factory.inflate(R.layout.fragment_image_post, null);

        final AlertDialog myDialog = new AlertDialog.Builder(context).create();

        ImageView mImageView = (ImageView) dialogMainView.findViewById(R.id.myImagePostContainer);

        myDialog.setView(dialogMainView);
        if (!image.isEmpty())
            Glide.with(context)
                    .load(image)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading)
                    .dontAnimate()
                    .error(myTextDrawable.setTextDrawableForError("Error!"))
                    .into(mImageView);

        myDialog.show();

    }
    public void down_net(final PostFeed postFeed) {
        String up_Count_url = Utility.getIPADDRESS() + "likes";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, up_Count_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("upordown", 1 + "");
                params.put("pid", postFeed.getPid() + "");
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void down_net_first(final PostFeed postFeed, final ViewHolder1 holder) {
        String up_Count_url = Utility.getIPADDRESS() + "likes";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, up_Count_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int count = Integer.parseInt(holder.like_count.getText().toString());
                //holder.minus_dislike.setLiked(true);
                holder.plus_like.setLiked(false);
                holder.like_count.setText(Integer.toString(count - 1));
                dop.setCommentCount(dop,postFeed.getPid(),count-1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("upordown", 1 + "");
                params.put("pid", postFeed.getPid() + "");
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void up_net(final PostFeed postFeed) {
        String up_Count_url = Utility.getIPADDRESS() + "likes";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, up_Count_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("upordown", 0 + "");
                params.put("pid", postFeed.getPid() + "");
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void up_net_first(final PostFeed postFeed, final ViewHolder1 holder) {
        String up_Count_url = Utility.getIPADDRESS() + "likes";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, up_Count_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int count = Integer.parseInt(holder.like_count.getText().toString());
                //holder.plus_like.setLiked(true);
                holder.minus_dislike.setLiked(false);
                holder.like_count.setText(Integer.toString(count + 1));
                dop.setCommentCount(dop,postFeed.getPid(),count+1);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("upordown", 0 + "");
                params.put("pid", postFeed.getPid() + "");
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    public int button(int pid) {
        return dop.getfbypid(dop, pid);
    }



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showDeleteReportDialog(final View view, int uid, final int pid, final PostFeed postFeed) {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.dialog_delete_report);
        arrayAdapter.add("Share");
        if(sharedPrefManager.getUser_id()==uid)
            arrayAdapter.add("Delete");
        DatabaseOperations dop = new DatabaseOperations(MyApplication.getAppContext());
        if(dop.reported(dop,pid)==0)
            arrayAdapter.add("Report");
        else
            arrayAdapter.add("Already reported");


        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if(strName.equals("Delete")) {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Delete post")
                                    .setMessage("Are you sure you want to delete this post?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            deletePost(pid);
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .show();
                        }
                        else if(strName.equals("Already reported"))
                            Toast.makeText(context,"You have already reported this post",Toast.LENGTH_LONG).show();
                        else if(strName.equals("Report"))
                            reportPost(pid);
                        else if(strName.equals("Share")){
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            String shareString =  "Title:"+postFeed.getTitle()
                                    +"\n\nDescription:"+postFeed.getDescription();
                            if(postFeed.getLink()!=null)
                                if(!postFeed.getLink().isEmpty())
                                    if(!postFeed.getLink().equals("abc"))
                                        shareString=shareString+"\n\nLink:"+postFeed.getLink();
                            shareString=shareString+"\n\nThank you - shared via FOCUS App, download now @link ";
                            sendIntent.putExtra(Intent.EXTRA_TEXT,shareString);
                            sendIntent.setType("text/plain");
                            context.startActivity(Intent.createChooser(sendIntent, "Share via..."));
                        }
                    }
                });
        builderSingle.show();

    }

    public void reportPost(final int pid){
        String URL = Utility.getIPADDRESS()+"reportPost";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        dop.addReportStatus(dop,sharedPrefManager.getUser_id(),pid);
                        Toast.makeText(context,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        if(jsonObject.getBoolean("deletepost")){
                            dop.deletePostbyPid(dop,pid);
                            ArrayList<PostFeed> postfeeds  = dop.readPostData(dop);
                            for(int i = 0; i<postfeeds.size(); i++)
                                feedsArrayList.add(new Feeds(postfeeds.get(i), null, null));
                            notifyDataSetChanged();
                        }
                    }
                    else{
                        Toast.makeText(context,"Report request could not be registered",Toast.LENGTH_SHORT).show();
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong. Try after sometime",Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pid", Integer.toString(pid));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void deletePost(final int pid){
        String URL = Utility.getIPADDRESS()+"deletePost";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        dop.deletePostbyPid(dop, pid);
                        ArrayList<PostFeed> postfeeds  = dop.readPostData(dop);
                        for(int i = 0; i<postfeeds.size(); i++)
                            feedsArrayList.add(new Feeds(postfeeds.get(i), null, null));
                        notifyDataSetChanged();
                        Toast.makeText(context,"Post Deleted",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context,"Post could not be deleted",Toast.LENGTH_SHORT).show();
                    }

                }catch(JSONException e){
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
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pid", Integer.toString(pid));
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }



    public void getPids() {
        String URL = Utility.getIPADDRESS() + "getlikebyapi";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                ArrayList<Likes> l = new ArrayList<>();
                try {
                    jsonObject = new JSONObject(response);
                    l = parseLike(jsonObject);
                    dop.insertLikes(dop, l, true);

                } catch (JSONException e) {
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
                String api_key = sharedPrefManager.getApiKey();
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", api_key);
                return params;
            }


        };
        requestQueue.add(stringRequest);
    }

    private ArrayList<Likes> parseLike(JSONObject jsonObject) {
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
                Log.e("ADARSH", e.toString());
                e.printStackTrace();
            }


        }
        return likes;
    }


    private void configureViewHolder2(final ViewHolder2 holder, int position) {

        final SessionFeed sessionFeed = feedsArrayList.get(position).getSessionFeed();
        final MyTextDrawable myTextDrawable = new MyTextDrawable();
        holder.title_session.setText(sessionFeed.getSession_title());
        holder.description.setText(sessionFeed.getSession_description());
        if (!sessionFeed.getSession_image().isEmpty()) {

            Glide.with(MyApplication.getAppContext())
                    .load(sessionFeed.getSession_image())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading)
                    .error(myTextDrawable.setTextDrawableForError("Error!"))
                    .crossFade()
                    .into(holder.imageView);

        } else {
            holder.imageView.setImageDrawable(myTextDrawable.setTextDrawableForPost(sessionFeed.getSession_title(), "No Image!"));
            // default
        }
        holder.post_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyApplication.getAppContext(),session_details.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id",sessionFeed.getSession_id());
                i.putExtra("title",sessionFeed.getSession_title());
                i.putExtra("desc",sessionFeed.getSession_description());
                i.putExtra("picurl",sessionFeed.getSession_image());
                i.putExtra("venue",sessionFeed.getS_venue());
                i.putExtra("coord",sessionFeed.getS_coordinator());
                i.putExtra("email",sessionFeed.getS_c_email());
                i.putExtra("phone",sessionFeed.getS_c_phone());
                i.putExtra("rp",sessionFeed.getResource_person());
                i.putExtra("rpd",sessionFeed.getRp_desg());
                i.putExtra("addr",sessionFeed.getAddress());
                i.putExtra("date_time",sessionFeed.getTime_and_date());
                i.putExtra("room",sessionFeed.getRoom());
                MyApplication.getAppContext().startActivity(i);

            }
        });

        holder.user_name.setText(sessionFeed.getUser_name()+"");
        Date gotDate = sessionFeed.getDosp();
        String formatedDate = dateFormat.format(gotDate);
        holder.date.setText(formatedDate+"");
        String avatar = sessionFeed.getUser_pic();

        if (!avatar.isEmpty()){

            Glide.with(MyApplication.getAppContext())
                    .load(avatar)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.blank_person_final)
                    .error(myTextDrawable.setTextDrawable(sessionFeed.getUser_name()))
                    .into(holder.circleImageView);

        }else {
            // default
            holder.circleImageView.setImageDrawable(myTextDrawable.setTextDrawable(sessionFeed.getUser_name()));
        }

        holder.post_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPrefManager.isLoggedIn()){
                    Intent myIntent = new Intent(MyApplication.getAppContext(), UserProfile.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("UID", sessionFeed.getUid());
                    myIntent.putExtra("NAME",sessionFeed.getUser_name());
                    myIntent.putExtra("PIC", sessionFeed.getUser_pic());
                    myIntent.putExtra("STATUS", sessionFeed.getUser_status());
                    MyApplication.getAppContext().startActivity(myIntent);
                }

                else
                    sharedPrefManager.showAlertDialog(v);
            }
        });
        holder.session_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sharedPrefManager.isLoggedIn())
                    sharedPrefManager.showAlertDialog(view);
                else {
                    String api_key = sharedPrefManager.getApiKey();
                    Intent myIntent = new Intent(myApplication.getApplicationContext(), Comment_Actiivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("PID_VALUE", sessionFeed.getSession_id() + "");
                    myIntent.putExtra("API_KEY", api_key);
                    myIntent.putExtra("FLAG",1);
                    myApplication.getApplicationContext().startActivity(myIntent);
                }
            }
        });
        holder.session_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello Folks...\nBelow are the details of yet another GLUG session" +
                        "\n\nSession:"+sessionFeed.getSession_title()+
                        "\n\nDescription:"+sessionFeed.getSession_description()+
                        "\n\nVenue:"+sessionFeed.getS_venue()+
                        "\n\nCoordinator:"+sessionFeed.getS_coordinator()+
                        "\n\nCoordinator's Email:"+sessionFeed.getS_c_email()+
                        "\n\nCoordinator's Phone:"+sessionFeed.getS_c_phone()+
                        "\n\nResource Person:"+sessionFeed.getResource_person()+
                        "\n\nDesignation:"+sessionFeed.getRp_desg()+
                        "\n\nTime:"+sessionFeed.getTime_and_date()+
                        "\n\nAddress:"+sessionFeed.getAddress()+
                        "\n\nRoom:"+sessionFeed.getRoom()+
                        "\n\nThank you - shared via FOCUS App, download now @link ");
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, "Share via..."));



            }
        });

    }


    @Override
    public int getItemCount() {
        return feedsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (feedsArrayList.get(position).isPostFeed())
            return POSTFEED;
        else if (feedsArrayList.get(position).isSessionFeed())
            return SESSIONFEED;
        else if (feedsArrayList.get(position).isRecyclerHeader())
            return HEADER;
        else return -1;

    }
}
