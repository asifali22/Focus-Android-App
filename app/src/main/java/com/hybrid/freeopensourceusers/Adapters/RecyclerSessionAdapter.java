package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hybrid.freeopensourceusers.Activities.Comment_Actiivity;
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
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
    private SharedPrefManager sharedPrefManager;
  //  public ImageLoader imageLoader;
    public Context context;
    public DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private ClickCallback clickCallback;

    public RecyclerSessionAdapter(Context context){
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
       // imageLoader = volleySingleton.getImageLoader();
        this.context=context;
        sharedPrefManager = new SharedPrefManager(context);
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

        final SessionFeed sessionFeed = sessiofeedArrayList.get(position);
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
                if (sharedPrefManager.isLoggedIn())
                clickCallback.openProfile(sessionFeed, holder);
                else
                    showAlertDialog(v);
            }
        });
        holder.session_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sharedPrefManager.isLoggedIn())
                    showAlertDialog(view);
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
        public ImageView session_comment;
        public ImageView session_share;
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
            session_comment = (ImageView) view.findViewById(R.id.comment_button_session);
            session_share = (ImageView) view.findViewById(R.id.session_share);
        }
    }


}
