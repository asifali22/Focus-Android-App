package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.hybrid.freeopensourceusers.R;

import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfile extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle, mTitleBehindPhoto, mStatus;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private String name, profilepic, status;
    private int userID;
    private CircleImageView avatar;
    private ImageView timeLine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bindActivity();

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getInt("UID");
        name = bundle.getString("NAME");
        profilepic = bundle.getString("PIC");
        status = bundle.getString("STATUS");

        mTitle.setText(name);
        mTitleBehindPhoto.setText(name);
        mStatus.setText(status);

        MyTextDrawable myTextDrawable = new MyTextDrawable();

        Glide.with(this)
                .load(profilepic)
                .fitCenter()
                .dontAnimate()
                .placeholder(R.drawable.blank_person_final)
                .error(myTextDrawable.setTextDrawable(name))
                .into(avatar);

        Glide.with(this)
                .load(R.drawable.geometry)
                .centerCrop()
                .into(timeLine);


        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);


    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
        mTitleBehindPhoto = (TextView) findViewById(R.id.behind_Image_TextView);
        mStatus         = (TextView)findViewById(R.id.user_status);
        avatar          = (CircleImageView) findViewById(R.id.user_profile_image_userActivity);
        timeLine        = (ImageView) findViewById(R.id.timeLineImageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public void openUserPostAndSession(View view) {
        Intent intent = new Intent(this, UserPostAndSessionActivity.class);
        intent.putExtra("UID", userID);
        intent.putExtra("USERNAME", name);
        startActivity(intent);
    }
}

