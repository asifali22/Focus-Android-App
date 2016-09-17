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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.Activities.session_details;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
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
    public ImageLoader imageLoader;
    public Context context;
    public DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private int previousPosition = 0;

    public RecyclerSessionAdapter(Context context){
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();
        this.context=context;
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
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,session_details.class);
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
                Log.e("ADARSH",Integer.toString(sessionFeed.getSession_id())+" "+sessionFeed.getSession_title()+
                " "+sessionFeed.getSession_description()+" "+sessionFeed.getSession_image()+" "+ sessionFeed.getResource_person()+
                " "+sessionFeed.getRoom());
                context.startActivity(i);
            }
        });
        holder.title_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,session_details.class);
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

                context.startActivity(i);

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
    }

    @Override
    public int getItemCount() {
        return sessiofeedArrayList.size();
    }

    static class ViewholderSessionFeed extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title_session;
        public TextView description;
        public CardView cardView;
        public TextView user_name;
        public TextView date;
        CircleImageView circleImageView;
        public ViewholderSessionFeed(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view_session);
            imageView = (ImageView) view.findViewById(R.id.session_image);
            title_session = (TextView) view.findViewById(R.id.session_title);
            description = (TextView) view.findViewById(R.id.session_description);
            user_name = (TextView) view.findViewById(R.id.user_name_session);
            date = (TextView) view.findViewById(R.id.time_session);
            circleImageView = (CircleImageView) view.findViewById(R.id.user_profile_image_session);
        }
    }


}
