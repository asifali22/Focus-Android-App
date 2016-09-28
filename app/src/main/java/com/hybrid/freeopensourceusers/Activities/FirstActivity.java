package com.hybrid.freeopensourceusers.Activities;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.animation.Animator;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hybrid.freeopensourceusers.Adapters.RecyclerTrendingAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.FabClickListener;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SampleSuggestionsBuilder;
import com.hybrid.freeopensourceusers.PojoClasses.SimpleAnimationListener;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Fragments.SessionFragment;
import com.hybrid.freeopensourceusers.Fragments.TrendingFragment;
import com.hybrid.freeopensourceusers.SearchStuffs.SearchableProvider;
import com.hybrid.freeopensourceusers.Services.MyFireBaseInstanceIdService;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.List;


public class FirstActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    MyApplication myApplication;
    private ViewPager mViewPager ;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPagerAdapter  mAdapter   =    new ViewPagerAdapter(getSupportFragmentManager());
    GoogleApiClient mGoogleApiClient;
    SharedPreferences user_details;
    private FloatingActionButton mFab;
    private final String TAG_TRENDING_FRAGMENT = "trending_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        myApplication = MyApplication.getInstance();

        //setupJob();
        bindViews();
//        setUpSearchView();
        if(isLoggedIn()&&isOnline()){
            FirebaseMessaging.getInstance().subscribeToTopic("fcm_token");
            MyFireBaseInstanceIdService myFireBaseInstanceIdService = new MyFireBaseInstanceIdService();
            myFireBaseInstanceIdService.registerToken(FirebaseInstanceId.getInstance().getToken());
        }
        user_details = getSharedPreferences("user_details", MODE_PRIVATE);
        if(getSharedPreferences("com.hybrid.freeopensourceusers", MODE_PRIVATE).getBoolean("firstrun",true)) {
            user_details.edit().putBoolean("logged_in", false).apply();
            getSharedPreferences("com.hybrid.freeopensourceusers", MODE_PRIVATE).edit().putBoolean("firstrun",false).apply();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, FirstActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    /*
    Initialize the views
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public String getFcmToken() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String api_key = sharedPreferences.getString("fcm_token", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }
    private void bindViews() {

        mToolbar   =    (Toolbar) findViewById(R.id.m_toolbar);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings_logged_in:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.action_settings_logged_out:
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();
                signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("logged_in",false).apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;


            case R.id.clearSuggestion:
                SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY,SearchableProvider.MODE);
                searchRecentSuggestions.clearHistory();

        }

        return super.onOptionsItemSelected(item);
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
        if(user_details.getBoolean("logged_in",false)) {
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

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean("logged_in", false);
        return status;

    }


}
