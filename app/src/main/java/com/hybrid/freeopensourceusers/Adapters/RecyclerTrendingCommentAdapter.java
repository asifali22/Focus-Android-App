package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.CommentFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by monster on 30/7/16.
 */

public class RecyclerTrendingCommentAdapter extends RecyclerView.Adapter<RecyclerTrendingCommentAdapter.ViewholderCommentsFeed>{

    private ArrayList<CommentFeed> commentsFeedArrayList = new ArrayList<>();
    private LayoutInflater layoutInflater = null;
    private DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private MyApplication myApplication;
//    private ImageLoader imageLoader;

    public RecyclerTrendingCommentAdapter(Context context) {
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
//        imageLoader = volleySingleton.getImageLoader();
    }

    public void setFeed(ArrayList<CommentFeed> commentsFeedArrayList) {
        this.commentsFeedArrayList = commentsFeedArrayList;
        notifyDataSetChanged();
    }
    public void setNewCommentFeed(ArrayList<CommentFeed> commentsFeedArrayList) {
        this.commentsFeedArrayList = commentsFeedArrayList;
       notifyItemInserted(0);
        notifyDataSetChanged();
    }



    @Override
    public ViewholderCommentsFeed onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.trending_comment_row_layout, parent, false);

        ViewholderCommentsFeed viewholderCommentsFeed = new ViewholderCommentsFeed(view);

        return viewholderCommentsFeed;
    }

    @Override
    public void onBindViewHolder(final ViewholderCommentsFeed holder, int position) {

        final MyTextDrawable myTextDrawable = new MyTextDrawable();
        final CommentFeed commentFeed = commentsFeedArrayList.get(position);
        holder.name.setText(commentFeed.getUser_name()+"");
        holder.comment.setText(commentFeed.getComment()+"");
        Date gotDate = commentFeed.getDoc();
        String formatedDate = dateFormat.format(gotDate);
        holder.timeOfComment.setText(formatedDate);
        String userImage = commentFeed.getUser_pic();
        if(!userImage.isEmpty()){

//            imageLoader.get(userImage, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    holder.avatar.setImageBitmap(response.getBitmap());
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    holder.avatar.setImageResource(R.drawable.ic_social_person_grey);
//                }
//            });

            Glide.with(MyApplication.getAppContext())
                    .load(userImage)
                    .fitCenter()
                    .dontAnimate()
                    .placeholder(R.drawable.blank_person_final)
                    .error(myTextDrawable.setTextDrawable(commentFeed.getUser_name()))
                    .into(holder.avatar);

        } else {
            // default

            holder.avatar.setImageDrawable(myTextDrawable.setTextDrawable(commentFeed.getUser_name()));
        }

    }

    @Override
    public int getItemCount() {
        return commentsFeedArrayList.size();
    }

    static class ViewholderCommentsFeed extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView name, timeOfComment, comment;

        public ViewholderCommentsFeed(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.miniProfile);
            name = (TextView) itemView.findViewById(R.id.name_user);
            timeOfComment = (TextView) itemView.findViewById(R.id.time_of_comment);
            comment = (TextView) itemView.findViewById(R.id.comment_text);
        }
    }

}

