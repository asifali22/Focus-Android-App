package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.Activities.RegisterActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.UpdateInterest;
import com.hybrid.freeopensourceusers.Callback.UpdateOrg;
import com.hybrid.freeopensourceusers.Callback.UpdateStatus;
import com.hybrid.freeopensourceusers.Callback.UpdateUI;
import com.hybrid.freeopensourceusers.Fragments.MainFragment;
import com.hybrid.freeopensourceusers.Fragments.MainFragmentForUserProfile;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileOwner extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener, UpdateUI,UpdateInterest,UpdateOrg,UpdateStatus{

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;
    private static final int STATUS_CODE = 4;
    private static final int DESC = 1;
    private static final int INTEREST = 2;
    private static final int ORG = 3;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle, mTitleBehindPhoto, mStatus, mFeed, user_desc, su_user, areaOfInterest, organisation;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private String name, profilepic, status, about, aoi, org;
    private int userID;
    private CircleImageView avatar;
    private ImageView editDesc, editInterest, editOrg;
    private ImageView timeLine;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private CoordinatorLayout coordinatorLayout;
    private SharedPrefManager sharedPrefManager;
    private static Bitmap bitmap;
    private ProgressDialog mProgressDialog;


    private ExecutorService mExecutor;
    public static Intent createIntent(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, UserProfileOwner.class);
        intent.setData(uri);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_owner);

        bindActivity();

        sharedPrefManager = new SharedPrefManager(this);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        userID = sharedPrefManager.getUser_id();
        name = sharedPrefManager.getUserName();
        profilepic = sharedPrefManager.getUserImage();
        status = sharedPrefManager.getUserStatus();
        about = sharedPrefManager.getAboutUser();
        aoi = sharedPrefManager.getAreaOfInterest();
        org = sharedPrefManager.getOrganisation();


        mTitle.setText(name);
        mTitleBehindPhoto.setText(name);
        mStatus.setText(status);
        mFeed.setText("Feed");
        if(sharedPrefManager.getSu_User().equals("1")) {
           su_user.setText("Coordinator");
        }
        else
            su_user.setText("Member");

        user_desc.setText(about);
        areaOfInterest.setText(aoi);
        organisation.setText(org);

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



        mExecutor = Executors.newSingleThreadExecutor();

        final Uri uri = getIntent().getData();
        if(uri != null)
            mExecutor.submit(new UserProfileOwner.LoadScaledImageTask(this, uri, avatar, calcImageSize()));





    }


    public int calcImageSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return Math.min(Math.max(metrics.widthPixels, metrics.heightPixels), 2048);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        mExecutor.shutdown();
        super.onDestroy();
    }


    public boolean isLargeImage(Bitmap bm) {
        return bm.getWidth() > 2048 || bm.getHeight() > 2048;
    }


    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        startActivity(UserProfileOwner.createIntent(this, uri));
    }


    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
        mTitleBehindPhoto = (TextView) findViewById(R.id.behind_Image_TextView);
        mStatus         = (TextView)findViewById(R.id.user_status);
        avatar          = (CircleImageView) findViewById(R.id.user_profile_image_userActivityOwner);
        timeLine        = (ImageView) findViewById(R.id.timeLineImageView);
        mFeed           = (TextView) findViewById(R.id.user_feed);
        su_user         =  (TextView) findViewById(R.id.su_user);
        user_desc       =  (TextView) findViewById(R.id.user_desc);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorUserProfileMain);
        areaOfInterest = (TextView) findViewById(R.id.user_Interest);
        organisation = (TextView) findViewById(R.id.user_organisation);
        editDesc = (ImageView) findViewById(R.id.editButtonDesc);
        editInterest = (ImageView) findViewById(R.id.editButtonInterest);
        editOrg = (ImageView) findViewById(R.id.editButtonOrg);

        editDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about = sharedPrefManager.getAboutUser();
                Bundle bundle = new Bundle();
                bundle.putInt("FLAG_FINAL", DESC);
                bundle.putInt("MAX",500);
                bundle.putString("DESCRIPTION", about);
                // set Fragmentclass Arguments
                EditFragmentUserProfile fragmentObject = new EditFragmentUserProfile();
                fragmentObject.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_containerUserProfile,fragmentObject,"frag").commit();

            }
        });
        editInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aoi = sharedPrefManager.getAreaOfInterest();
                Bundle bundle = new Bundle();
                bundle.putInt("FLAG_FINAL", INTEREST);
                bundle.putString("INTEREST", aoi);
                bundle.putInt("MAX", 500);
                // set Fragmentclass Arguments
                EditFragmentUserProfile fragmentObject = new EditFragmentUserProfile();
                fragmentObject.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_containerUserProfile,fragmentObject,"frag").commit();
            }
        });
        editOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                org = sharedPrefManager.getOrganisation();
                Bundle bundle = new Bundle();
                bundle.putInt("FLAG_FINAL", ORG);
                bundle.putString("ORGANISATION", org);
                bundle.putInt("MAX",75);
                // set Fragmentclass Arguments
                EditFragmentUserProfile fragmentObject = new EditFragmentUserProfile();
                fragmentObject.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_containerUserProfile,fragmentObject,"frag").commit();
            }
        });
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
                status = sharedPrefManager.getUserStatus();
                Bundle bundle = new Bundle();
                bundle.putInt("FLAG_FINAL", STATUS_CODE);
                bundle.putString("STATUS", status);
                bundle.putInt("MAX", 50);
                // set Fragmentclass Arguments
                EditFragmentUserProfile fragmentObject = new EditFragmentUserProfile();
                fragmentObject.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_containerUserProfile,fragmentObject,"frag").commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag("frag") != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentByTag("frag"))
                    .commit();
        }else  if(getSupportFragmentManager().findFragmentByTag("frag_profile") != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentByTag("frag_profile"))
                    .commit();
        }
        else {
            finish();
        }
    }


    @Override
    public void updateDESC(String desc) {
        user_desc.setText(desc);
    }

    @Override
    public void updateInterest(String interest) {
        areaOfInterest.setText(interest);
    }

    @Override
    public void updateOrg(String org) {
        organisation.setText(org);
    }

    @Override
    public void statusUpdate(String status) {
        mStatus.setText(status);
    }




    public void changeUserProfilePic(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_containerUserProfile, MainFragmentForUserProfile.getInstance(),"frag_profile")
                .commit();
    }




    public static class LoadScaledImageTask implements Runnable {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        Context context;
        Uri uri;
        ImageView imageView;
        int width;

        public LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {
            this.context = context;
            this.uri = uri;
            this.imageView = imageView;
            this.width = width;
        }

        @Override
        public void run() {
            final int exifRotation = Utils.getExifOrientation(context, uri);
            Log.d("TAG", "exifRotation = " + exifRotation);
            int maxSize = Utils.getMaxSize();
            int requestSize = Math.min(width, maxSize);
            try {
                final Bitmap sampledBitmap = Utils.decodeSampledBitmapFromUri(context, uri, requestSize);
                setBitMap(sampledBitmap);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageMatrix(Utils.getMatrixFromExifOrientation(exifRotation));
                        imageView.setImageBitmap(sampledBitmap);
                    }
                });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setBitMap(Bitmap bm){
        bitmap = bm;
    }



    private void showProgressDialog() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


}
