package com.hybrid.freeopensourceusers.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.AnimatorRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.FabClickListener;
import com.hybrid.freeopensourceusers.Callback.NotificationCallback;
import com.hybrid.freeopensourceusers.Callback.PostFeedLoadingListener;
import com.hybrid.freeopensourceusers.Callback.TabClickListener;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Fragments.SessionFragment;
import com.hybrid.freeopensourceusers.Fragments.TrendingFragment;
import com.hybrid.freeopensourceusers.SearchStuffs.SearchableProvider;
import com.hybrid.freeopensourceusers.Services.MyFireBaseInstanceIdService;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfileOwner;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.R.attr.name;
import static android.R.attr.type;
import static java.security.AccessController.getContext;


public class FirstActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,PostFeedLoadingListener {

    private MyApplication myApplication;
    private ViewPager mViewPager ;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPagerAdapter  mAdapter   =    new ViewPagerAdapter(getSupportFragmentManager());
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton mFab;
    private final String TAG_TRENDING_FRAGMENT = "trending_fragment";
    private RequestQueue requestQueue;
    private VolleySingleton volleySingleton;
    private SharedPrefManager sharedPrefManager;
    private AppBarLayout appBarLayout;
    private Bundle b = null;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        IntentFilter filter = new IntentFilter("FIRST_ACTIVITY");
        registerReceiver(myReceiver, filter);
        b = getIntent().getExtras();
        if(b!=null)
        if(b.getBoolean("login_intent",false)==true)
            new TaskLoadPostFeed(this).execute();

        myApplication = MyApplication.getInstance();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sharedPrefManager = new SharedPrefManager(this);


        bindViews();


        if(sharedPrefManager.isLoggedIn()&&sharedPrefManager.isOnline()){
            FirebaseMessaging.getInstance().subscribeToTopic("fcm_token");
            MyFireBaseInstanceIdService myFireBaseInstanceIdService = new MyFireBaseInstanceIdService();
            myFireBaseInstanceIdService.registerToken(FirebaseInstanceId.getInstance().getToken());
        }
        if(sharedPrefManager.getFirstRunStatus()) {
            sharedPrefManager.setLoggedInStatus(false);
            sharedPrefManager.setFirstRunStatus(false);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, FirstActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

//  FAB Animation not included yet. Test it in future
//    private static final int FAB_ANIM_DURATION = 200;
//
//    public void hide() {
//        // Only use scale animation if FAB is visible
//        if (mFab.getVisibility() == View.VISIBLE) {
//            // Pivots indicate where the animation begins from
//            float pivotX = mFab.getPivotX() + mFab.getTranslationX();
//            float pivotY = mFab.getPivotY() + mFab.getTranslationY();
//
//            // Animate FAB shrinking
//            ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0, pivotX, pivotY);
//            anim.setDuration(FAB_ANIM_DURATION);
//            anim.setInterpolator(getInterpolator());
//            mFab.startAnimation(anim);
//        }
//        mFab.setVisibility(View.INVISIBLE);
//    }
//
//
//    public void show() {
//        show(0, 0);
//    }
//
//    public void show(float translationX, float translationY) {
//
//        // Set FAB's translation
//        setTranslation(translationX, translationY);
//
//        // Only use scale animation if FAB is hidden
//        if (mFab.getVisibility() != View.VISIBLE) {
//            // Pivots indicate where the animation begins from
//            float pivotX = mFab.getPivotX() + translationX;
//            float pivotY = mFab.getPivotY() + translationY;
//
//            ScaleAnimation anim;
//            // If pivots are 0, that means the FAB hasn't been drawn yet so just use the
//            // center of the FAB
//            if (pivotX == 0 || pivotY == 0) {
//                anim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
//                        Animation.RELATIVE_TO_SELF, 0.5f);
//            } else {
//                anim = new ScaleAnimation(0, 1, 0, 1, pivotX, pivotY);
//            }
//
//            // Animate FAB expanding
//            anim.setDuration(FAB_ANIM_DURATION);
//            anim.setInterpolator(getInterpolator());
//            mFab.startAnimation(anim);
//        }
//        mFab.setVisibility(View.VISIBLE);
//    }
//
//    private void setTranslation(float translationX, float translationY) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
//            mFab.animate().setInterpolator(getInterpolator()).setDuration(FAB_ANIM_DURATION)
//                    .translationX(translationX).translationY(translationY);
//        }
//    }
//
//    private Interpolator getInterpolator() {
//        return AnimationUtils.loadInterpolator(this, android.R.interpolator.decelerate_cubic);
//    }




    /*
    Initialize the views
     */


    private void bindViews() {

        mToolbar   =    (Toolbar) findViewById(R.id.m_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layoutFirstAcitivty);
        mViewPager =    (ViewPager) findViewById(R.id.m_viewpager);
        mTabLayout =    (TabLayout) findViewById(R.id.tab_layout);

        mFab       =    (FloatingActionButton) findViewById(R.id.fabButton);
        mFab.setOnClickListener(this);
        mFab.setTag(TAG_TRENDING_FRAGMENT);
        if( mToolbar != null)
            setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("FOCUS");

        setupViewPager(mViewPager);

        if(mTabLayout != null)
            mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager){
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                appBarLayout.setExpanded(true, true);
                Fragment fragment = (Fragment) mAdapter.instantiateItem(mViewPager,mViewPager.getCurrentItem());
                if (fragment instanceof TabClickListener)
                    ((TabClickListener) fragment).tabListener();
            }
        });

    }


    public void setupViewPager(ViewPager viewPager){
        mAdapter.addFrag(new TrendingFragment(), "Trending");
        mAdapter.addFrag(new SessionFragment(), "Sessions");
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getTag().equals(TAG_TRENDING_FRAGMENT)){
            Fragment fragment = (Fragment) mAdapter.instantiateItem(mViewPager,mViewPager.getCurrentItem());
            if (fragment instanceof FabClickListener)
                ((FabClickListener) fragment).fabListener();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onPostFeedLoaded(ArrayList<PostFeed> newsFeedLists) {

    }


     /*
    Class for the View pager
     */

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {


        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings_logged_in:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.action_settings_logged_out:
                custom_signout();
                break;

            case R.id.clearSuggestion:
                SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY,SearchableProvider.MODE);
                searchRecentSuggestions.clearHistory();
                break;
            case R.id.action_settings_open_userProfile:
                startActivity(new Intent(this, UserProfileOwner.class));
                break;

            case R.id.action_settings_main:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void custom_signout(){
        String UPLOAD_URL = "http://focusvce.com/api/v1/signout";
        final ProgressDialog loading = ProgressDialog.show(this,"Signing Out...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                       try{
                           loading.dismiss();
                           JSONObject jsonObject = new JSONObject(s);
                           if(jsonObject.getBoolean("error")==false){
                               FacebookSdk.sdkInitialize(getApplicationContext());
                               LoginManager.getInstance().logOut();
                               signOut();
                               SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                               sharedPreferences.edit().putBoolean("logged_in",false).apply();
                               startActivity(new Intent(FirstActivity.this, LoginActivity.class));
                               finish();
                           }
                           else
                               Toast.makeText(FirstActivity.this,"Sign Out Failed.",Toast.LENGTH_LONG).show();
                       } catch (JSONException e){
                           e.printStackTrace();
                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(FirstActivity.this,"Error occurred! Try again.", Toast.LENGTH_LONG).show();
                        Log.e("Error", volleyError.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",   sharedPrefManager.getApiKey());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...

                    }
                });
    }



    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if(sharedPrefManager.getLoggedinStatus()) {
            inflater.inflate(R.menu.menu_main_logged_out, menu);


            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            final CursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.suggestion_item,
                    null,
                    new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                    new int[]{R.id.textView_suggestion},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            searchView.setSuggestionsAdapter(adapter);


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Cursor c = getSuggestions(newText);
                    adapter.changeCursor(c);
                    return false;
                }
            });
        }
        else
            inflater.inflate(R.menu.menu_main_logged_in,menu);


        return true;
    }

    private Cursor getSuggestions(String newText) {
        Cursor c = null;
        ContentResolver contentResolver = getContentResolver();
        String strUri = "content://"+SearchableProvider.AUTHORITY+"/"+SearchManager.SUGGEST_URI_PATH_QUERY;
        c = contentResolver.query(Uri.parse(strUri), null, null, new String[]{newText}, null);
        return c;
    }


    @Override
    public void onBackPressed() {

            super.onBackPressed();
            moveTaskToBack(true);

    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // refresh UI or finish activity and start again
            String result="",action="";
            final Bundle b = intent.getExtras();
            if(b.getInt("flag",0)==0) {
                result = "Refresh to see new feeds";
                action = "Refresh";
            }
            else if(b.getInt("flag",0) ==1){
                result = "New comment on your post";
                action = "View";
            }


            final Snackbar snackBar = Snackbar.make(findViewById(R.id.main_layout), result, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b.getInt("flag",0)==0){
                        Fragment fragment = (Fragment) mAdapter.instantiateItem(mViewPager,mViewPager.getCurrentItem());
                        if (fragment instanceof NotificationCallback)
                            ((NotificationCallback) fragment).notifyUser();
                    }

                    else if(b.getInt("flag",0)==1){
                        String api_key = b.getString("API_KEY");
                        int flag_extra = b.getInt("FLAG");
                        SharedPreferences sharedPreferences = getSharedPreferences("comment",MODE_PRIVATE);
                        String pid=sharedPreferences.getString("comment_pid",null);
                        Intent i = new Intent(FirstActivity.this, Comment_Actiivity.class);
                        i.putExtra("API_KEY",api_key);
                        i.putExtra("FLAG",flag_extra);
                        i.putExtra("PID_VALUE",pid);
                        startActivity(i);
                    }
                }
            });
            snackBar.show();
        }
    };

}
