package com.hybrid.freeopensourceusers.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.hybrid.freeopensourceusers.Activities.FirstActivity;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Logging.L;
import com.hybrid.freeopensourceusers.PojoClasses.CommentFeed;
import com.hybrid.freeopensourceusers.PojoClasses.Likes;
import com.hybrid.freeopensourceusers.PojoClasses.PostFeed;

import org.cryse.widget.persistentsearch.SearchItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by monster on 4/7/16.
 */
public class DatabaseOperations extends SQLiteOpenHelper {

    public DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd 'at' h:mm a");
    Context ctx;
    Search_Suggestion_Const search_suggestion_const = new Search_Suggestion_Const();
    Html_Test_Const html_test_const = new Html_Test_Const();
    User_Const user_const = new User_Const();
    Likes_Const likes_const = new Likes_Const();
    Comments_Const comments_const = new Comments_Const();
    String create_html_test = "CREATE TABLE IF NOT EXISTS " + html_test_const.getTable_name() + "(" +
            html_test_const.getTitle() + " VARCHAR(50)," +
            html_test_const.getLink() + " VARCHAR(200)," +
            html_test_const.getDescription() + " VARCHAR(200)," +
            html_test_const.getDate() + " DATE," +
            html_test_const.getUser_name() + " VARCHAR(100)," +
            html_test_const.getSr_key() + " INTEGER PRIMARY KEY," +
            html_test_const.getUp_down() + " INTEGER," +
            html_test_const.getComment_count() + " INTEGER," +
            html_test_const.getUid() + " INTEGER," +
            html_test_const.getPost_pic() + " VARCHAR(200),"+
            html_test_const.getUser_pic()+" VARCHAR(200),"+
            html_test_const.getUser_status()+ " VARCHAR(300));";

    String create_user = "CREATE TABLE " + user_const.getTable_name() + "(" +
            user_const.getUser_id() + " INTEGER PRIMARY KEY," +
            user_const.getUser_name() + " VARCHAR(30)," +
            user_const.getUser_email() + " VARCHAR(50)," +
            user_const.getUser_password() + " VARCHAR(50)," +
            user_const.getUser_status() + " VARCHAR(50)," +
            user_const.getUser_description() + " VARCHAR(200)," +
            user_const.getUser_pic() + " VARCHAR(200));";
    String create_like = "CREATE TABLE " + likes_const.getTable_name() + "(" +
            likes_const.getUser_id() + " INTEGER," +
            likes_const.getPid() + " INTEGER," +
            likes_const.getFlag() + " INTEGER," +
            likes_const.getFlagd() + " INTEGER);";
    String create_comment = "CREATE TABLE " + comments_const.getTable_name() + "(" +
            comments_const.getComment_id() + " INTEGER," +
            comments_const.getUid() + " INTEGER," +
            comments_const.getPid() + " INTEGER," +
            comments_const.getComment() + " VARCHAR(200),"+
            comments_const.getDoc() + " DATE, " +
            comments_const.getUser_name() + " VARCHAR(200),"+
            html_test_const.getUser_pic()+" VARCHAR(200));";

    String create_search = "CREATE TABLE " + search_suggestion_const.getTable_name() + "(" +
            search_suggestion_const.getSearchSLNo() + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            search_suggestion_const.getLastSearch() + " VARCHAR(100));";



    // Database Version
    private static final int DATABASE_VERSION = 1;


    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    public DatabaseOperations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(create_html_test);
        sqLiteDatabase.execSQL(create_user);
        sqLiteDatabase.execSQL(create_like);
        sqLiteDatabase.execSQL(create_comment);
        sqLiteDatabase.execSQL(create_search);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void delete_post(DatabaseOperations databaseOperations) {
        SQLiteDatabase sqLiteDatabase = databaseOperations.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + html_test_const.getTable_name());
    }

    public void delete_all(DatabaseOperations databaseOperations) {
        SQLiteDatabase sqLiteDatabase = databaseOperations.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + search_suggestion_const.getTable_name());
        sqLiteDatabase.execSQL("delete from " + html_test_const.getTable_name());
        sqLiteDatabase.execSQL("delete from " + user_const.getTable_name());
        sqLiteDatabase.execSQL("delete from " + likes_const.getTable_name());
        sqLiteDatabase.execSQL("delete from " + comments_const.getTable_name());
    }
    public void insertLikes(DatabaseOperations dop, ArrayList<Likes> likes,boolean clearPrevious){
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
        if(clearPrevious){
            if(isOnline())
                sqLiteDatabase.execSQL("delete from "+likes_const.getTable_name());
        }
        ContentValues cv = new ContentValues();
        for (int i = 0; i < likes.size(); i++) {
            Likes l = likes.get(i);
            /*cv.put(likes_const.getUser_id(),l.getUser_id());
            cv.put(likes_const.getPid(),l.getPid());
            cv.put(likes_const.getFlag(),l.getFlag());
            cv.put(likes_const.getFlagd(),l.getFlagd());
            sqLiteDatabase.insert(likes_const.getTable_name(), null, cv);*/
            sqLiteDatabase.execSQL("insert into "+likes_const.getTable_name()+" values("+l.getUser_id()+
            ","+l.getPid()+","+l.getFlag()+","+l.getFlagd()+")");
        }
    }
    public int getfbypid(DatabaseOperations dop,int pid){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        Cursor cursor1 = sqLiteDatabase.rawQuery("select "+likes_const.getFlag()+" from "+likes_const.getTable_name()+" where "+likes_const.getPid()+"="+pid,null);
        cursor1.moveToFirst();
        Cursor cursor2 = sqLiteDatabase.rawQuery("select "+likes_const.getFlagd()+" from "+likes_const.getTable_name()+" where "+likes_const.getPid()+"="+pid,null);
        cursor2.moveToFirst();
        int flag=0,flagd=0;
        if(cursor1.getCount()!=0)
        flag = cursor1.getInt(cursor1.getColumnIndex(likes_const.getFlag()));
        if(cursor2.getCount()!=0)
            flagd = cursor2.getInt(cursor2.getColumnIndex(likes_const.getFlagd()));
        sqLiteDatabase.close();
        if(cursor1.getCount()==0&&cursor2.getCount()==0)
            return -1;
        if(flag==1&&flagd==0)
            return 2;
        if(flag==0&&flagd==1)
            return 1;
        else if(flag==0&&flagd==0)
            return 0;
        return 0;
    }
    public void setflagandflagd(DatabaseOperations dop,int flag,int flagd,int pid){
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
        sqLiteDatabase.execSQL("update "+likes_const.getTable_name()+" set flag="+flag+" where "+likes_const.getPid()+"="+pid);
        sqLiteDatabase.execSQL("update "+likes_const.getTable_name()+" set flagd="+flagd+" where "+likes_const.getPid()+"="+pid);
    }

    public void insertPosts(DatabaseOperations dop, ArrayList<PostFeed> newsFeedList, boolean clearPrevious){

        SQLiteDatabase mDatabase = dop.getWritableDatabase();

        if (clearPrevious) {
            if(isOnline())
            mDatabase.execSQL("delete from " + html_test_const.getTable_name());
        }
        ContentValues cv = new ContentValues();

        for (int i = 0; i < newsFeedList.size(); i++) {
            PostFeed currentPostFeed = newsFeedList.get(i);
            cv.put(html_test_const.getTitle(), currentPostFeed.getTitle());
            cv.put(html_test_const.getLink(), currentPostFeed.getLink());
            cv.put(html_test_const.getDescription(), currentPostFeed.getDescription());
            long dateOfPost =  currentPostFeed.getDop().getTime();
            cv.put(html_test_const.getDate(),dateOfPost);
            cv.put(html_test_const.getUser_name(), currentPostFeed.getUser_name());
            cv.put(html_test_const.getSr_key(), currentPostFeed.getPid());
            cv.put(html_test_const.getUp_down(), currentPostFeed.getUp());
            cv.put(html_test_const.getComment_count(), currentPostFeed.getComment_count());
            cv.put(html_test_const.getUid(), currentPostFeed.getUid());
            cv.put(html_test_const.getPost_pic(), currentPostFeed.getPostPicUrl());
            cv.put(html_test_const.getUser_pic(),currentPostFeed.getUser_pic());
            cv.put(html_test_const.getUser_status(), currentPostFeed.getUser_status());
            mDatabase.insert(html_test_const.getTable_name(), null, cv);



        }

    }


    public ArrayList<PostFeed> readPostData(DatabaseOperations dop){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        ArrayList<PostFeed> newsFeedList = new ArrayList<>();
        //get a list of columns to be retrieved, we need all of them
        String columns[] = {html_test_const.getTitle(),
                html_test_const.getLink(),
                html_test_const.getDescription(),
                html_test_const.getDate(),
                html_test_const.getUser_name(),
                html_test_const.getSr_key(),
                html_test_const.getUp_down(),
                html_test_const.getComment_count(),
                html_test_const.getUid(),
                html_test_const.getPost_pic(),
                html_test_const.getUser_pic(),
                html_test_const.getUser_status()};

        Cursor cursor = sqLiteDatabase.query(html_test_const.getTable_name(), columns, null, null, null, null, html_test_const.getDate()+" desc");
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new movie object and retrieve the data from the cursor to be stored in this movie object
                PostFeed postFeed = new PostFeed();
                postFeed.setTitle(cursor.getString(cursor.getColumnIndex(html_test_const.getTitle())));
                postFeed.setLink(cursor.getString(cursor.getColumnIndex(html_test_const.getLink())));
                postFeed.setDescription(cursor.getString(cursor.getColumnIndex(html_test_const.getDescription())));
                long dateOfPost = cursor.getLong(cursor.getColumnIndex(html_test_const.getDate()));
                postFeed.setDop(new java.sql.Date(dateOfPost));
                postFeed.setUser_name(cursor.getString(cursor.getColumnIndex(html_test_const.getUser_name())));
                postFeed.setPid(cursor.getInt(cursor.getColumnIndex(html_test_const.getSr_key())));
                postFeed.setUp(cursor.getInt(cursor.getColumnIndex(html_test_const.getUp_down())));
                postFeed.setComment_count(cursor.getInt(cursor.getColumnIndex(html_test_const.getComment_count())));
                postFeed.setUid(cursor.getInt(cursor.getColumnIndex(html_test_const.getUid())));
                postFeed.setPostPicUrl(cursor.getString(cursor.getColumnIndex(html_test_const.getPost_pic())));
                postFeed.setUser_pic(cursor.getString(cursor.getColumnIndex(html_test_const.getUser_pic())));
                postFeed.setUser_status(cursor.getString(cursor.getColumnIndex(html_test_const.getUser_status())));
                newsFeedList.add(postFeed);

            }while (cursor.moveToNext());


        }

        return newsFeedList;
    }

    public ArrayList<CommentFeed> readCommentDataForPost(String pid, DatabaseOperations dop){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        ArrayList<CommentFeed> newsFeedList = new ArrayList<>();



        String query ="select * from " + comments_const.getTable_name() + " where " + comments_const.getPid() +" = " + pid +" order by doc desc " +";";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new object and retrieve the data from the cursor to be stored in this object
                CommentFeed commentFeed = new CommentFeed();
                commentFeed.setComment_id(cursor.getInt(cursor.getColumnIndex(comments_const.getComment_id())));
                commentFeed.setUser_id(cursor.getInt(cursor.getColumnIndex(comments_const.getUid())));
                commentFeed.setPid(cursor.getInt(cursor.getColumnIndex(comments_const.getPid())));
                commentFeed.setComment(cursor.getString(cursor.getColumnIndex(comments_const.getComment())));
                long dateOfcomment = cursor.getLong(cursor.getColumnIndex(comments_const.getDoc()));
                Log.e("Date from sql:", dateOfcomment+"");
                commentFeed.setDoc(new java.sql.Date(dateOfcomment));
                commentFeed.setUser_name(cursor.getString(cursor.getColumnIndex(comments_const.getUser_name())));
                commentFeed.setUser_pic(cursor.getString(cursor.getColumnIndex(comments_const.getUser_pic())));

                newsFeedList.add(commentFeed);

            }while (cursor.moveToNext());


        }

        return newsFeedList;
    }

    public ArrayList<SearchItem> readSearchSuggestion(DatabaseOperations dop){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        ArrayList<SearchItem> newsFeedList = new ArrayList<>();
        String query = "select " + search_suggestion_const.getLastSearch() + " from " + search_suggestion_const.getTable_name()+  " order by " + search_suggestion_const.getSearchSLNo() + " desc " +";";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                SearchItem searchItem = new SearchItem(cursor.getString(cursor.getColumnIndex(search_suggestion_const.getLastSearch())),
                        cursor.getString(cursor.getColumnIndex(search_suggestion_const.getLastSearch())),
                        SearchItem.TYPE_SEARCH_ITEM_HISTORY);
                newsFeedList.add(searchItem);
            }while (cursor.moveToNext());
        }

        return newsFeedList;

    }

    public ArrayList<PostFeed> readPostForSearch(String searchText, DatabaseOperations dop){
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        ArrayList<PostFeed> newsFeedList = new ArrayList<>();
        String query ="select * from " + html_test_const.getTable_name() + " where " + html_test_const.getTitle() +" LIKE '%"+searchText+"%' OR "+ html_test_const.getDescription() + " LIKE '%"+searchText+"%'"+" order by date desc " +";";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new object and retrieve the data from the cursor to be stored in this object
                PostFeed postFeed = new PostFeed();
                postFeed.setTitle(cursor.getString(cursor.getColumnIndex(html_test_const.getTitle())));
                postFeed.setLink(cursor.getString(cursor.getColumnIndex(html_test_const.getLink())));
                postFeed.setDescription(cursor.getString(cursor.getColumnIndex(html_test_const.getDescription())));
                long dateOfPost = cursor.getLong(cursor.getColumnIndex(html_test_const.getDate()));
                postFeed.setDop(new java.sql.Date(dateOfPost));
                postFeed.setUser_name(cursor.getString(cursor.getColumnIndex(html_test_const.getUser_name())));
                postFeed.setPid(cursor.getInt(cursor.getColumnIndex(html_test_const.getSr_key())));
                postFeed.setUp(cursor.getInt(cursor.getColumnIndex(html_test_const.getUp_down())));
                postFeed.setComment_count(cursor.getInt(cursor.getColumnIndex(html_test_const.getComment_count())));
                postFeed.setUid(cursor.getInt(cursor.getColumnIndex(html_test_const.getUid())));
                postFeed.setPostPicUrl(cursor.getString(cursor.getColumnIndex(html_test_const.getPost_pic())));
                postFeed.setUser_pic(cursor.getString(cursor.getColumnIndex(html_test_const.getUser_pic())));
                postFeed.setUser_status(cursor.getString(cursor.getColumnIndex(html_test_const.getUser_status())));
                newsFeedList.add(postFeed);

            } while (cursor.moveToNext());
        }

        return newsFeedList;
    }

    public void putInfo_Comment(DatabaseOperations dop, int comment_id, int uid, int pid, String comment, long doc, String user_name, String user_pic) {
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(comments_const.getComment_id(), comment_id);
        cv.put(comments_const.getUid(), uid);
        cv.put(comments_const.getPid(), pid);
        cv.put(comments_const.getComment(), comment);
        cv.put(comments_const.getDoc(),doc);
        cv.put(comments_const.getUser_name(), user_name);
        cv.put(comments_const.getUser_pic(), user_pic);
        sqLiteDatabase.insert(comments_const.getTable_name(), null, cv);
    }

    public void insertSuggestionForSearch(DatabaseOperations dop, String suggestion){

        String searchSuggest = suggestion.toLowerCase();
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();

        String Query ="insert into " + search_suggestion_const.getTable_name() + "("+ search_suggestion_const.getLastSearch()+") " + " select " + "'"+searchSuggest+ "'"  + " where NOT EXISTS( " + "select 1 from search_suggestion where lastSearch = '"+searchSuggest +"');";
        sqLiteDatabase.execSQL(Query);

    }

    public void putInfo_Like(DatabaseOperations dop, int uid, int pid, int flag, int flagd) {
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(likes_const.getUser_id(), uid);
        cv.put(likes_const.getPid(), pid);
        cv.put(likes_const.getFlag(), flag);
        cv.put(likes_const.getFlagd(), flagd);
        sqLiteDatabase.insert(likes_const.getTable_name(), null, cv);
    }

    public void putInfo_Users(DatabaseOperations dop, String uname, String uemail, String upass, String ustatus, String udesc, String upic) {
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(user_const.getUser_name(), uname);
        cv.put(user_const.getUser_email(), uemail);
        cv.put(user_const.getUser_password(), upass);
        cv.put(user_const.getUser_status(), ustatus);
        cv.put(user_const.getUser_description(), udesc);
        cv.put(user_const.getUser_pic(), upic);
        sqLiteDatabase.insert(user_const.getTable_name(), null, cv);
    }

    public void putInfo_HtmlTest(DatabaseOperations dp, String title, String link, String description, String dop
            , String user_name, int sr_key, int up, int comment_count, int uid, String post_pic,String user_pic) {
        SQLiteDatabase sqLiteDatabase = dp.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(html_test_const.getTitle(), title);
        cv.put(html_test_const.getLink(), link);
        cv.put(html_test_const.getDescription(), description);
        cv.put(html_test_const.getDate(), dop);
        cv.put(html_test_const.getUser_name(), user_name);
        cv.put(html_test_const.getSr_key(), sr_key);
        cv.put(html_test_const.getUp_down(), up);
        cv.put(html_test_const.getComment_count(), comment_count);
        cv.put(html_test_const.getUid(), uid);
        cv.put(html_test_const.getPost_pic(), post_pic);
        cv.put(html_test_const.getUser_pic(),user_pic);
        sqLiteDatabase.insert(html_test_const.getTable_name(), null, cv);
    }

    public void delete_commentbyPid(String pid,DatabaseOperations databaseOperations) {
        SQLiteDatabase sqLiteDatabase = databaseOperations.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + comments_const.getTable_name() + " where " + comments_const.getPid() +" = " + pid +";");
    }

    public Cursor getInfo_Comment(String pid,DatabaseOperations dop) {
      /*  SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        String columns[] = {comments_const.getComment_id(),comments_const.getUid(), comments_const.getPid(), comments_const.getComment(), comments_const.getDoc(), comments_const.getUser_name(),comments_const.getUser_pic()};
        Cursor cr = sqLiteDatabase.query(comments_const.getTable_name(), columns, null, null, null, null, null);
        return cr;*/
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        String Query ="select * from " + comments_const.getTable_name() + " where " + comments_const.getPid() +" = " + pid +" order by doc desc " +";";
        cursor = sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public Cursor getInfo_Like(DatabaseOperations dop) {
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        String columns[] = {likes_const.getUser_id(), likes_const.getPid(),
                likes_const.getFlag(), likes_const.getFlagd()};
        Cursor cr = sqLiteDatabase.query(likes_const.getTable_name(), columns, null, null, null, null, null);
        return cr;
    }

    public Cursor getInfo_Users(DatabaseOperations dop) {
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        String columns[] = {user_const.getUser_id(), user_const.getUser_name(), user_const.getUser_email(),
                user_const.getUser_password(), user_const.getUser_status(), user_const.getUser_description(),
                user_const.getUser_pic()};
        Cursor cr = sqLiteDatabase.query(user_const.getTable_name(), columns, null, null, null, null, null);
        return cr;
    }

    public Cursor getInfo_HmtlTest(DatabaseOperations dop) {
        SQLiteDatabase sqLiteDatabase = dop.getReadableDatabase();
        String columns[] = {html_test_const.getTitle(), html_test_const.getLink(), html_test_const.getDescription(),
                html_test_const.getDate(), html_test_const.getUser_name(), html_test_const.getSr_key(), html_test_const.getUp_down(),
                html_test_const.getComment_count(), html_test_const.getUid(), html_test_const.getPost_pic(),html_test_const.getUser_pic()};
        Cursor cr = sqLiteDatabase.query(html_test_const.getTable_name(), columns, null, null, null, null, html_test_const.getDate()+" desc");
        return cr;
    }

    public class Comments_Const {
        String comment_id = "comment_id";
        String pid = "pid";
        String uid = "uid";
        String comment = "comment";
        String doc = "doc";
        String user_name = "user_name";
        String user_pic = "user_pic";

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_pic() {
            return user_pic;
        }

        public void setUser_pic(String user_pic) {
            this.user_pic = user_pic;
        }

        String table_name = "comments";


        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getDoc() {
            return doc;
        }

        public void setDoc(String doc) {
            this.doc = doc;
        }



        public String getTable_name() {
            return table_name;
        }

        public String getPid() {
            return pid;
        }

        public String getUid() {
            return uid;
        }

        public String getComment() {
            return comment;
        }
    }

    public class Likes_Const {
        String table_name = "likes";
        String user_id = "user_id";
        String pid = "pid";
        String flag = "flag";
        String flagd = "flagd";

        public String getTable_name() {
            return table_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getPid() {
            return pid;
        }

        public String getFlag() {
            return flag;
        }

        public String getFlagd() {
            return flagd;
        }
    }

    public class User_Const {
        String table_name = "users";
        String user_id = "user_id";
        String user_name = "user_name";
        String user_email = "user_email";
        String user_password = "user_password";
        String user_status = "user_status";
        String user_description = "user_description";
        String user_pic = "user_pic";

        public String getTable_name() {
            return table_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getUser_email() {
            return user_email;
        }

        public String getUser_password() {
            return user_password;
        }

        public String getUser_status() {
            return user_status;
        }

        public String getUser_description() {
            return user_description;
        }

        public String getUser_pic() {
            return user_pic;
        }
    }

    public class Search_Suggestion_Const{
        String table_name = "search_suggestion";

        public String getSearchSLNo() {
            return searchSLNo;
        }

        public void setSearchSLNo(String searchSLNo) {
            this.searchSLNo = searchSLNo;
        }

        String searchSLNo = "SLNo";
        String lastSearch = "lastSearch";

        public String getLastSearch() {
            return lastSearch;
        }

        public void setLastSearch(String lastSearch) {
            this.lastSearch = lastSearch;
        }

        public String getTable_name() {
            return table_name;
        }

        public void setTable_name(String table_name) {
            this.table_name = table_name;
        }
    }

    public class Html_Test_Const {
        String table_name = "html_tests";
        String sr_key = "sr_key";
        String link = "link";
        String title = "title";
        String description = "description";
        String up_down = "up_down";
        String comment_count = "comment_count";
        String uid = "uid";
        String date = "date";
        String post_pic = "post_pic";
        String user_name = "user_name";
        String user_pic = "user_pic";
        String user_status = "user_status";

        public String getUser_status() {
            return user_status;
        }

        Html_Test_Const() {

        }

        public String getUser_pic() {
            return user_pic;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getPost_pic() {
            return post_pic;
        }

        public String getTable_name() {
            return table_name;
        }

        public String getSr_key() {
            return sr_key;
        }

        public String getLink() {
            return link;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUp_down() {
            return up_down;
        }

        public String getComment_count() {
            return comment_count;
        }

        public String getUid() {
            return uid;
        }

        public String getDate() {
            return date;
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)  ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
