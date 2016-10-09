package com.hybrid.freeopensourceusers.PojoClasses;


/**
 * Created by monster on 25/9/16.
 */

public class Feeds {

    private PostFeed postFeed;
    private SessionFeed sessionFeed;
    private RecyclerHeader recyclerHeader;

    public Feeds(PostFeed postfeed, SessionFeed sessionfeed, RecyclerHeader recyclerHeader){
        this.postFeed = postfeed;
        this.recyclerHeader = recyclerHeader;
        this.sessionFeed = sessionfeed;

    }

    public RecyclerHeader getRecyclerHeader(){ return  recyclerHeader;}

    public boolean isRecyclerHeader(){ return  recyclerHeader!=null;}

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

