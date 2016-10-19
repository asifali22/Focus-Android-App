package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.Activities.WebViewActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.Likes;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;
import com.like.LikeButton;
import com.like.OnLikeListener;


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

import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerTrendingAdapter extends RecyclerView.Adapter<RecyclerTrendingAdapter.ViewholderPostFeed> {


    private ArrayList<PostFeed> newsFeedArrayList = new ArrayList<>();
    private LayoutInflater layoutInflater = null;
    private DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private MyApplication myApplication;
    private SharedPrefManager sharedPrefManager;
    //    public ImageLoader imageLoader;
    private DatabaseOperations dop;
    private ClickCallback clickCallback;
    private Context context;

    public RecyclerTrendingAdapter(Context context, ArrayList<PostFeed> newsFeedArrayList) {
        // Below is for getting application context
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
//        imageLoader = volleySingleton.getImageLoader();
        this.newsFeedArrayList = newsFeedArrayList;
        setCallback(clickCallback);

    }

    public void setFeed(ArrayList<PostFeed> newsFeedArrayList) {
        this.newsFeedArrayList = newsFeedArrayList;
        notifyDataSetChanged();
    }


    @Override
    public ViewholderPostFeed onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.trending_row_layout, parent, false);
        ViewholderPostFeed viewholderPostFeed = new ViewholderPostFeed(view);
        dop = new DatabaseOperations(MyApplication.getAppContext());
        return viewholderPostFeed;
    }

    @Override
    public void onBindViewHolder(final ViewholderPostFeed holder, final int position) {


        final MyTextDrawable myTextDrawable = new MyTextDrawable();

        final PostFeed postFeed = newsFeedArrayList.get(position);

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
                            clickCallback.startDialogForNewImage(postFeed.getPostPicUrl());

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



        holder.post_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedPrefManager.isLoggedIn())
                    sharedPrefManager.showAlertDialog(v);
                else {
                    clickCallback.openProfile(postFeed, holder);

                }
            }
        });

        holder.post_body.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteReportDialog(v,postFeed.getUid(),postFeed.getPid());
                return true;
            }
        });
        holder.postBodyNoImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteReportDialog(view,postFeed.getUid(),postFeed.getPid());
                return true;
            }
        });

    }

    public void setCallback(ClickCallback callback) {
        this.clickCallback = callback;
    }

    public interface ClickCallback {
        void openProfile(PostFeed postFeed, ViewholderPostFeed viewHolder);

        void startDialogForNewImage(String image);

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

    public void down_net_first(final PostFeed postFeed, final ViewholderPostFeed holder) {
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

    public void up_net_first(final PostFeed postFeed, final ViewholderPostFeed holder) {
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

    @Override
    public int getItemCount() {
        return newsFeedArrayList.size();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static class ViewholderPostFeed extends RecyclerView.ViewHolder {

        public CircleImageView circleImageView;
        public TextView user_name, user_share_time,post_descriptionCustom, post_title, post_description, like_count, comment_count, postTitleNoImage, postDescriptionNOImage,post_descriptionBelowImage;
        public ImageView post_pic, comment_button;
        public RelativeLayout post_header;
        public LinearLayout post_body, postBodyNoImage;
        public LikeButton plus_like, minus_dislike;


        public ViewholderPostFeed(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_share_time = (TextView) itemView.findViewById(R.id.user_share_time);
            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_description = (TextView) itemView.findViewById(R.id.post_description);
            like_count = (TextView) itemView.findViewById(R.id.like_count);
            comment_count = (TextView) itemView.findViewById(R.id.comment_count);
            post_pic = (ImageView) itemView.findViewById(R.id.post_pic);
            plus_like = (LikeButton) itemView.findViewById(R.id.plus_like);
            minus_dislike = (LikeButton) itemView.findViewById(R.id.minus_dislike);
            comment_button = (ImageView) itemView.findViewById(R.id.comment_button);
            post_header = (RelativeLayout) itemView.findViewById(R.id.post_header);
            post_body = (LinearLayout) itemView.findViewById(R.id.post_body);
            // changes made
            postTitleNoImage = (TextView) itemView.findViewById(R.id.post_titleForNoImage);
            postDescriptionNOImage = (TextView) itemView.findViewById(R.id.post_descriptionForNoImage);
            postBodyNoImage = (LinearLayout) itemView.findViewById(R.id.post_bodyForNoImage);
            post_descriptionCustom = (TextView) itemView.findViewById(R.id.post_descriptionCustom);
            post_descriptionBelowImage = (TextView) itemView.findViewById(R.id.post_descriptionBelowImage);
        }


    }


//    @Override
//    public void onViewDetachedFromWindow(ViewholderPostFeed holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.itemView.clearAnimation();
//    }



    public void showDeleteReportDialog(final View view,int uid,final int pid) {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.dialog_delete_report);
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
                                newsFeedArrayList=dop.readPostData(dop);
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
                            newsFeedArrayList = dop.readPostData(dop);
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





    // For Search

    public void addAll(Collection<PostFeed> items) {
        int currentItemCount = newsFeedArrayList.size();
        newsFeedArrayList.addAll(items);
        notifyItemRangeInserted(currentItemCount, items.size());
    }

    public void addAll(int position, Collection<PostFeed> items) {
        int currentItemCount = newsFeedArrayList.size();
        if (position > currentItemCount)
            throw new IndexOutOfBoundsException();
        else
            newsFeedArrayList.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public void replaceWith(Collection<PostFeed> items) {
        replaceWith(items, false);
    }

    public void clear() {
        int itemCount = newsFeedArrayList.size();
        newsFeedArrayList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }


    public void replaceWith(Collection<PostFeed> items, boolean cleanToReplace) {
        if (cleanToReplace) {
            clear();
            addAll(items);
        } else {
            int oldCount = newsFeedArrayList.size();
            int newCount = items.size();
            int delCount = oldCount - newCount;
            newsFeedArrayList.clear();
            newsFeedArrayList.addAll(items);
            if (delCount > 0) {
                notifyItemRangeChanged(0, newCount);
                notifyItemRangeRemoved(newCount, delCount);
            } else if (delCount < 0) {
                notifyItemRangeChanged(0, oldCount);
                notifyItemRangeInserted(oldCount, -delCount);
            } else {
                notifyItemRangeChanged(0, newCount);
            }
        }
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



}

