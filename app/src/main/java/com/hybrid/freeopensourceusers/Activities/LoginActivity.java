package com.hybrid.freeopensourceusers.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    String SOCIAL_REGISTER = Utility.getIPADDRESS() + "socialRegister";
    String LOGIN = Utility.getIPADDRESS() + "login";
    StringRequest stringRequest = null;
    ImageView logoImage;
    GoogleApiClient mGoogleApiClient;
    EditText input_email, input_password;
    TextView invalidPop, navigateToRegister;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;
    Button Login;
    CallbackManager callbackManager;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_login);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sharedPrefManager = new SharedPrefManager(this);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        getLoginDetails(loginButton);
        logoImage = (ImageView) findViewById(R.id.logoImageeView);
        invalidPop = (TextView) findViewById(R.id.invalidPop);
        input_email = (EditText) findViewById(R.id.input_email);
        navigateToRegister = (TextView) findViewById(R.id.createOneLink);
        input_password = (EditText) findViewById(R.id.input_password);
        Login = (Button) findViewById(R.id.btn_login);
        Login.setOnClickListener(this);
        Glide.with(this).load(R.drawable.focus_brand).into(logoImage);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
    }

    /*
        Initialize the facebook sdk and then callback manager will handle the login responses.
     */
    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
    }
    /*
    Register a callback function with LoginButton to respond to the login result.
    On successful login,login result has new access token and  recently granted permissions.
    */

    protected void getLoginDetails(LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                getUserInfo(login_result);
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    protected void getUserInfo(LoginResult login_result) {

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            final JSONObject json_object,
                            GraphResponse response) {

                        /*Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                        intent.putExtra("jsondata", json_object.toString());*/

                        Log.e("JSONDATA", json_object.toString());
                        try {
                            final String user_name, user_email, pic_url;
                            JSONObject resp = new JSONObject(json_object.toString());
                            user_name = resp.get("name").toString();
                            user_email = resp.get("email").toString();
                            JSONObject profile_pic_data = new JSONObject(resp.get("picture").toString());
                            JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                            pic_url = profile_pic_url.getString("url");
                            stringRequest = new StringRequest(Request.Method.POST, SOCIAL_REGISTER,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            if (response != null) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String api_key = jsonObject.get("api_key").toString();
                                                    String fcm_token = jsonObject.get("fcm_token").toString();
                                                    String su_user = jsonObject.get("su_user").toString();
                                                    String user_pic = jsonObject.get("user_pic").toString();
                                                    String uid = jsonObject.get("user_id").toString();
                                                    int user_id = Integer.parseInt(uid);
                                                    String status = jsonObject.get("user_status").toString();

                                                    String about_user = jsonObject.get("about_user").toString();
                                                    String area_of_interest = jsonObject.get("area_of_interest").toString();
                                                    String organisation = jsonObject.get("organisation").toString();
                                                    int not_post = jsonObject.getInt("not_post");
                                                    int not_sess = jsonObject.getInt("not_sess");

                                                    sharedPrefManager.setUserStatusOnLogin(user_name,user_email,api_key,fcm_token,su_user,user_pic,user_id,status,about_user,area_of_interest,organisation, not_post, not_sess);
                                                    Toast.makeText(LoginActivity.this, "Welcome " + user_name, Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(LoginActivity.this, FirstActivity.class);
                                                    i.putExtra("login_intent",true);
                                                    startActivity(i);
                                                    finish();
                                                } catch (JSONException e) {
//                                                    Toast.makeText(LoginActivity.this,"CATCH",Toast.LENGTH_LONG).show();
                                                    Log.e(TAG, e.toString());
                                                    e.printStackTrace();
                                                }
                                                Log.e(TAG, response);
                                                hideProgressDialog();
                                            } else {
                                                invalidPop.setText("Could not login through Facebook.");
                                                hideProgressDialog();
                                                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("Volley Error : ", error.toString());
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("email", user_email);
                                    params.put("name", user_name);
                                    params.put("picurl", pic_url);
                                    return params;
                                }

                            };

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestQueue.add(stringRequest);


                        //startActivity(intent);
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }


    public void navigateToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void loginUser() {
        final String email = input_email.getText().toString().trim();
        if (email.isEmpty()) {
            input_email.setError("Email can't be empty!");
            return;
        }
        final String password = input_password.getText().toString().trim();
        if (password.isEmpty()) {
            input_password.setError("Password can't be empty!");
            return;
        } else if (password.length() < 6) {
            input_password.setError("Password should be at least 8 characters!");
            return;
        }

        //initialse your params

        if (Utility.validate(email)) {
            //handle params
            showProgressDialog();
            stringRequest = new StringRequest(Request.Method.POST, LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null && !errorPresent(response)) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String user_name = jsonObject.get("name").toString();
                                    String user_email = jsonObject.get("email").toString();
                                    String api_key = jsonObject.get("apiKey").toString();
                                    String fcm_token = jsonObject.get("fcm_token").toString();
                                    String su_user = jsonObject.get("su_user").toString();
                                    String user_pic = jsonObject.get("user_pic").toString();
                                    String uid = jsonObject.get("user_id").toString();
                                    int user_id = Integer.parseInt(uid);

                                    String status = jsonObject.get("user_status").toString();
                                    String about_user = jsonObject.get("about_user").toString();
                                    String area_of_interest = jsonObject.get("area_of_interest").toString();
                                    String organisation = jsonObject.get("organisation").toString();
                                    int not_post = jsonObject.getInt("not_post");
                                    int not_sess = jsonObject.getInt("not_sess");

                                    sharedPrefManager.setUserStatusOnLogin(user_name,user_email,api_key,fcm_token,su_user,user_pic,user_id,status,about_user,area_of_interest,organisation, not_post, not_sess);
                                    Toast.makeText(LoginActivity.this, "Welcome " + jsonObject.get("name").toString(), Toast.LENGTH_LONG).show();
                                    hideProgressDialog();
                                    Intent i = new Intent(LoginActivity.this, FirstActivity.class);
                                    i.putExtra("login_intent",true);
                                    startActivity(i);
                                    finish();
                                } catch (JSONException e) {
//                                    Toast.makeText(LoginActivity.this,"CATCH",Toast.LENGTH_LONG).show();
                                    Log.e(TAG, e.toString());
                                    e.printStackTrace();
                                }
                                Log.e(TAG, response);
                                hideProgressDialog();

                            } else if (errorPresent(response)) {
                                hideProgressDialog();
                                invalidPop.setText("Invalid email or password!");


                            } else {
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();
                            if (!sharedPrefManager.isOnline())
                                Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_LONG).show();
                            Log.e("Volley Error : ", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }

            };
            requestQueue.add(stringRequest);

        } else {
            input_email.setError("Enter a valid Email");
            return;
        }

    }

    private Boolean errorPresent(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.getBoolean("error")) {
                Log.e("wops", jsonObject.getBoolean("error") + "");
                return false;
            } else if (jsonObject.getBoolean("error")) {
                Log.e("man", jsonObject.getBoolean("error") + "");
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void SingInIntent() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();

            //get details and start an activity for results


            stringRequest = new StringRequest(Request.Method.POST, SOCIAL_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response != null) {
                                try {
                                    Log.e("ADARSH",response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    String user_name = acct.getDisplayName();
                                    String user_email = acct.getEmail();
                                    String api_key = jsonObject.get("api_key").toString();
                                    String fcm_token = jsonObject.get("fcm_token").toString();
                                    String su_user = jsonObject.get("su_user").toString();
                                    String user_pic = jsonObject.get("user_pic").toString();
                                    String uid = jsonObject.get("user_id").toString();
                                    String status = jsonObject.get("user_status").toString();
                                    int user_id = Integer.parseInt(uid);
                                    String about_user = jsonObject.get("about_user").toString();
                                    String area_of_interest = jsonObject.get("area_of_interest").toString();
                                    String organisation = jsonObject.get("organisation").toString();
                                    int not_post = jsonObject.getInt("not_post");
                                    int not_sess = jsonObject.getInt("not_sess");

                                    sharedPrefManager.setUserStatusOnLogin(user_name,user_email,api_key,fcm_token,su_user,user_pic,user_id,status,about_user,area_of_interest,organisation, not_post, not_sess);

                                    Toast.makeText(LoginActivity.this, "Welcome " + acct.getDisplayName(), Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(LoginActivity.this, FirstActivity.class);
                                    i.putExtra("login_intent",true);
                                    startActivity(i);
                                    finish();
                                } catch (JSONException e) {
//                                    Toast.makeText(LoginActivity.this,"CATCH",Toast.LENGTH_LONG).show();
                                    Log.e(TAG, e.toString());
                                    e.printStackTrace();
                                }
                                Log.e(TAG, response);
                                hideProgressDialog();
                            } else {
                                invalidPop.setText("Could not login through Google.");
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error : ", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", acct.getEmail());
                    params.put("name", acct.getDisplayName());
                    if (acct.getPhotoUrl() == null)
                        Log.e("ADARSH", "ADARSH");
                    if (acct.getPhotoUrl() != null)
                        params.put("picurl", acct.getPhotoUrl().toString());
                    else
                        params.put("picurl", "https://www.google.co.in/imgres?imgurl=http%3A%2F%2Fpreviews.123rf.com%2Fimages%2Fanwarsikumbang%2Fanwarsikumbang1402%2Fanwarsikumbang140200057%2F25705833-geek-cartoon-Stock-Vector.jpg&imgrefurl=http%3A%2F%2Fwww.123rf.com%2Fphoto_25705833_geek-cartoon.html&docid=vdyw3cqi_Mo6aM&tbnid=NPV6ZZ97Ihjy8M%3A&w=1300&h=1300&bih=678&biw=1323&ved=0ahUKEwjvps6nltHPAhWHOo8KHfRSBBwQMwg-KBIwEg&iact=mrc&uact=8");
                    return params;
                }

            };
            requestQueue.add(stringRequest);


        } else {
            // Signed out, show unauthenticated UI.

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.btn_login:
                loginUser();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        SingInIntent();
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }


    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}



