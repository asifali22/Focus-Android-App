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
import android.view.View;
import android.widget.TextView;

import com.hybrid.freeopensourceusers.Adapters.ComplexRecyclerViewAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.PojoClasses.Feeds;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.RecyclerHeader;
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
    DatabaseOperations databaseOperations = new DatabaseOperations(this);
    DatabaseOperations_Session databaseOperations_session = new DatabaseOperations_Session(this);
    private TextView noSearchResults;



    private RecyclerView searchRecyclerView;
    private ComplexRecyclerViewAdapter mComplexRecyclerViewAdapter;

    private ArrayList<Feeds> feedsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        noSearchResults = (TextView) findViewById(R.id.noSearchResultsTV);
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
            if (!feedsArrayList.isEmpty()) {
                searchRecyclerView.setVisibility(View.VISIBLE);
                noSearchResults.setVisibility(View.GONE);
                mComplexRecyclerViewAdapter = new ComplexRecyclerViewAdapter(this, feedsArrayList);
                searchRecyclerView.setAdapter(mComplexRecyclerViewAdapter);
                mComplexRecyclerViewAdapter.setFeed(feedsArrayList);
            }else {
                searchRecyclerView.setVisibility(View.GONE);
                noSearchResults.setText("No results found!");
                noSearchResults.setVisibility(View.VISIBLE);
            }
//            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
                SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
                searchRecentSuggestions.saveRecentQuery(query, null);

        }

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
        ArrayList<PostFeed> postfeeds = MyApplication.getDatabase().readPostForSearch(searchText, databaseOperations);
        //get sessionfeeds
        ArrayList<SessionFeed> sessionfeeds = MyApplication.getMsDatabase().readSessionForSearch(searchText, databaseOperations_session);

        RecyclerHeader recyclerHeaderPost = new RecyclerHeader("Posts");
        RecyclerHeader recyclerHeaderSession = new RecyclerHeader("Sessions");
        ArrayList<Feeds> feeds = new ArrayList<>();

        if (!sessionfeeds.isEmpty())
            feeds.add(new Feeds(null,null,recyclerHeaderSession));

        //sessionfeeds to feeds
        for(int i = 0; i<sessionfeeds.size(); i++)
            feeds.add(new Feeds(null, sessionfeeds.get(i),null));

        if (!postfeeds.isEmpty())
            feeds.add(new Feeds(null,null,recyclerHeaderPost));

        //postfeeds to feeds
        for(int i = 0; i<postfeeds.size(); i++)
            feeds.add(new Feeds(postfeeds.get(i), null, null));





        return feeds;

    }
}
