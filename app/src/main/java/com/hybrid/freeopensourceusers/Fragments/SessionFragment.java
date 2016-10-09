package com.hybrid.freeopensourceusers.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hybrid.freeopensourceusers.Activities.new_session_add;
import com.hybrid.freeopensourceusers.Activities.session_details;
import com.hybrid.freeopensourceusers.Adapters.RecyclerSessionAdapter;
import com.hybrid.freeopensourceusers.Adapters.RecyclerTrendingAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.FabClickListener;
import com.hybrid.freeopensourceusers.Callback.SessionFeedLoadingListener;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations_Session;
import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
import com.hybrid.freeopensourceusers.Task.TaskLoadSessionFeed;
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfile;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment implements SessionFeedLoadingListener, FabClickListener{

    private RecyclerView recyclerView;
    private RecyclerSessionAdapter recyclerSessionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<SessionFeed> newsFeedsList = new ArrayList<>();
    private static final String SESSION_FEED = "session_feed";
    public SessionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_session, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_session);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshForSessionPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerSessionAdapter = new RecyclerSessionAdapter(getContext());
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclerSessionAdapter);
        recyclerView.setAdapter( new ScaleInAnimationAdapter(alphaAdapter));
        recyclerSessionAdapter.setCallback(new RecyclerSessionAdapter.ClickCallback() {
            @Override
            public void openProfile(SessionFeed sessionFeed, RecyclerSessionAdapter.ViewholderSessionFeed viewHolder) {
                Intent myIntent = new Intent(getActivity(), UserProfile.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("UID", sessionFeed.getUid());
                myIntent.putExtra("NAME",sessionFeed.getUser_name());
                myIntent.putExtra("PIC", sessionFeed.getUser_pic());
                myIntent.putExtra("STATUS", sessionFeed.getUser_status());
                Pair<View, String> p1 = Pair.create((View)viewHolder.circleImageView, "profile");
                Pair<View, String> p2 = Pair.create((View)viewHolder.user_name, "user_name");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(myIntent, options.toBundle());
            }

            @Override
            public void openSessionDetails(SessionFeed sessionFeed, RecyclerSessionAdapter.ViewholderSessionFeed viewHolder) {
                Intent i = new Intent(getActivity(),session_details.class);
                i.putExtra("id",sessionFeed.getSession_id());
                i.putExtra("title",sessionFeed.getSession_title());
                i.putExtra("desc",sessionFeed.getSession_description());
                i.putExtra("picurl",sessionFeed.getSession_image());
                i.putExtra("venue",sessionFeed.getS_venue());
                i.putExtra("coord",sessionFeed.getS_coordinator());
                i.putExtra("email",sessionFeed.getS_c_email());
                i.putExtra("phone",sessionFeed.getS_c_phone());
                i.putExtra("rp",sessionFeed.getResource_person());
                i.putExtra("rpd",sessionFeed.getRp_desg());
                i.putExtra("addr",sessionFeed.getAddress());
                i.putExtra("date_time",sessionFeed.getTime_and_date());
                i.putExtra("room",sessionFeed.getRoom());
                Pair<View, String> p1 = Pair.create((View)viewHolder.imageView, "sessionImage");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1);
                startActivity(i, options.toBundle());
            }
        });
        if (savedInstanceState != null) {
            newsFeedsList = savedInstanceState.getParcelableArrayList(SESSION_FEED);
        } else {

            DatabaseOperations_Session dop = new DatabaseOperations_Session(getActivity());
            newsFeedsList = dop.readSessionData(dop);
            if (newsFeedsList.isEmpty()) {
                if (isOnline())
                    new TaskLoadSessionFeed(this).execute();
            }
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    swipeRefreshLayout.setRefreshing(true);
                    new TaskLoadSessionFeed(SessionFragment.this).execute();

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
        recyclerSessionAdapter.setFeed(newsFeedsList);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SESSION_FEED,newsFeedsList);
    }

    @Override
    public void onSessionFeedLoaded(ArrayList<SessionFeed> newsFeedLists) {
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        recyclerSessionAdapter.setFeed(newsFeedLists);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void fabListener() {
        startActivity(new Intent(getActivity(),new_session_add.class));
    }
}
