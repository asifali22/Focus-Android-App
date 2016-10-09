package com.hybrid.freeopensourceusers.SearchStuffs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hybrid.freeopensourceusers.R;
import com.like.LikeButton;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by monster on 25/9/16.
 */

public  class ViewHolder1 extends RecyclerView.ViewHolder {


    public CircleImageView circleImageView;
    public TextView user_name, user_share_time, post_title, post_description, like_count, comment_count, postTitleNoImage,postDescriptionNOImage,post_descriptionCustom;
    public ImageView post_pic, comment_button;
    public RelativeLayout post_header;
    public LinearLayout post_body, postBodyNoImage;
    public LikeButton plus_like, minus_dislike;

    public ViewHolder1(View itemView) {
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
        postTitleNoImage = (TextView) itemView.findViewById(R.id.post_titleForNoImage);
        postDescriptionNOImage = (TextView) itemView.findViewById(R.id.post_descriptionForNoImage);
        postBodyNoImage = (LinearLayout) itemView.findViewById(R.id.post_bodyForNoImage);
        post_descriptionCustom = (TextView) itemView.findViewById(R.id.post_descriptionCustom);
    }

    public CircleImageView getCircleImageView() {
        return circleImageView;
    }

    public void setCircleImageView(CircleImageView circleImageView) {
        this.circleImageView = circleImageView;
    }

    public TextView getUser_name() {
        return user_name;
    }

    public void setUser_name(TextView user_name) {
        this.user_name = user_name;
    }

    public TextView getUser_share_time() {
        return user_share_time;
    }

    public void setUser_share_time(TextView user_share_time) {
        this.user_share_time = user_share_time;
    }

    public TextView getPost_title() {
        return post_title;
    }

    public void setPost_title(TextView post_title) {
        this.post_title = post_title;
    }

    public ImageView getPost_pic() {
        return post_pic;
    }

    public void setPost_pic(ImageView post_pic) {
        this.post_pic = post_pic;
    }

    public TextView getPost_description() {
        return post_description;
    }

    public void setPost_description(TextView post_description) {
        this.post_description = post_description;
    }

    public RelativeLayout getPost_header() {
        return post_header;
    }

    public void setPost_header(RelativeLayout post_header) {
        this.post_header = post_header;
    }

    public LinearLayout getPost_body() {
        return post_body;
    }

    public void setPost_body(LinearLayout post_body) {
        this.post_body = post_body;
    }

    public LikeButton getPlus_like() {
        return plus_like;
    }

    public void setPlus_like(LikeButton plus_like) {
        this.plus_like = plus_like;
    }

    public LikeButton getMinus_dislike() {
        return minus_dislike;
    }

    public void setMinus_dislike(LikeButton minus_dislike) {
        this.minus_dislike = minus_dislike;
    }

    public TextView getLike_count() {
        return like_count;
    }

    public void setLike_count(TextView like_count) {
        this.like_count = like_count;
    }

    public ImageView getComment_button() {
        return comment_button;
    }

    public void setComment_button(ImageView comment_button) {
        this.comment_button = comment_button;
    }

    public TextView getComment_count() {
        return comment_count;
    }

    public void setComment_count(TextView comment_count) {
        this.comment_count = comment_count;
    }
}
