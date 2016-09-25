package com.hybrid.freeopensourceusers.PojoClasses;

import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;

import java.util.ArrayList;

/**
 * Created by monster on 25/9/16.
 */

public class Feeds {

    private PostFeed postFeed;
    private SessionFeed sessionFeed;

    DatabaseOperations dop = new DatabaseOperations(MyApplication.getAppContext());
    ArrayList<PostFeed> postFeedArrayList = new ArrayList<>();
    ArrayList<SessionFeed> sessionFeedArrayList = new ArrayList<>();

    public Feeds(PostFeed postfeed, SessionFeed sessionfeed){
        this.postFeed = postfeed;
        this.sessionFeed = sessionfeed;
    }





}
