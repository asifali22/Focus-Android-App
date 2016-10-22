package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.GridView;
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

import com.hybrid.freeopensourceusers.Activities.session_details;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
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


public class UserProfile extends AppCompatActivity
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
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private CoordinatorLayout coordinatorLayout;
    private SharedPrefManager sharedPrefManager;
    static private String mail;
    private ImageView mailButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bindActivity();

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sharedPrefManager = new SharedPrefManager(this);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getInt("UID");
        name = bundle.getString("NAME");
        profilepic = bundle.getString("PIC");
        status = bundle.getString("STATUS");

        mTitle.setText(name);
        mTitleBehindPhoto.setText(name);
        mStatus.setText(status);

        mFeed.setText(name + "'s feed");

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
                letsFetchData();

            }
        }, 1000);




    }





    private void letsFetchData() {
        String UPLOAD_URL = "http://focusvce.com/api/v1/userDetails";
      //  final ProgressDialog loading = ProgressDialog.show(UserProfile.this, "Updating...", "Please wait...", false, false);
        Snackbar.make(coordinatorLayout,"Refreshing...", Snackbar.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        //    loading.dismiss();
                        Snackbar.make(coordinatorLayout,"Updating...", Snackbar.LENGTH_SHORT).show();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (!jsonObject.getBoolean("error")){
                                JSONArray jsonArray = jsonObject.getJSONArray("user_details");
                                JSONObject finalObject = jsonArray.getJSONObject(0);
                                String u_status = finalObject.getString("user_status");
                                String about_user = finalObject.getString("about_user");
                                String memORCo = finalObject.getString("su_user");
                                String aoi = finalObject.getString("aoi");
                                String org = finalObject.getString("organisation");
                                mail = finalObject.getString("user_email");

                                mailButton.setClickable(true);

                                if (about_user.isEmpty())
                                    user_desc.setHint("Description not available");
                                else
                                    user_desc.setText(about_user);

                                mStatus.setText(u_status);

                                if (memORCo.equals("0"))
                                    su_user.setText("Member");
                                else
                                    su_user.setText("Coordinator");

                                if (aoi.isEmpty())
                                    areaOfInterest.setHint("Not available");
                                else
                                    areaOfInterest.setText(aoi+"");
                                if (org.isEmpty())
                                    organisation.setHint("Not available");
                                else
                                    organisation.setText(org);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Showing toast message of the response

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                     //   loading.dismiss();

                        //Showing toast
                        Snackbar.make(coordinatorLayout,"Failed to update : Try again",Snackbar.LENGTH_SHORT).show();
                       mailButton.setClickable(false);
                        Log.e("Error", volleyError.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey() + "");
                return params;
            }@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String, String> params = new Hashtable<>();



                params.put("user_id", userID+"");


                //returning parameters
                return params;
            }
        };


        requestQueue.add(stringRequest);


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
        mailButton = (ImageView) findViewById(R.id.mailButtonUserProfile);
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

    public void mailButtonClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + mail));
        startActivity(intent);
    }



}

