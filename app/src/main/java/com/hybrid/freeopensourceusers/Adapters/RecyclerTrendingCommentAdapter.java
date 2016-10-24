package com.hybrid.freeopensourceusers.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.CommentFeed;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    SharedPrefManager sharedPrefManager;
    DatabaseOperations dop;
//    private ImageLoader imageLoader;


    public RecyclerTrendingCommentAdapter(Context context) {
        myApplication = MyApplication.getInstance();
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sharedPrefManager = new SharedPrefManager(myApplication.getApplicationContext());
        dop = MyApplication.getDatabase();
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
    public void onBindViewHolder(final ViewholderCommentsFeed holder, final int position) {

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

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.timeOfComment.getVisibility() == View.GONE) {
                    holder.timeOfComment.setVisibility(View.VISIBLE);

                }
                else {
                    holder.timeOfComment.setVisibility(View.GONE);

                }
            }
        });
        holder.body.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(commentsFeedArrayList.get(position).getUser_id()==sharedPrefManager.getUser_id())
                showDeleteReportDialog(view,commentsFeedArrayList.get(position).getComment_id(),commentsFeedArrayList.get(position).getPid());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentsFeedArrayList.size();
    }

    public void showDeleteReportDialog(final View view, final int comment_id,final int pid) {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.dialog_delete_report);
        arrayAdapter.add("Delete Comment");



        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if(strName.equals("Delete Comment")) {
                            deleteComment(comment_id,pid);
                        }
                    }
                });
        builderSingle.show();

    }
    public void deleteComment(final int comment_id,final int pid){
        String URL = Utility.getIPADDRESS()+"deleteComment";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        dop.deleteCommentbyComment_id(dop, comment_id);
                        commentsFeedArrayList = dop.readCommentDataForPost(Integer.toString(pid),dop);
                        notifyDataSetChanged();
                        Toast.makeText(myApplication.getApplicationContext(),"Comment Deleted",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(myApplication.getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
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
                params.put("cid", Integer.toString(comment_id));
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }

    static class ViewholderCommentsFeed extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView name, timeOfComment, comment;
        LinearLayout body;

        public ViewholderCommentsFeed(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.miniProfile);
            name = (TextView) itemView.findViewById(R.id.name_user);
            timeOfComment = (TextView) itemView.findViewById(R.id.time_of_comment);
            comment = (TextView) itemView.findViewById(R.id.comment_text);
            body = (LinearLayout) itemView.findViewById(R.id.body);

        }
    }

}

