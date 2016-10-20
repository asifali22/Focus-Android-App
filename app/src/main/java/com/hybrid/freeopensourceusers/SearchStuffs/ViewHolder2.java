package com.hybrid.freeopensourceusers.SearchStuffs;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hybrid.freeopensourceusers.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by monster on 25/9/16.
 */

public class ViewHolder2 extends RecyclerView.ViewHolder{

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

    public ViewHolder2(View view) {
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

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public CircleImageView getCircleImageView() {
        return circleImageView;
    }

    public void setCircleImageView(CircleImageView circleImageView) {
        this.circleImageView = circleImageView;
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public LinearLayout getPost_body() {
        return post_body;
    }

    public void setPost_body(LinearLayout post_body) {
        this.post_body = post_body;
    }

    public RelativeLayout getPost_header() {
        return post_header;
    }

    public void setPost_header(RelativeLayout post_header) {
        this.post_header = post_header;
    }

    public TextView getTitle_session() {
        return title_session;
    }

    public void setTitle_session(TextView title_session) {
        this.title_session = title_session;
    }

    public TextView getUser_name() {
        return user_name;
    }

    public void setUser_name(TextView user_name) {
        this.user_name = user_name;
    }
}
