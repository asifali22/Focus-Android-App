package com.hybrid.freeopensourceusers.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hybrid.freeopensourceusers.Activities.new_session_add;
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

import java.util.ArrayList;


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
        recyclerView.setAdapter(recyclerSessionAdapter);
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
