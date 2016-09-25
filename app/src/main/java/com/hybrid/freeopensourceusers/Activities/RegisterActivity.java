package com.hybrid.freeopensourceusers.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.Fragments.MainFragment;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;
import com.isseiaoki.simplecropview.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener{

    private ProgressDialog mProgressDialog;

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private CircleImageView circleImageView;
    private MyApplication myApplication;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private AppCompatButton changeProfile;

    private static final String REGISTER_URL = Utility.getIPADDRESS()+"register.php";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_USERNAME = "user_email";
    public static final String KEY_PASSWORD = "user_password";
    private ExecutorService mExecutor;
    public static Intent createIntent(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.setData(uri);
        return intent;
    }

    EditText input_nameRegister, input_emailRegister, input_passwordRegister, input_retypepasswordRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hybrid.freeopensourceusers.R.layout.activity_register);
        myApplication = MyApplication.getInstance();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        circleImageView = (CircleImageView) findViewById(R.id.userProfile);
        changeProfile = (AppCompatButton) findViewById(R.id.btn_change);
        input_nameRegister = (EditText) findViewById(R.id.input_nameRegister);
        input_emailRegister = (EditText) findViewById(R.id.input_emailRegister);
        input_passwordRegister = (EditText) findViewById(R.id.input_passwordRegister);
        input_retypepasswordRegister = (EditText) findViewById(R.id.input_retypepasswordRegister);

        changeProfile.setOnClickListener(this);
        mExecutor = Executors.newSingleThreadExecutor();

        final Uri uri = getIntent().getData();
        if(uri != null)
            mExecutor.submit(new RegisterActivity.LoadScaledImageTask(this, uri, circleImageView, calcImageSize()));


    }

//    public void changeProfile(View view) {
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.container, MainFragment.getInstance(),"frag")
//                .commit();
//
//
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void crossClicked(View view){
       finish();
    }


    public void registerUser(View view) {
        final String name = input_nameRegister.getText().toString().trim();
        if (name.isEmpty()) {
            input_nameRegister.setError("Name can't be empty!");
            return;
        }

        final String email = input_emailRegister.getText().toString().trim();
        if (email.isEmpty()) {
            input_emailRegister.setError("Email can't be emplty!");
            return;
        }

        final String password = input_passwordRegister.getText().toString().trim();
        String re_password = input_retypepasswordRegister.getText().toString().trim();

        if (!password.equals(re_password)) {
            input_passwordRegister.setError("Password mismatch!");
            return;
        } else if (password.isEmpty()) {
            input_passwordRegister.setError("Password can't be empty!");
            return;
        } else if (password.length() < 8) {
            input_passwordRegister.setError("Password should be at least 8 characters!");
            return;
        }

        //get params and work

        if (Utility.validate(email)) {
            //handle params
            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("Registration Success")) {
                                Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                hideProgressDialog();
//                                startActivity(intent);
                                finish();
                            } else {
                                hideProgressDialog();
                                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();
                            if(!isOnline())
                                Toast.makeText(RegisterActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(RegisterActivity.this, "Error Occurred!", Toast.LENGTH_LONG).show();
                            Log.e("Volley Error : ", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_NAME, name);
                    params.put(KEY_USERNAME, email);
                    params.put(KEY_PASSWORD, password);
                    return params;
                }

            };

            requestQueue.add(stringRequest);

        } else {
            input_emailRegister.setError("Enter a valid Email");
            return;
        }

    }

    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        startActivity(RegisterActivity.createIntent(this, uri));
    }



    @Override
    protected void onDestroy() {
        mExecutor.shutdown();
        super.onDestroy();
    }

    public int calcImageSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return Math.min(Math.max(metrics.widthPixels, metrics.heightPixels), 2048);
    }

    public boolean isLargeImage(Bitmap bm) {
        return bm.getWidth() > 2048 || bm.getHeight() > 2048;
    }



    @Override
    public void onClick(View v) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, MainFragment.getInstance(),"frag")
                .commit();
    }

    public static class LoadScaledImageTask implements Runnable {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        Context context;
        Uri uri;
        ImageView imageView;
        int width;

        public LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {
            this.context = context;
            this.uri = uri;
            this.imageView = imageView;
            this.width = width;
        }

        @Override
        public void run() {
            final int exifRotation = Utils.getExifOrientation(context, uri);
            Log.d(TAG, "exifRotation = " + exifRotation);
            int maxSize = Utils.getMaxSize();
            int requestSize = Math.min(width, maxSize);
            try {
                final Bitmap sampledBitmap = Utils.decodeSampledBitmapFromUri(context, uri, requestSize);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageMatrix(Utils.getMatrixFromExifOrientation(exifRotation));
                        imageView.setImageBitmap(sampledBitmap);
                    }
                });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag("frag") != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentByTag("frag"))
                    .commit();
        }
        else {
            finish();
        }
    }

    private void showProgressDialog() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
