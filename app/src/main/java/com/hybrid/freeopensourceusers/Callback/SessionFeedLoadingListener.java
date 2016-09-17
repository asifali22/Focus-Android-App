package com.hybrid.freeopensourceusers.Callback;

/**
 * Created by adarsh on 6/9/16.
 */

import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;

import java.util.ArrayList;

/**
 * Created by monster on 10/8/16.
 */

public interface SessionFeedLoadingListener {
    void onSessionFeedLoaded(ArrayList<SessionFeed> newsFeedLists);
}


