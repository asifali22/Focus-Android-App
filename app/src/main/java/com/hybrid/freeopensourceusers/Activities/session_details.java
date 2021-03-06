package com.hybrid.freeopensourceusers.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.transform.Templates;


public class session_details extends AppCompatActivity {


    MyApplication myApplication;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    int id;

    private String title,desc,picurl,venue,coord,email,phone,rp,rpd,addr,date_time,room;
    private TextView des,resource_person,respd,ven,d_t,add,roo,coo;
    private ImageView sessionPic;
    private FloatingActionButton addd;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private CoordinatorLayout coordinatorLayout;
    private SharedPrefManager sharedPrefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_session_details);
        myApplication = MyApplication.getInstance();
        sharedPrefManager = new SharedPrefManager(this);
        MyTextDrawable myTextDrawable = new MyTextDrawable();
        collapsingToolbarLayout = (CollapsingToolbarLayout)  findViewById(R.id.collapsing_toolbarSession);
        toolbar = (Toolbar) findViewById(R.id.toolbarSession);
        collapsingToolbarLayout.setExpandedTitleMarginStart(16);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addd = (FloatingActionButton) findViewById(R.id.fab_add_session);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.session_detail_coordinatorlayout);


        Bundle bundle = getIntent().getExtras();
        id=bundle.getInt("id");
        title=bundle.getString("title");
        desc=bundle.getString("desc");
        picurl=bundle.getString("picurl");
        venue=bundle.getString("venue");
        coord=bundle.getString("coord");
        email=bundle.getString("email");
        phone=bundle.getString("phone");
        rp=bundle.getString("rp");
        rpd=bundle.getString("rpd");
        addr=bundle.getString("addr");
        date_time=bundle.getString("date_time");
        room=bundle.getString("room");

        if(id==0) {
            addd.setVisibility(View.VISIBLE);
            Snackbar.make(coordinatorLayout, "Preview : Make sure the details are correct", Snackbar.LENGTH_LONG).show();
        }


        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        des = (TextView) findViewById(R.id.sd_desc);
        resource_person= (TextView) findViewById(R.id.sd_resource_person);
        respd = (TextView) findViewById(R.id.sd_desg);
        ven = (TextView) findViewById(R.id.s_venue);
        d_t=(TextView) findViewById(R.id.sd_date_time);
        add=(TextView) findViewById(R.id.sd_address);
        roo=(TextView) findViewById(R.id.sd_room);
        coo=(TextView) findViewById(R.id.nameOfCoordinator);
        sessionPic = (ImageView) findViewById(R.id.postpicImageSession);


        collapsingToolbarLayout.setTitle(title);
        des.setText(desc);
        resource_person.setText(rp);
        respd.setText(rpd);
        ven.setText(venue);
        d_t.setText(date_time);
        add.setText(addr);
        roo.setText(room);
        coo.setText(coord);

        if(id!=0)
        Glide.with(this)
                .load(picurl)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(myTextDrawable.setTextDrawableForError("Error!"))
                .dontAnimate()
                .into(sessionPic);
        else
            sessionPic.setImageBitmap(UriToBitmap(picurl));

        addd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPrefManager.isOnline()){
                String UPLOAD_URL = "http://focusvce.com/api/v1/upload_session";
                final ProgressDialog loading = ProgressDialog.show(session_details.this, "Uploading...", "Please wait...", false, false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                //Disimissing the progress dialog
                                loading.dismiss();
                                Toast.makeText(myApplication.getApplicationContext(), "Session Added", Toast.LENGTH_LONG).show();
                                Intent returnIntent = getIntent();
                                returnIntent.putExtra("success", 1);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                                //Showing toast message of the response

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //Dismissing the progress dialog
                                loading.dismiss();

                                //Showing toast
                                Toast.makeText(session_details.this, "Error occurred!", Toast.LENGTH_LONG).show();
                                Log.e("Error", volleyError.toString());
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", sharedPrefManager.getApiKey() + "");
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //Converting Bitmap to String


                        //Creating parameters
                        Map<String, String> params = new Hashtable<>();

                        String api_key = sharedPrefManager.getApiKey();
                        String name = api_key + "-" + Long.toString(System.currentTimeMillis());
                        //Adding parameters

                        params.put("title", title);
                        params.put("desc", desc);
                        params.put("image", getStringImage(UriToBitmap(picurl)));
                        params.put("name", name);
                        params.put("venue", venue);
                        params.put("coord", coord);
                        params.put("email", email);
                        params.put("phone", phone);
                        params.put("rp", rp);
                        params.put("rpd", rpd);
                        params.put("addr", addr);
                        params.put("room", room);
                        params.put("dos", date_time);

                        //returning parameters
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 50000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 50000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });


                //Adding request to the queue
                requestQueue.add(stringRequest);
            }
                else
                    Snackbar.make(coordinatorLayout,"No internet connection",Snackbar.LENGTH_SHORT).show();
        }
        });

    }

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(300);
            getWindow().setEnterTransition(fade);


            getWindow().setReturnTransition(fade);

        }

    }


    public Bitmap UriToBitmap(String uri){
        Uri u = Uri.parse(uri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), u);
            return bitmap;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;

    }



    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }




    public void callButtonClicked(View v){
        if (sharedPrefManager.isLoggedIn()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }else
            sharedPrefManager.showAlertDialog(v);
    }

    public void mailButtonClicked(View v){
        if (sharedPrefManager.isLoggedIn()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
            startActivity(intent);
        }else
            sharedPrefManager.showAlertDialog(v);
    }

    public void showOnMap(View v){
        String map = "http://maps.google.co.in/maps?q=" + addr;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.session_details, menu);

        return true;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public void showFullSessionImage(View view) {
        MyTextDrawable myTextDrawable = new MyTextDrawable();
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogMainView = factory.inflate(R.layout.fragment_image_post, null);

        final AlertDialog myDialog = new AlertDialog.Builder(this).create();

        ImageView mImageView = (ImageView) dialogMainView.findViewById(R.id.myImagePostContainer);

        myDialog.setView(dialogMainView);
        if (!picurl.isEmpty())
            Glide.with(this)
                    .load(picurl)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading)
                    .dontAnimate()
                    .error(myTextDrawable.setTextDrawableForError("Error!"))
                    .into(mImageView);

        myDialog.show();
    }
}
