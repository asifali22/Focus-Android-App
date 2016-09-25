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

//import me.tatarka.support.job.JobScheduler;

public class FirstActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

//    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1023;
//    private PersistentSearchView mSearchView;
//    private View mSearchTintView;
//    private RecyclerTrendingAdapter mResultAdapter;
//    private RecyclerView mRecyclerView;
//    private Boolean isSearchOpen = false;
//    private ArrayList<PostFeed> newsFeedsList = new ArrayList<>();

//    private static final int JOB_ID = 100;
//    private static final long POLL_FREQUENCY = 3000;
    MyApplication myApplication;
//    private JobScheduler mJobScheduler;


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

    /*private void setupJob() {
        mJobScheduler = JobScheduler.getInstance(this);
        //set an initial delay with a Handler so that the data loading by the JobScheduler does not clash with the loading inside the Fragment
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //schedule the job after the delay has been elapsed
                constructJob();
            }
        }, 30000);
    }*/

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
//        mSearchView = (PersistentSearchView) findViewById(R.id.searchview);
//        mSearchTintView = findViewById(R.id.view_search_tint);
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

//        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
//        if(delegate.isVoiceRecognitionAvailable()) {
//            mSearchView.setVoiceRecognitionDelegate(delegate);
//        }



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

//            case R.id.action_search:
//                    openSearch();
//                        return true;

            case R.id.clearSuggestion:
                SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY,SearchableProvider.MODE);
                searchRecentSuggestions.clearHistory();


        }

        return super.onOptionsItemSelected(item);
    }

//    public void openSearch() {
//
//
//
//        View menuItemView = findViewById(R.id.action_search);
//        mSearchView.setStartPositionFromMenuItem(menuItemView);
//        mSearchView.openSearch();
//    }

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
        if(user_details.getBoolean("logged_in",false))
        inflater.inflate(R.menu.menu_main_logged_out, menu);
        else
        inflater.inflate(R.menu.menu_main_logged_in,menu);

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
//        if(mSearchView.isSearching()) {
//            mSearchView.closeSearch();
//            mTabLayout.setVisibility(View.VISIBLE);
//
//        } else if(mRecyclerView.getVisibility() == View.VISIBLE) {
//            mResultAdapter.clear();
//            mRecyclerView.setVisibility(View.GONE);
//            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
//            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS| AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
//            mTabLayout.setVisibility(View.VISIBLE);
//
//        } else {
            super.onBackPressed();
            moveTaskToBack(true);
//        }
    }
    /*private void constructJob(){
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,new ComponentName(this, MyService.class));
        builder.setPeriodic(POLL_FREQUENCY)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);

        mJobScheduler.schedule(builder.build());
    }*/


//    public void setUpSearchView() {
//        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_search_result);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mResultAdapter = new RecyclerTrendingAdapter(this,new ArrayList<PostFeed>());
//        mRecyclerView.setAdapter(mResultAdapter);
//        mSearchTintView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)  {
//                mSearchView.cancelEditing();
//            }
//        });
//
//        mSearchView.setHomeButtonListener(new PersistentSearchView.HomeButtonListener() {
//
//            @Override
//            public void onHomeButtonClick() {
//                // Hamburger has been clicked
////                Toast.makeText(FirstActivity.this, "Menu click",
////                        Toast.LENGTH_LONG).show();
//            }
//
//        });
//        mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(this));
//        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {
//
//            @Override
//            public void onSearchEditOpened() {
//                //Use this to tint the screen
//                mSearchTintView.setVisibility(View.VISIBLE);
//                mSearchTintView
//                        .animate()
//                        .alpha(1.0f)
//                        .setDuration(300)
//                        .setListener(new SimpleAnimationListener())
//                        .start();
//                isSearchOpen = true;
//
//
//
//            }
//
//            @Override
//            public void onSearchEditClosed() {
//                mSearchTintView
//                        .animate()
//                        .alpha(0.0f)
//                        .setDuration(300)
//                        .setListener(new SimpleAnimationListener() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                mSearchTintView.setVisibility(View.GONE);
//                            }
//                        })
//                        .start();
//                if (isSearchOpen){
//                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
//                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS| AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
//                    mTabLayout.setVisibility(View.VISIBLE);
//                }
//
//            }
//
//            @Override
//            public boolean onSearchEditBackPressed() {
//                if(mSearchView.isEditing()) {
//                   mSearchView.cancelEditing();
//                    mTabLayout.setVisibility(View.VISIBLE);
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public void onSearchExit() {
//                mResultAdapter.clear();
//                if (mRecyclerView.getVisibility() == View.VISIBLE) {
//                    mRecyclerView.setVisibility(View.GONE);
//                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
//                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS| AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
//                    mTabLayout.setVisibility(View.VISIBLE);
//                    mFab.setVisibility(View.VISIBLE);
//
//                }
//            }
//
//            @Override
//            public void onSearchTermChanged(String term) {
//
//            }
//
//            @Override
//            public void onSearch(String string) {
////                Toast.makeText(FirstActivity.this, "Results for " + string , Toast.LENGTH_SHORT).show();
//                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
//                params.setScrollFlags(0);
//                DatabaseOperations dop = new DatabaseOperations(FirstActivity.this);
//                dop.insertSuggestionForSearch(dop,string);
//                isSearchOpen = false;
//                mFab.setVisibility(View.INVISIBLE);
//                mTabLayout.setVisibility(View.GONE);
//                mRecyclerView.setVisibility(View.VISIBLE);
//                fillResultToRecyclerView(string);
//            }
//
//            @Override
//            public void onSearchCleared() {
//
//            }
//
//        });
//
//    }
//
//    private void fillResultToRecyclerView(String query) {
//
//        DatabaseOperations dop = new DatabaseOperations(this);
//        newsFeedsList = dop.readPostForSearch(query,dop);
////        for(int i =0; i< 100; i++) {
////            SearchResult result = new SearchResult(query, query + Integer.toString(i), "");
////            newResults.add(result);
////        }
//
//
//        mResultAdapter.replaceWith(newsFeedsList);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
//            ArrayList<String> matches = data
//                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            mSearchView.populateEditText(matches);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean("logged_in", false);
        return status;

    }


}
