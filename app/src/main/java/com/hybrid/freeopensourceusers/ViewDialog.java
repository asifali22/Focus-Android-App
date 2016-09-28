package com.hybrid.freeopensourceusers;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by monster on 28/9/16.
 */

public class ViewDialog {

    public void showDialog(Activity activity, String userImageText, String userNameText, String postTitleText,
                           String postDescriptionText, String postPicUrlText){

        MyTextDrawable myTextDrawable = new MyTextDrawable();

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_for_dialog);

         CircleImageView circleImageView;
         TextView user_name, post_title, post_description, like_count, comment_count;
         ImageView post_pic;
        TextView addButton, cancelButton;



        circleImageView = (CircleImageView) dialog.findViewById(R.id.user_profile_image);
        user_name = (TextView) dialog.findViewById(R.id.user_name);
        post_title = (TextView) dialog.findViewById(R.id.post_title);
        post_description = (TextView) dialog.findViewById(R.id.post_description);
        like_count = (TextView) dialog.findViewById(R.id.like_count);
        comment_count = (TextView) dialog.findViewById(R.id.comment_count);
        post_pic = (ImageView) dialog.findViewById(R.id.post_pic);
        addButton = (TextView) dialog.findViewById(R.id.addButtonDialog);
        cancelButton = (TextView) dialog.findViewById(R.id.cancelButtonDialog);



        Glide.with(activity)
                .load(postPicUrlText)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(myTextDrawable.setTextDrawableForError("Error!"))
                .crossFade()
                .into(post_pic);

        if (userImageText.isEmpty())
            userImageText = userNameText;

        Glide.with(activity)
                .load(userImageText)
                .fitCenter()
                .dontAnimate()
                .placeholder(R.drawable.blank_person_final)
                .error(myTextDrawable.setTextDrawable(userNameText))
                .into(circleImageView);

        user_name.setText(userNameText);
        post_title.setText(postTitleText);
        post_description.setText(postDescriptionText);
        like_count.setText("0");
        comment_count.setText("0");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}

