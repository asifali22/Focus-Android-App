package com.hybrid.freeopensourceusers.SearchStuffs;

import android.app.SearchManager;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hybrid.freeopensourceusers.Adapters.ComplexRecyclerViewAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.Feeds;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations_Session;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class SearchableActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    DatabaseOperations databaseOperations = new DatabaseOperations(MyApplication.getAppContext());
    DatabaseOperations_Session databaseOperations_session = new DatabaseOperations_Session(MyApplication.getAppContext());




    private RecyclerView searchRecyclerView;
    private ComplexRecyclerViewAdapter mComplexRecyclerViewAdapter;

    private ArrayList<Feeds> feedsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String query;
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             query = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle(query);
            feedsArrayList = readAllPostForSearch(query);
            mComplexRecyclerViewAdapter = new ComplexRecyclerViewAdapter(this, feedsArrayList);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mComplexRecyclerViewAdapter);
            searchRecyclerView.setAdapter( new ScaleInAnimationAdapter(alphaAdapter));
            mComplexRecyclerViewAdapter.setFeed(feedsArrayList);
//            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,SearchableProvider.AUTHORITY,SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(query, null);
        }

    }


    private void doMySearch(String query) {

//        List<SessionFeed> sessionFeeds = databaseOperations_session.readSessionForSearch(query,databaseOperations_session);
//        List<PostFeed> postFeeds = databaseOperations.readPostForSearch(query,databaseOperations);
//
//        Feeds feeds = new Feeds(postFeeds,sessionFeeds);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
           this.finish();
        }

        return super.onOptionsItemSelected(item);
    }



    public ArrayList<Feeds> readAllPostForSearch(String searchText){
        //get postfeeds
        ArrayList<PostFeed> postfeeds = databaseOperations.readPostForSearch(searchText, databaseOperations);
        //get sessionfeeds
        ArrayList<SessionFeed> sessionfeeds = databaseOperations_session.readSessionForSearch(searchText, databaseOperations_session);

        ArrayList<Feeds> feeds = new ArrayList<>();

        //postfeeds to feeds
        for(int i = 0; i<postfeeds.size(); i++)
            feeds.add(new Feeds(postfeeds.get(i), null));

        //sessionfeeds to feeds
        for(int i = 0; i<sessionfeeds.size(); i++)
            feeds.add(new Feeds(null, sessionfeeds.get(i)));

        return feeds;

    }
}
