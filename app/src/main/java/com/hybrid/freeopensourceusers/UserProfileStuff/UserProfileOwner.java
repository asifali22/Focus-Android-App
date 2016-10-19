package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.Activities.new_session_add;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileOwner extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle, mTitleBehindPhoto, mStatus, mFeed, user_desc, su_user, areaOfInterest, organisation;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private String name, profilepic, status;
    private int userID;
    private CircleImageView avatar;
    private ImageView timeLine;
   // private VolleySingleton volleySingleton;
   // private RequestQueue requestQueue;
    private CoordinatorLayout coordinatorLayout;
    private SharedPrefManager sharedPrefManager;
    static private String mail;
  //  private ImageView mailButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_owner);

        bindActivity();

        sharedPrefManager = new SharedPrefManager(this);

        userID = sharedPrefManager.getUser_id();
        name = sharedPrefManager.getUserName();
        profilepic = sharedPrefManager.getUserImage();
        status = sharedPrefManager.getUserStatus();

        mTitle.setText(name);
        mTitleBehindPhoto.setText(name);
        mStatus.setText(status);
        mFeed.setText("Feed");
        if(sharedPrefManager.getSu_User().equals("1")) {
           su_user.setText("Coordinator");
        }
        else
            su_user.setText("Member");
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


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(coordinatorLayout,"You are logged in as "+sharedPrefManager.getUserEmail(),Snackbar.LENGTH_SHORT).show();
            }
        }, 1000);




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
        mFeed           = (TextView) findViewById(R.id.user_feed);
        su_user         =  (TextView) findViewById(R.id.su_user);
        user_desc       =  (TextView) findViewById(R.id.user_desc);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorUserProfileMain);
        areaOfInterest = (TextView) findViewById(R.id.user_Interest);
        organisation = (TextView) findViewById(R.id.user_organisation);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_edit:
                startActivity(new Intent(this, EditProfile.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
