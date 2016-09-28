package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.Feeds;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SearchStuffs.ViewHolder1;
import com.hybrid.freeopensourceusers.SearchStuffs.ViewHolder2;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private RecyclerTrendingAdapter.ClickCallback clickCallback;
    private final int POSTFEED = 0, SESSIONFEED = 1;


    public ComplexRecyclerViewAdapter(Context context, ArrayList<Feeds> feedsArrayList) {

        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.feedsArrayList = feedsArrayList;
//        setCallback(clickCallback);
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
            case SESSIONFEED:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, position);
                break;
        }
    }


    private void configureViewHolder1(ViewHolder1 vh1, int position) {

        final MyTextDrawable myTextDrawable = new MyTextDrawable();

        final PostFeed postFeed = feedsArrayList.get(position).getPostFeed();
        vh1.user_name.setText(postFeed.getUser_name());
        Date gotDate = postFeed.getDop();
        String formatedDate = dateFormat.format(gotDate);
        vh1.user_share_time.setText(formatedDate);
        vh1.post_title.setText(postFeed.getTitle());
        if(postFeed.getDescription()!=null)
            vh1.post_description.setText(postFeed.getDescription());
        else
            vh1.post_description.setText(postFeed.getTitle());
        vh1.like_count.setText(postFeed.getUp() + "");
        vh1.comment_count.setText(postFeed.getComment_count() + "");
        final String avatar = postFeed.getUser_pic();
        final String postpic = postFeed.getPostPicUrl();
        if (!avatar.isEmpty()) {
            Glide.with(MyApplication.getAppContext())
                    .load(avatar)
                    .fitCenter()
                    .dontAnimate()
                    .placeholder(R.drawable.blank_person_final)
                    .error(myTextDrawable.setTextDrawable(postFeed.getUser_name()))
                    .into(vh1.circleImageView);
        }
     else {
        // default

        vh1.circleImageView.setImageDrawable(myTextDrawable.setTextDrawable(postFeed.getUser_name()));

    }
    if (!postpic.isEmpty()) {


        Glide.with(MyApplication.getAppContext())
                .load(postpic)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(myTextDrawable.setTextDrawableForError("Error!"))
                .crossFade()
                .into(vh1.post_pic);



    } else {

        vh1.post_pic.setImageDrawable(myTextDrawable.setTextDrawableForPost(postFeed.getTitle(), "No Image!"));
    }

    }


    private void configureViewHolder2(ViewHolder2 vh2, int position) {

        final SessionFeed sessionFeed = feedsArrayList.get(position).getSessionFeed();
        final MyTextDrawable myTextDrawable = new MyTextDrawable();
        vh2.title_session.setText(sessionFeed.getSession_title());
        vh2.description.setText(sessionFeed.getSession_description());
        if (!sessionFeed.getSession_image().isEmpty()) {

            Glide.with(MyApplication.getAppContext())
                    .load(sessionFeed.getSession_image())
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(myTextDrawable.setTextDrawableForError("Error!"))
                    .crossFade()
                    .into(vh2.imageView);

        } else {
            vh2.imageView.setImageDrawable(myTextDrawable.setTextDrawableForPost(sessionFeed.getSession_title(), "No Image!"));
            // default
        }

        vh2.user_name.setText(sessionFeed.getUser_name()+"");
        Date gotDate = sessionFeed.getDosp();
        String formatedDate = dateFormat.format(gotDate);
        vh2.date.setText(formatedDate+"");
        String avatar = sessionFeed.getUser_pic();

        if (!avatar.isEmpty()){

            Glide.with(MyApplication.getAppContext())
                    .load(avatar)
                    .fitCenter()
                    .dontAnimate()
                    .placeholder(R.drawable.blank_person_final)
                    .error(myTextDrawable.setTextDrawable(sessionFeed.getUser_name()))
                    .into(vh2.circleImageView);

        }else {
            // default
            vh2.circleImageView.setImageDrawable(myTextDrawable.setTextDrawable(sessionFeed.getUser_name()));
        }

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
        else return -1;

    }
}
