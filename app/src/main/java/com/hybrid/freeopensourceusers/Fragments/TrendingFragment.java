package com.hybrid.freeopensourceusers.Fragments;


import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.hybrid.freeopensourceusers.Activities.FirstActivity;
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.Activities.New_Post;
import com.hybrid.freeopensourceusers.Adapters.RecyclerTrendingAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.FabClickListener;
import com.hybrid.freeopensourceusers.Callback.PostFeedLoadingListener;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SampleSuggestionsBuilder;
import com.hybrid.freeopensourceusers.PojoClasses.SimpleAnimationListener;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfile;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment implements PostFeedLoadingListener,FabClickListener{

    private ArrayList<PostFeed> newsFeedsList = new ArrayList<>();

    private RecyclerView trendingRecyclerView;
    private RecyclerTrendingAdapter mRecyclerTrendingAdapter;
    private static final String POST_FEED = "post_feed";
    SwipeRefreshLayout swipeRefreshLayout;

    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);


        trendingRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshForTrendingPost);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trendingRecyclerView.setHasFixedSize(true);
        mRecyclerTrendingAdapter = new RecyclerTrendingAdapter(getActivity(), newsFeedsList);
        mRecyclerTrendingAdapter.setCallback(new RecyclerTrendingAdapter.ClickCallback() {
            @Override
            public void openProfile(PostFeed postFeed, RecyclerTrendingAdapter.ViewholderPostFeed viewHolder) {
                Intent myIntent = new Intent(getActivity(), UserProfile.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("NAME",postFeed.getUser_name());
                myIntent.putExtra("PIC", postFeed.getUser_pic());
                myIntent.putExtra("STATUS", postFeed.getUser_status());
                Pair<View, String> p1 = Pair.create((View)viewHolder.circleImageView, "profile");
                Pair<View, String> p2 = Pair.create((View)viewHolder.user_name, "user_name");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(myIntent, options.toBundle());
            }
        });
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mRecyclerTrendingAdapter);
        trendingRecyclerView.setAdapter( new ScaleInAnimationAdapter(alphaAdapter));
        if (savedInstanceState != null) {
            newsFeedsList = savedInstanceState.getParcelableArrayList(POST_FEED);
        } else {

            DatabaseOperations dop = new DatabaseOperations(MyApplication.getAppContext());
                    newsFeedsList = MyApplication.getDatabase().readPostData(dop);
                    if (newsFeedsList.isEmpty()) {
                        if (isOnline())
                            new TaskLoadPostFeed(this).execute();
                    }

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    swipeRefreshLayout.setRefreshing(true);
                    new TaskLoadPostFeed(TrendingFragment.this).execute();

                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(),"No Network",Toast.LENGTH_SHORT).show();
                }

            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
        mRecyclerTrendingAdapter.setFeed(newsFeedsList);


        return view;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(POST_FEED, newsFeedsList);
    }



    @Override
    public void onPostFeedLoaded(ArrayList<PostFeed> newsFeedsLists) {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        mRecyclerTrendingAdapter.setFeed(newsFeedsLists);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void fabListener() {
        if(isLoggedIn()) {
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_details", MODE_PRIVATE);
//            String api_key = sharedPreferences.getString("api_key", null);
            Intent intent = new Intent(getContext(), New_Post.class);
//            intent.putExtra("API_KEY", api_key);
            startActivity(intent);
        }
//        else if(!isOnline())
//            Toast.makeText(getActivity(),"No Network",Toast.LENGTH_SHORT).show();
        else
            showAlertDialog(getView());


    }

    private void showAlertDialog(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Sign up?")
                .setMessage("Join us to explore more!")
                .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }


    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_details", getActivity().MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean("logged_in", false);
        return status;

    }

}
