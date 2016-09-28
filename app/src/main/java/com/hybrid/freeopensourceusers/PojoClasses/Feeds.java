package com.hybrid.freeopensourceusers.PojoClasses;

import android.support.annotation.NonNull;

import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations_Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monster on 25/9/16.
 */

public class Feeds {

////    private PostFeed postFeed;
////    private SessionFeed sessionFeed;
////
////
////    public Feeds(PostFeed postfeed, SessionFeed sessionfeed){
////        this.postFeed = postfeed;
////        this.sessionFeed = sessionfeed;
////    }
////
////
////    public Feeds(){
////
////    }
//
//    private final List<PostFeed> postFeeds;
//    private final List<SessionFeed> sessionFeeds;
//
//
//
//    public Feeds(@NonNull List<PostFeed> postFeeds, @NonNull List<SessionFeed> sessionFeeds) {
//        this.postFeeds = postFeeds;
//        this.sessionFeeds = sessionFeeds;
//    }
//
//   public List<SessionFeed> getSessionFeeds() {
//        return sessionFeeds;
//    }
//
//    public List<PostFeed> getPostFeeds() {
//        return postFeeds;
//    }
//
//    //If you're using this data in an adapter, knowing the total
//    //size of both lists might be helpful.
//    public int getTotalFeedCount() {
//        return postFeeds.size() + sessionFeeds.size();
//    }



    private PostFeed postFeed;
    private SessionFeed sessionFeed;

    public Feeds(PostFeed postfeed, SessionFeed sessionfeed){
        this.postFeed = postfeed;
        this.sessionFeed = sessionfeed;
    }

    public PostFeed getPostFeed() {
        return postFeed;
    }

    public boolean isSessionFeed(){
        return sessionFeed!=null;
    }

    public boolean isPostFeed(){
        return postFeed!=null;
    }

    public void setPostFeed(PostFeed postFeed) {
        this.postFeed = postFeed;
    }

    public SessionFeed getSessionFeed() {
        return sessionFeed;
    }

    public void setSessionFeed(SessionFeed sessionFeed) {
        this.sessionFeed = sessionFeed;
    }
}

