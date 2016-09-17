package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;

import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hybrid.freeopensourceusers.Activities.Comment_Actiivity;
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.Activities.WebViewActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.Likes;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfile;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.RecyclerViewAnimation;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;
import com.like.LikeButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerTrendingAdapter extends RecyclerView.Adapter<RecyclerTrendingAdapter.ViewholderPostFeed> {


    private ArrayList<PostFeed> newsFeedArrayList = new ArrayList<>();
    private LayoutInflater layoutInflater = null;
    private DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private MyApplication myApplication;
//    public ImageLoader imageLoader;
    private DatabaseOperations dop;
    private int previousPosition = 0;
    private ClickCallback clickCallback;

    public RecyclerTrendingAdapter(Context context, ArrayList<PostFeed> newsFeedArrayList) {
        // Below is for getting application context
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

        if (position > previousPosition)
            RecyclerViewAnimation.animateRecyclerView(holder, true);
        else
            RecyclerViewAnimation.animateRecyclerView(holder, false);
        previousPosition = position;
//        //Animation
//        int lastPosition = -1;
//        Animation animation = AnimationUtils.loadAnimation(myApplication.getApplicationContext(),
//                (position > lastPosition) ? R.anim.up_from_bottom
//                        : R.anim.down_from_top);
//        holder.itemView.startAnimation(animation);
//        lastPosition = position;
        //For textDrawable image
        final MyTextDrawable myTextDrawable = new MyTextDrawable();

        final PostFeed postFeed = newsFeedArrayList.get(position);
        holder.user_name.setText(postFeed.getUser_name());
        Date gotDate = postFeed.getDop();
        String formatedDate = dateFormat.format(gotDate);
        holder.user_share_time.setText(formatedDate);
        holder.post_title.setText(postFeed.getTitle());
        if(postFeed.getDescription()!=null)
        holder.post_description.setText(postFeed.getDescription());
        else
            holder.post_description.setText(postFeed.getTitle());
        holder.like_count.setText(postFeed.getUp() + "");
        holder.comment_count.setText(postFeed.getComment_count() + "");
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

//            imageLoader.get(avatar, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    holder.circleImageView.setImageBitmap(response.getBitmap());
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    holder.circleImageView.setImageResource(R.drawable.blank_person_final);
//                }
//            });


            Glide.with(MyApplication.getAppContext())
                    .load(avatar)
                    .fitCenter()
                    .dontAnimate()
                    .placeholder(R.drawable.blank_person_final)
                    .error(myTextDrawable.setTextDrawable(postFeed.getUser_name()))
                    .into(holder.circleImageView);

        } else {
            // default


               holder.circleImageView.setImageDrawable(myTextDrawable.setTextDrawable(postFeed.getUser_name()));

        }
        if (!postpic.isEmpty()) {

//            imageLoader.get(postpic, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    holder.post_pic.setVisibility(View.VISIBLE);
//                    holder.post_pic.setImageBitmap(response.getBitmap());
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
////                    holder.post_pic.setVisibility(View.GONE);
//                    holder.post_pic.setImageDrawable(myTextDrawable.setTextDrawableForError("Error!"));
//                }
//            });

            Glide.with(MyApplication.getAppContext())
                    .load(postpic)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(myTextDrawable.setTextDrawableForError("Error!"))
                    .crossFade()
                    .into(holder.post_pic);



        } else {
//            holder.post_pic.setVisibility(View.GONE);
//             default
            holder.post_pic.setImageDrawable(myTextDrawable.setTextDrawableForPost(postFeed.getTitle(), "No Image!"));
        }

        holder.post_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoggedIn())
                    showAlertDialog(view);
            }
        });

        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!isLoggedIn())
                    showAlertDialog(view);
                else {
                    String api_key = getApiKey();
                    Intent myIntent = new Intent(myApplication.getApplicationContext(), Comment_Actiivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("PID_VALUE", postFeed.getPid() + "");
                    myIntent.putExtra("API_KEY", api_key);
                    myApplication.getApplicationContext().startActivity(myIntent);
                }
            }
        });

        holder.plus_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!isLoggedIn())
                    showAlertDialog(view);
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
                        dop.setflagandflagd(dop, 0, 0, postFeed.getPid());
                        up_net(postFeed);
                    } else if (button(postFeed.getPid()) == 1) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(true);
                        holder.like_count.setText(Integer.toString(count + 2));
                        dop.setflagandflagd(dop, 1, 0, postFeed.getPid());
                        up_net(postFeed);
                    } else if (button(postFeed.getPid()) == 0) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(true);
                        holder.like_count.setText(Integer.toString(count + 1));
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
                if (!isLoggedIn())
                    showAlertDialog(view);
                else {
                    int count = Integer.parseInt(holder.like_count.getText().toString());
                    if (button(postFeed.getPid()) == 2) {
                        holder.minus_dislike.setLiked(true);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count - 2));
                        dop.setflagandflagd(dop, 0, 1, postFeed.getPid());
                        down_net(postFeed);
                    } else if (button(postFeed.getPid()) == 1) {
                        holder.minus_dislike.setLiked(false);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count + 1));
                        dop.setflagandflagd(dop, 0, 0, postFeed.getPid());
                        down_net(postFeed);
                    } else if (button(postFeed.getPid()) == 0) {
                        holder.minus_dislike.setLiked(true);
                        holder.plus_like.setLiked(false);
                        holder.like_count.setText(Integer.toString(count - 1));
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
        holder.post_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( isOnline()) {
                    Intent myIntent = new Intent(myApplication.getApplicationContext(), WebViewActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("LINK", postFeed.getLink() + "");
                    myIntent.putExtra("TITLE", postFeed.getTitle() + "");
                    myApplication.getApplicationContext().startActivity(myIntent);
                } else if (!isOnline())
                    Toast.makeText(MyApplication.getAppContext(), "No Network", Toast.LENGTH_SHORT).show();
            }
        });

        holder.post_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoggedIn())
                    showAlertDialog(v);
                else {
                    clickCallback.openProfile(postFeed, holder);

                }
            }
        });

    }

    public void setCallback(ClickCallback callback) {
        this.clickCallback = callback;
    }

    public interface ClickCallback {
        void openProfile(PostFeed postFeed,ViewholderPostFeed viewHolder);
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
                params.put("Authorization", getApiKey());
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", getApiKey());
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
                params.put("Authorization", getApiKey());
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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", getApiKey());
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
        public TextView user_name, user_share_time, post_title, post_description, like_count, comment_count;
        public ImageView post_pic, comment_button;
        public RelativeLayout post_header;
        public LinearLayout post_body;
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

        }



    }


//    @Override
//    public void onViewDetachedFromWindow(ViewholderPostFeed holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.itemView.clearAnimation();
//    }


    public void showAlertDialog(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Sign up?")
                .setMessage("Join us to explore more!")
                .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(myApplication.getApplicationContext(), LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        myApplication.getApplicationContext().startActivity(myIntent);
                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public String getApiKey() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String api_key = sharedPreferences.getString("api_key", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean("logged_in", false);
        return status;

    }

    // For Search

    public void addAll(Collection<PostFeed> items) {
        int currentItemCount = newsFeedArrayList.size();
        newsFeedArrayList.addAll(items);
        notifyItemRangeInserted(currentItemCount, items.size());
    }

    public void addAll(int position, Collection<PostFeed> items) {
        int currentItemCount = newsFeedArrayList.size();
        if(position > currentItemCount)
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
        if(cleanToReplace) {
            clear();
            addAll(items);
        } else {
            int oldCount = newsFeedArrayList.size();
            int newCount = items.size();
            int delCount = oldCount - newCount;
            newsFeedArrayList.clear();
            newsFeedArrayList.addAll(items);
            if(delCount > 0) {
                notifyItemRangeChanged(0, newCount);
                notifyItemRangeRemoved(newCount, delCount);
            } else if(delCount < 0) {
                notifyItemRangeChanged(0, oldCount);
                notifyItemRangeInserted(oldCount, - delCount);
            } else {
                notifyItemRangeChanged(0, newCount);
            }
        }
    }

    public void getPids() {
        String URL =Utility.getIPADDRESS() + "getlikebyapi";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                ArrayList<Likes> l = new ArrayList<>();
                try{
                    jsonObject = new JSONObject(response);
                    l = parseLike(jsonObject);
                    dop.insertLikes(dop,l,true);

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
                SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                String api_key = sharedPreferences.getString("api_key", null);
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",api_key);
                return params;
            }


        };
        requestQueue.add(stringRequest);
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



}

