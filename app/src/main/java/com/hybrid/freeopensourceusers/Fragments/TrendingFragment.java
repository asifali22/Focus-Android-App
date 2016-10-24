package com.hybrid.freeopensourceusers.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.Activities.New_Post;
import com.hybrid.freeopensourceusers.Adapters.RecyclerTrendingAdapter;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Callback.FabClickListener;
import com.hybrid.freeopensourceusers.Callback.NotificationCallback;
import com.hybrid.freeopensourceusers.Callback.PostFeedLoadingListener;
import com.hybrid.freeopensourceusers.Callback.TabClickListener;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfile;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;


import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.Shape;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment implements PostFeedLoadingListener,FabClickListener,TabClickListener, NotificationCallback{

    private static final int REQUEST_CODE = 100;
    private static final String LIKE1 = "like1";
    private static final String DISLIKE1 = "dislike1";
    private static final String LIKE2 = "like2";
    private static final String DISLIKE2 = "dislike2";
    private ArrayList<PostFeed> newsFeedsList = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;

    private RecyclerView trendingRecyclerView;
    private RecyclerTrendingAdapter mRecyclerTrendingAdapter;
    private static final String POST_FEED = "post_feed";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog ;
    private CoordinatorLayout coordinatorLayout;



    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trending, container, false);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.trendingFragmentContainer);
        progressDialog = new ProgressDialog(getContext());
        sharedPrefManager = new SharedPrefManager(getContext());
        trendingRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshForTrendingPost);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trendingRecyclerView.setHasFixedSize(false);
        mRecyclerTrendingAdapter = new RecyclerTrendingAdapter(getActivity(), newsFeedsList);
        mRecyclerTrendingAdapter.setCallback(new RecyclerTrendingAdapter.ClickCallback() {
            @Override
            public void openProfile(PostFeed postFeed, RecyclerTrendingAdapter.ViewholderPostFeed viewHolder) {
                Intent myIntent = new Intent(getActivity(), UserProfile.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("UID",postFeed.getUid());
                myIntent.putExtra("NAME",postFeed.getUser_name());
                myIntent.putExtra("PIC", postFeed.getUser_pic());
                myIntent.putExtra("STATUS", postFeed.getUser_status());
                Pair<View, String> p1 = Pair.create((View)viewHolder.circleImageView, "profile");
                Pair<View, String> p2 = Pair.create((View)viewHolder.user_name, "user_name");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(myIntent, options.toBundle());
            }

            @Override
            public void likeDislike1(RecyclerTrendingAdapter.ViewholderPostFeed viewholderPostFeed){
                final MaterialShowcaseView.Builder materialShowcaseView = new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(viewholderPostFeed.getLikeDislikeContainer())
                        .setDismissText("GOT IT")
                        .setContentText("Like or Dislike !! How it works?\n\n1)" +
                                " Like and Dislike works as their literal meaning says\n\n" +
                                "2)" +
                                " Simultaneous like and dislike results in last action, and vise-versa")
                        .setShapePadding(0)
                        .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                        .singleUse(LIKE1) ;// provide a unique ID used to ensure it is only shown once

                coordinatorLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        materialShowcaseView.show();
                    }
                });
            }



            @Override
            public void startDialogForNewImage(String image) {

                MyTextDrawable myTextDrawable = new MyTextDrawable();
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View dialogMainView = factory.inflate(R.layout.fragment_image_post, null);

                final AlertDialog myDialog = new AlertDialog.Builder(getActivity()).create();

                ImageView mImageView = (ImageView) dialogMainView.findViewById(R.id.myImagePostContainer);

                myDialog.setView(dialogMainView);
                if (!image.isEmpty())
                    Glide.with(getActivity())
                            .load(image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.loading)
                            .dontAnimate()
                            .error(myTextDrawable.setTextDrawableForError("Error!"))
                            .into(mImageView);

                myDialog.show();

            }
        });
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mRecyclerTrendingAdapter);
        trendingRecyclerView.setAdapter( new ScaleInAnimationAdapter(alphaAdapter));
        if (savedInstanceState != null) {
            newsFeedsList = savedInstanceState.getParcelableArrayList(POST_FEED);
        } else {
            DatabaseOperations dop = new DatabaseOperations(getContext());
                    newsFeedsList = MyApplication.getDatabase().readPostData(dop);
                    if (newsFeedsList.isEmpty()) {
                        if (sharedPrefManager.isOnline()) {
                            progressDialog = ProgressDialog.show(getContext(),"Loading","Fetching data...",false,false);
                            new TaskLoadPostFeed(this).execute();
                        }
                    }

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (sharedPrefManager.isOnline()) {
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
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        mRecyclerTrendingAdapter.setFeed(newsFeedsLists);
    }



    @Override
    public void fabListener() {
        if(sharedPrefManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), New_Post.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else
            sharedPrefManager.showAlertDialog(getView());


    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                if (result.equals("true")){
                    swipeRefreshLayout.setRefreshing(true);
                    new TaskLoadPostFeed(this).execute();
                }
            }
            if(resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there is no result
            }
        }
    }

    @Override
    public void tabListener() {
        trendingRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void notifyUser() {
        if (sharedPrefManager.isOnline()) {
            swipeRefreshLayout.setRefreshing(true);
            new TaskLoadPostFeed(TrendingFragment.this).execute();

        }
        else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),"No Network",Toast.LENGTH_SHORT).show();
        }
    }
}
