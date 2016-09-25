package com.hybrid.freeopensourceusers.SearchStuffs;

import android.app.SearchManager;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hybrid.freeopensourceusers.Adapters.RecyclerTrendingAdapter;
import com.hybrid.freeopensourceusers.R;

public class SearchableActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView searchRecyclerView;
//    private RecyclerViewSearchAdapter mRecyclerViewSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String query;
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             query = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle(query);
            doMySearch(query);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,SearchableProvider.AUTHORITY,SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(query, null);
        }




    }

    private void doMySearch(String query) {



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
}
