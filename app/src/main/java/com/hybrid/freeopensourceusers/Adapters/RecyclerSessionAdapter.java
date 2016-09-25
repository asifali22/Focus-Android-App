package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.Activities.session_details;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Utility.RecyclerViewAnimation;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by adarsh on 6/9/16.
 */

public class RecyclerSessionAdapter extends RecyclerView.Adapter<RecyclerSessionAdapter.ViewholderSessionFeed> {

    public ArrayList<SessionFeed> sessiofeedArrayList = new ArrayList<>();
    public LayoutInflater layoutInflater = null;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public MyApplication myApplication;
  //  public ImageLoader imageLoader;
    public Context context;
    public DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private int previousPosition = 0;
    private ClickCallback clickCallback;

    public RecyclerSessionAdapter(Context context){
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
       // imageLoader = volleySingleton.getImageLoader();
        this.context=context;
        setCallback(clickCallback);
    }
    public void setFeed(ArrayList<SessionFeed> newsFeedArrayList) {
        this.sessiofeedArrayList = newsFeedArrayList;
        notifyDataSetChanged();
    }

    @Override
    public ViewholderSessionFeed onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.session_row_layout, parent, false);
        ViewholderSessionFeed viewholderPostFeed = new ViewholderSessionFeed(view);

        return viewholderPostFeed;
    }

    @Override
    public void onBindViewHolder(final ViewholderSessionFeed holder, int position) {
        if (position > previousPosition)
            RecyclerViewAnimation.animateRecyclerView(holder, true);
        else
            RecyclerViewAnimation.animateRecyclerView(holder, false);
        previousPosition = position;
        final SessionFeed sessionFeed = sessiofeedArrayList.get(position);
        final MyTextDrawable myTextDrawable = new MyTextDrawable();
        holder.title_session.setText(sessionFeed.getSession_title());
        holder.description.setText(sessionFeed.getSession_description());
        if (!sessionFeed.getSession_image().isEmpty()) {

            Glide.with(MyApplication.getAppContext())
                    .load(sessionFeed.getSession_image())
                    .centerCrop()
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
                clickCallback.openSessionDetails(sessionFeed, holder);

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
                clickCallback.openProfile(sessionFeed, holder);
            }
        });


    }

    public void setCallback(ClickCallback callback) {
        this.clickCallback = callback;
    }

    public interface ClickCallback {
        void openProfile(SessionFeed sessionFeed, ViewholderSessionFeed viewHolder);
        void openSessionDetails(SessionFeed sessionFeed, ViewholderSessionFeed viewHolder);
    }

    @Override
    public int getItemCount() {
        return sessiofeedArrayList.size();
    }

    public static class ViewholderSessionFeed extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title_session;
        public TextView description;
        public CardView cardView;
        public RelativeLayout post_header;
        public LinearLayout post_body;
        public TextView user_name;
        public TextView date;
        public CircleImageView circleImageView;
        public ViewholderSessionFeed(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view_session);
            imageView = (ImageView) view.findViewById(R.id.session_image);
            title_session = (TextView) view.findViewById(R.id.session_title);
            description = (TextView) view.findViewById(R.id.session_description);
            user_name = (TextView) view.findViewById(R.id.user_name_session);
            date = (TextView) view.findViewById(R.id.time_session);
            circleImageView = (CircleImageView) view.findViewById(R.id.user_profile_image_session);
            post_body = (LinearLayout) view.findViewById(R.id.post_body_session);
            post_header = (RelativeLayout) view.findViewById(R.id.post_header_session);
        }
    }


}
