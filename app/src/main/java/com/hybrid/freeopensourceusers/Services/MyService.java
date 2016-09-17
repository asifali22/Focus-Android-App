//package com.hybrid.freeopensourceusers.Services;
//
//import com.hybrid.freeopensourceusers.Callback.PostFeedLoadingListener;
//import com.hybrid.freeopensourceusers.Logging.L;
//import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
//import com.hybrid.freeopensourceusers.Task.TaskLoadPostFeed;
//
//import java.util.ArrayList;
//
//
//import me.tatarka.support.job.JobParameters;
//import me.tatarka.support.job.JobService;
//
///**
// * Created by monster on 9/8/16.
// */
//
//public class MyService extends JobService implements PostFeedLoadingListener{
//    private JobParameters jobParameters;
//    @Override
//    public boolean onStartJob(JobParameters jobParameters) {
//        this.jobParameters = jobParameters;
//        new TaskLoadPostFeed(this).execute();
//        return true;
//    }
//
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        return false;
//    }
//
//    @Override
//    public void onPostFeedLoaded(ArrayList<PostFeed> newsFeedLists) {
//        jobFinished(jobParameters, false);
//    }
//}
