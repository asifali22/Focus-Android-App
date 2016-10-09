package com.hybrid.freeopensourceusers.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hybrid.freeopensourceusers.Logging.L;
import com.hybrid.freeopensourceusers.PojoClasses.Feeds;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;
import com.hybrid.freeopensourceusers.PojoClasses.SessionFeed;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by adarsh on 7/9/16.
 */

public class DatabaseOperations_Session extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "session_details";
    private static final int DATABASE_VERSION=1;
    public Context context;
    private Session_Class session_class= new Session_Class();
    private String create_session="CREATE TABLE IF NOT EXISTS " + session_class.getTable_name() + "(" +
            session_class.getS_title() + " VARCHAR(50)," +
            session_class.getS_description() + " VARCHAR(200)," +
            session_class.getS_picurl() + " VARCHAR(200)," +
            session_class.getSession_id() + " INTEGER,"+
            session_class.getS_venue()+" VARCHAR(100),"+
            session_class.getS_coordinator()+" VARCHAR(100),"+
            session_class.getS_c_email()+" VARCHAR(100),"+
            session_class.getS_c_phone()+" VARCHAR(100),"+
            session_class.getResource_person()+" VARCHAR(100),"+
            session_class.getRp_desg()+" VARCHAR(100),"+
            session_class.getDate_time()+" VARCHAR(100),"+
            session_class.getAddress()+" VARCHAR(100),"+
            session_class.getRoom()+" VARCHAR(20),"+
            session_class.getDosp()+" DATE,"+
            session_class.getUser_name()+" VARCHAR(100),"+
            session_class.getUid()+" INTEGER,"+
            session_class.getUser_pic()+" VARCHAR(400),"+
            session_class.getUser_status()+" VARCHAR(100));";

    public DatabaseOperations_Session(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(create_session);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void insertSessions(DatabaseOperations_Session dop, ArrayList<SessionFeed> newsFeedList, boolean clearPrevious){

        SQLiteDatabase mDatabase = dop.getWritableDatabase();

        if (clearPrevious) {
            if(isOnline())
                mDatabase.execSQL("delete from " + session_class.getTable_name());
        }
        ContentValues cv = new ContentValues();


        for (int i = 0; i < newsFeedList.size(); i++) {
            SessionFeed currentPostFeed = newsFeedList.get(i);
            cv.put(session_class.getS_title(), currentPostFeed.getSession_title());
            cv.put(session_class.getS_description(), currentPostFeed.getSession_description());
            cv.put(session_class.getS_picurl(), currentPostFeed.getSession_image());
            cv.put(session_class.getSession_id(),currentPostFeed.getSession_id());
            cv.put(session_class.getS_venue(),currentPostFeed.getS_venue());
            cv.put(session_class.getS_coordinator(),currentPostFeed.getS_coordinator());
            cv.put(session_class.getS_c_email(),currentPostFeed.getS_c_email());
            cv.put(session_class.getS_c_phone(),currentPostFeed.getS_c_phone());
            cv.put(session_class.getResource_person(),currentPostFeed.getResource_person());
            cv.put(session_class.getRp_desg(),currentPostFeed.getRp_desg());
            cv.put(session_class.getDate_time(),currentPostFeed.getTime_and_date());
            cv.put(session_class.getAddress(),currentPostFeed.getAddress());
            cv.put(session_class.getRoom(),currentPostFeed.getRoom());
            cv.put(session_class.getUser_name(),currentPostFeed.getUser_name());
            cv.put(session_class.getUser_pic(),currentPostFeed.getUser_pic());
            cv.put(session_class.getUser_status(),currentPostFeed.getUser_status());
            cv.put(session_class.getUid(),currentPostFeed.getUid());
            long dateOfPost =  currentPostFeed.getDosp().getTime();
            cv.put(session_class.getDosp(),dateOfPost);
            mDatabase.insert(session_class.getTable_name(), null, cv);
        }
    }


    public ArrayList<SessionFeed> readSessionForSearch(String searchText, DatabaseOperations_Session dop){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        ArrayList<SessionFeed> newsFeedList = new ArrayList<>();
        String query ="select * from " + session_class.getTable_name() + " where " + session_class.getS_title() +" LIKE '%"+searchText+"%' OR "+ session_class.getS_description() + " LIKE '%"+searchText+"%'"+" order by Dosp desc " +";";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new object and retrieve the data from the cursor to be stored in this object
                SessionFeed sessionFeed = new SessionFeed();
                sessionFeed.setSession_id(cursor.getInt(cursor.getColumnIndex(session_class.getSession_id())));
                sessionFeed.setSession_title(cursor.getString(cursor.getColumnIndex(session_class.getS_title())));
                sessionFeed.setSession_description(cursor.getString(cursor.getColumnIndex(session_class.getS_description())));
                sessionFeed.setSession_image(cursor.getString(cursor.getColumnIndex(session_class.getS_picurl())));
                sessionFeed.setS_venue(cursor.getString(cursor.getColumnIndex(session_class.getS_venue())));
                sessionFeed.setS_coordinator(cursor.getString(cursor.getColumnIndex(session_class.getS_coordinator())));
                sessionFeed.setS_c_email(cursor.getString(cursor.getColumnIndex(session_class.getS_c_email())));
                sessionFeed.setS_c_phone(cursor.getString(cursor.getColumnIndex(session_class.getS_c_phone())));
                sessionFeed.setResource_person(cursor.getString(cursor.getColumnIndex(session_class.getResource_person())));
                sessionFeed.setRp_desg(cursor.getString(cursor.getColumnIndex(session_class.getRp_desg())));
                sessionFeed.setTime_and_date(cursor.getString(cursor.getColumnIndex(session_class.getDate_time())));
                sessionFeed.setAddress(cursor.getString(cursor.getColumnIndex(session_class.getAddress())));
                sessionFeed.setRoom(cursor.getString(cursor.getColumnIndex(session_class.getRoom())));
                long dateOfPost = cursor.getLong(cursor.getColumnIndex(session_class.getDosp()));
                sessionFeed.setDosp(new Date(dateOfPost));
                sessionFeed.setUser_name(cursor.getString(cursor.getColumnIndex(session_class.getUser_name())));
                sessionFeed.setUser_pic(cursor.getString(cursor.getColumnIndex(session_class.getUser_pic())));
                sessionFeed.setUser_status(cursor.getString(cursor.getColumnIndex(session_class.getUser_status())));
                sessionFeed.setUid(cursor.getInt(cursor.getColumnIndex(session_class.getUid())));
                newsFeedList.add(sessionFeed);
            } while (cursor.moveToNext());
        }


        return newsFeedList;
    }
    public SessionFeed readBySessionId(DatabaseOperations_Session dop,int id){
        SessionFeed sessionFeed = new SessionFeed() ;
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        String query ="select * from " + session_class.getTable_name()+"where "+session_class.getSession_id()+"="+id+";";
        Cursor c = sqLiteDatabase.rawQuery(query,null);
        c.moveToFirst();
        if(c.getCount()==0)
            Log.e("ADARSH","AGAIN ZERO");
        else
            Log.e("ADARSH",c.getString(c.getColumnIndex("count(*)")));
        Cursor cursor = sqLiteDatabase.query(session_class.getTable_name(),null,session_class.getSession_id()+"="+id,null,null,null,null);

        if(cursor!=null&&cursor.moveToFirst()){

                sessionFeed.setSession_id(cursor.getInt(cursor.getColumnIndex(session_class.getSession_id())));
                sessionFeed.setSession_title(cursor.getString(cursor.getColumnIndex(session_class.getS_title())));
                sessionFeed.setSession_description(cursor.getString(cursor.getColumnIndex(session_class.getS_description())));
                sessionFeed.setSession_image(cursor.getString(cursor.getColumnIndex(session_class.getS_picurl())));
                sessionFeed.setS_venue(cursor.getString(cursor.getColumnIndex(session_class.getS_venue())));
                sessionFeed.setS_coordinator(cursor.getString(cursor.getColumnIndex(session_class.getS_coordinator())));
                sessionFeed.setS_c_email(cursor.getString(cursor.getColumnIndex(session_class.getS_c_email())));
                sessionFeed.setS_c_phone(cursor.getString(cursor.getColumnIndex(session_class.getS_c_phone())));
                sessionFeed.setResource_person(cursor.getString(cursor.getColumnIndex(session_class.getResource_person())));
                sessionFeed.setRp_desg(cursor.getString(cursor.getColumnIndex(session_class.getRp_desg())));
                sessionFeed.setTime_and_date(cursor.getString(cursor.getColumnIndex(session_class.getDate_time())));
                sessionFeed.setAddress(cursor.getString(cursor.getColumnIndex(session_class.getAddress())));
                sessionFeed.setRoom(cursor.getString(cursor.getColumnIndex(session_class.getRoom())));

        }
        return sessionFeed;


    }
    public ArrayList<SessionFeed> readSessionData(DatabaseOperations_Session dop){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        ArrayList<SessionFeed> newsFeedList = new ArrayList<>();
        String query ="select * from " + session_class.getTable_name()+";";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new movie object and retrieve the data from the cursor to be stored in this movie object
                SessionFeed sessionFeed = new SessionFeed();
                sessionFeed.setSession_id(cursor.getInt(cursor.getColumnIndex(session_class.getSession_id())));
                sessionFeed.setSession_title(cursor.getString(cursor.getColumnIndex(session_class.getS_title())));
                sessionFeed.setSession_description(cursor.getString(cursor.getColumnIndex(session_class.getS_description())));
                sessionFeed.setSession_image(cursor.getString(cursor.getColumnIndex(session_class.getS_picurl())));
                sessionFeed.setS_venue(cursor.getString(cursor.getColumnIndex(session_class.getS_venue())));
                sessionFeed.setS_coordinator(cursor.getString(cursor.getColumnIndex(session_class.getS_coordinator())));
                sessionFeed.setS_c_email(cursor.getString(cursor.getColumnIndex(session_class.getS_c_email())));
                sessionFeed.setS_c_phone(cursor.getString(cursor.getColumnIndex(session_class.getS_c_phone())));
                sessionFeed.setResource_person(cursor.getString(cursor.getColumnIndex(session_class.getResource_person())));
                sessionFeed.setRp_desg(cursor.getString(cursor.getColumnIndex(session_class.getRp_desg())));
                sessionFeed.setTime_and_date(cursor.getString(cursor.getColumnIndex(session_class.getDate_time())));
                sessionFeed.setAddress(cursor.getString(cursor.getColumnIndex(session_class.getAddress())));
                sessionFeed.setRoom(cursor.getString(cursor.getColumnIndex(session_class.getRoom())));
                long dateOfPost = cursor.getLong(cursor.getColumnIndex(session_class.getDosp()));
                sessionFeed.setDosp(new Date(dateOfPost));
                sessionFeed.setUser_name(cursor.getString(cursor.getColumnIndex(session_class.getUser_name())));
                sessionFeed.setUser_pic(cursor.getString(cursor.getColumnIndex(session_class.getUser_pic())));
                sessionFeed.setUser_status(cursor.getString(cursor.getColumnIndex(session_class.getUser_status())));
                sessionFeed.setUid(cursor.getInt(cursor.getColumnIndex(session_class.getUid())));
                newsFeedList.add(sessionFeed);

            }while (cursor.moveToNext());
        }
        return newsFeedList;
    }

    public ArrayList<SessionFeed> readSessionForUserProfile(int userID, DatabaseOperations_Session databaseOperations_session) {

        SQLiteDatabase sqLiteDatabase = databaseOperations_session.getReadableDatabase();
        ArrayList<SessionFeed> newsFeedList = new ArrayList<>();
        String query ="select * from " + session_class.getTable_name() + " where " + session_class.getUid()+ " = " + userID +" order by Dosp desc ;";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new object and retrieve the data from the cursor to be stored in this object
                SessionFeed sessionFeed = new SessionFeed();
                sessionFeed.setSession_id(cursor.getInt(cursor.getColumnIndex(session_class.getSession_id())));
                sessionFeed.setSession_title(cursor.getString(cursor.getColumnIndex(session_class.getS_title())));
                sessionFeed.setSession_description(cursor.getString(cursor.getColumnIndex(session_class.getS_description())));
                sessionFeed.setSession_image(cursor.getString(cursor.getColumnIndex(session_class.getS_picurl())));
                sessionFeed.setS_venue(cursor.getString(cursor.getColumnIndex(session_class.getS_venue())));
                sessionFeed.setS_coordinator(cursor.getString(cursor.getColumnIndex(session_class.getS_coordinator())));
                sessionFeed.setS_c_email(cursor.getString(cursor.getColumnIndex(session_class.getS_c_email())));
                sessionFeed.setS_c_phone(cursor.getString(cursor.getColumnIndex(session_class.getS_c_phone())));
                sessionFeed.setResource_person(cursor.getString(cursor.getColumnIndex(session_class.getResource_person())));
                sessionFeed.setRp_desg(cursor.getString(cursor.getColumnIndex(session_class.getRp_desg())));
                sessionFeed.setTime_and_date(cursor.getString(cursor.getColumnIndex(session_class.getDate_time())));
                sessionFeed.setAddress(cursor.getString(cursor.getColumnIndex(session_class.getAddress())));
                sessionFeed.setRoom(cursor.getString(cursor.getColumnIndex(session_class.getRoom())));
                long dateOfPost = cursor.getLong(cursor.getColumnIndex(session_class.getDosp()));
                sessionFeed.setDosp(new Date(dateOfPost));
                sessionFeed.setUser_name(cursor.getString(cursor.getColumnIndex(session_class.getUser_name())));
                sessionFeed.setUser_pic(cursor.getString(cursor.getColumnIndex(session_class.getUser_pic())));
                sessionFeed.setUser_status(cursor.getString(cursor.getColumnIndex(session_class.getUser_status())));
                sessionFeed.setUid(cursor.getInt(cursor.getColumnIndex(session_class.getUid())));
                newsFeedList.add(sessionFeed);
            } while (cursor.moveToNext());
        }


        return newsFeedList;

    }

    public class Session_Class{
        String table_name="session";
        String session_id="session_id";
        String s_title="s_title";
        String s_description="s_description";
        String s_picurl="s_picurl";
        String s_venue="s_venue";
        String s_coordinator="s_coordinator";
        String s_c_email="s_c_email";
        String s_c_phone="s_c_phone";
        String resource_person="resource_person";
        String rp_desg="rp_desg";
        String date_time="date_time";
        String address="address";
        String room="room";
        String Dosp="Dosp";
        String user_name="user_name";
        String user_status="user_status";
        String uid="uid";
        String user_pic="user_pic";

        public String getUser_name() {
            return user_name;
        }

        public String getUser_status() {
            return user_status;
        }

        public String getUid() {
            return uid;
        }

        public String getUser_pic() {
            return user_pic;
        }

        public String getDosp() {
            return Dosp;
        }

        public String getResource_person() {
            return resource_person;
        }

        public String getRp_desg() {
            return rp_desg;
        }

        public String getDate_time() {
            return date_time;
        }

        public String getAddress() {
            return address;
        }

        public String getRoom() {
            return room;
        }

        public String getS_venue() {
            return s_venue;
        }

        public String getS_coordinator() {
            return s_coordinator;
        }

        public String getS_c_email() {
            return s_c_email;
        }

        public String getS_c_phone() {
            return s_c_phone;
        }

        public String getTable_name() {
            return table_name;
        }

        public String getSession_id() {
            return session_id;
        }

        public String getS_title() {
            return s_title;
        }

        public String getS_description() {
            return s_description;
        }

        public String getS_picurl() {
            return s_picurl;
        }
    }




}



/*

public ArrayList<Feeds> readAllPostForSearch(String searchText, DatabaseOperations dop){
//get postfeeds
ArrayList<PostFeed> postfeeds = readPostForSearch(searchText, dop);
//get sessionfeeds
ArrayList<SessionFeed> sessionfeeds = readSessionForSearch(searchText, dop);
ArrayList<Feeds> feeds = new ArrayList<Feeds>;

//postfeeds to feeds
for(int i = 0; i<postfeeds.size(); i++)
feeds.add(new Feeds(postfeeds.get(i), null))

//sessionfeeds to feeds
for(int i = 0; i<sessionfeeds.size(); i++)
feeds.add(new Feeds(null, sessionfeeds))

return feeds

}
 */


/*

postFeed = feeds.get(position).postFeed;
sessionFeed = feeds.get(position).sessionFeed;
then a check which one is null
if(postFeed == null) {
//use sessionfeed
}
else {
//use postFeed
}
 */


/*

class FeedHolder implements Parcelable {

    private final List<PostFeed> postFeeds;
    private final List<SessionFeed> sessionFeeds;

    FeedHolder(@NonNull List<PostFeed> postFeeds,@NonNull List<SessionFeed> sessionFeeds) {
        this.postFeeds = postFeeds;
        this.sessionFeeds = sessionFeeds;
    }

    List<SessionFeed> getSessionFeeds() {
        return sessionFeeds;
    }

    List<PostFeed> getPostFeeds() {
        return postFeeds;
    }

    //If you're using this data in an adapter, knowing the total
    //size of both lists might be helpful.
    int getTotalFeedCount() {
        return postFeeds.size() + sessionFeeds.size();
    }

}

//...

List<SessionFeed> sessionFeeds = //whatever you do to populate your
//List of SessionFeeds...
List<PostFeed> postFeeds = //Same again.
FeedHolder feedHolder = FeedHolder(postFeeds, sessionFeeds);
 */
