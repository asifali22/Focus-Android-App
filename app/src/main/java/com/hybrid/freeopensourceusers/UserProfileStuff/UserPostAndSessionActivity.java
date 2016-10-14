package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hybrid.freeopensourceusers.Adapters.ComplexRecyclerViewAdapter;
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

public class UserPostAndSessionActivity extends AppCompatActivity {

    private String name;
    private Toolbar userDataToolbar;
    private int user_id;
    private RecyclerView mRecyclerView;
    private ComplexRecyclerViewAdapter mComplexRecyclerViewAdapter;
    private TextView noDataTextView;
    private ArrayList<Feeds> feedsArrayList = new ArrayList<>();
    private DatabaseOperations databaseOperations = new DatabaseOperations(this);
    private DatabaseOperations_Session databaseOperations_session = new DatabaseOperations_Session(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_and_session);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("USERNAME");
        user_id = bundle.getInt("UID");

        mRecyclerView = (RecyclerView) findViewById(R.id.userProfileRecyclerView_recyclerView);
        userDataToolbar = (Toolbar) findViewById(R.id.userDataToolbar);
        noDataTextView = (TextView) findViewById(R.id.noDataUserProfile);
        if (userDataToolbar != null)
            setSupportActionBar(userDataToolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(name + "'s feed");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData(user_id);

    }


    private void loadData(int user_id) {
        feedsArrayList = readAllPostForUserProfile(user_id);
        if (feedsArrayList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            noDataTextView.setText("Nothing available here!");
            noDataTextView.setVisibility(View.VISIBLE);
        }
        else{
        noDataTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        mComplexRecyclerViewAdapter = new ComplexRecyclerViewAdapter(this, feedsArrayList);
        mRecyclerView.setAdapter( mComplexRecyclerViewAdapter);
        mComplexRecyclerViewAdapter.setFeed(feedsArrayList);
        }

    }


    private ArrayList<Feeds> readAllPostForUserProfile(int userID) {

        //get postfeeds
        ArrayList<PostFeed> postfeeds = databaseOperations.readPostForUserProfile(userID, databaseOperations);
        //get sessionfeeds
        ArrayList<SessionFeed> sessionfeeds = databaseOperations_session.readSessionForUserProfile(userID, databaseOperations_session);

        RecyclerHeader recyclerHeaderPost = new RecyclerHeader("Posts");
        RecyclerHeader recyclerHeaderSession = new RecyclerHeader("Sessions");


        ArrayList<Feeds> feeds = new ArrayList<>();

        if (!sessionfeeds.isEmpty())
            feeds.add(new Feeds(null, null, recyclerHeaderSession));

        //sessionfeeds to feeds
        for (int i = 0; i < sessionfeeds.size(); i++)
            feeds.add(new Feeds(null, sessionfeeds.get(i), null));

        if (!postfeeds.isEmpty())
            feeds.add(new Feeds(null, null, recyclerHeaderPost));

        //postfeeds to feeds
        for (int i = 0; i < postfeeds.size(); i++)
            feeds.add(new Feeds(postfeeds.get(i), null, null));




        return feeds;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.comment_menu_trending, menu);

        return true;
    }


}
