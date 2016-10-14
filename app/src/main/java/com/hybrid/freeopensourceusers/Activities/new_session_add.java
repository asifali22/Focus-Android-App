package com.hybrid.freeopensourceusers.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class new_session_add extends AppCompatActivity {


    private static final String RESULT_CODE = "true";
    private int PICK_IMAGE_REQUEST = 1;
    private int TRANSACTION_REQUEST = 2;
    private Bitmap bitmap;
    private ImageView ses_image;
    private MyApplication myApplication;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private EditText title, desc, venue, coord, email, phone, rp, rpd, d_t, addr, room;
    private FloatingActionButton addImage;
    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session_add);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        myApplication = MyApplication.getInstance();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        addImage = (FloatingActionButton) findViewById(R.id.ses_add_image);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.new_session_add_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbarSession);
        ses_image = (ImageView) findViewById(R.id.ses_image);
        title = (EditText) findViewById(R.id.ses_title);
        desc = (EditText) findViewById(R.id.ses_description);
        venue = (EditText) findViewById(R.id.ses_venue);
        coord = (EditText) findViewById(R.id.ses_coordinator);
        email = (EditText) findViewById(R.id.ses_email);
        phone = (EditText) findViewById(R.id.ses_phone);
        rp = (EditText) findViewById(R.id.ses_resource_person);
        rpd = (EditText) findViewById(R.id.ses_resource_person_designation);
        d_t = (EditText) findViewById(R.id.ses_date_time);
        addr = (EditText) findViewById(R.id.ses_address);
        room = (EditText) findViewById(R.id.ses_room);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        if (mToolbar != null)
            setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(" ");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyTextDrawable myTextDrawable = new MyTextDrawable();
        ses_image.setImageDrawable(myTextDrawable.setTextDrawableForPost("Dark", "Image"));


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarSession);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layoutSession);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Add session");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    public void addSesImage(View view) {
        showFileChooser();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ses_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int result = 0;
        if (requestCode == TRANSACTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                result = data.getIntExtra("success", 0);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

            if (result == 1) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", RESULT_CODE);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }


    }

    public void submit_ses(View view) {

        String stitle, sdesc, svenue, scoord, semail, sphone, srp, srpd, sd_t, saddr, sroom;
        stitle = title.getText().toString().trim();
        sdesc = desc.getText().toString().trim();
        svenue = venue.getText().toString().trim();
        scoord = coord.getText().toString().trim();
        semail = email.getText().toString().trim();
        sphone = phone.getText().toString().trim();
        srp = rp.getText().toString().trim();
        srpd = rpd.getText().toString().trim();
        sd_t = d_t.getText().toString().trim();
        saddr = addr.getText().toString().trim();
        sroom = room.getText().toString().trim();

        if (stitle.isEmpty()) {
            title.setError("Field can't be empty");
            return;
        }
        if (sdesc.isEmpty()) {
            desc.setError("Field can't be empty");
            return;
        }
        if (svenue.isEmpty()) {
            venue.setError("Field can't be empty");
            return;
        }
        if (scoord.isEmpty()) {
            coord.setError("Field can't be empty");
            return;
        }
        if (semail.isEmpty()) {
            email.setError("Field can't can't be empty");
            return;
        }
        if (sphone.isEmpty()) {
            phone.setError("Field can't can't be empty");
            return;
        }
        if (srp.isEmpty()) {
            rp.setError("Field can't be empty");
            return;
        }
        if (srpd.isEmpty()) {
            rpd.setError("Field can't be empty");
            return;
        }
        if (sd_t.isEmpty()) {
            d_t.setError("Field can't be empty");
            return;
        }
        if (saddr.isEmpty()) {
            addr.setError("Field can't be empty");
            return;
        }
        if (sroom.isEmpty()) {
            room.setError("Field can't be empty");
            return;
        }

        if (bitmap == null || getImageUri(this, bitmap).toString().isEmpty()) {
            Snackbar.make(coordinatorLayout, "Please select an image for the session", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, session_details.class);
        intent.putExtra("id", 0);
        intent.putExtra("title", stitle);
        intent.putExtra("desc", sdesc);
        intent.putExtra("picurl", getImageUri(this, bitmap).toString());
        intent.putExtra("venue", svenue);
        intent.putExtra("coord", scoord);
        intent.putExtra("email", semail);
        intent.putExtra("phone", sphone);
        intent.putExtra("rp", srp);
        intent.putExtra("rpd", srpd);
        intent.putExtra("addr", saddr);
        intent.putExtra("date_time", sd_t);
        intent.putExtra("room", sroom);
        startActivityForResult(intent, TRANSACTION_REQUEST);


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
                return Uri.parse(path);
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        }

        return null;
    }

    public String getApiKey() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String api_key = sharedPreferences.getString("api_key", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.new_post_activity_menu, menu);

        return true;
    }

}
