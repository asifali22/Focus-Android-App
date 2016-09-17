package com.hybrid.freeopensourceusers.Callback;

import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;

import java.util.ArrayList;

/**
 * Created by monster on 10/8/16.
 */

public interface PostFeedLoadingListener {
     void onPostFeedLoaded(ArrayList<PostFeed> newsFeedLists);
}
