package com.hybrid.freeopensourceusers.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingFragment settingFragment = new SettingFragment();
        fragmentTransaction.add(android.R.id.content, settingFragment, "SETTING_FRAGMENT");
        fragmentTransaction.commit();



    }

    public static class SettingFragment extends PreferenceFragment{

        private static int FLAG;
        private static final int CANCEL = 0;
        private VolleySingleton volleySingleton;
        private RequestQueue requestQueue;
        private static final int ALLOW = 1;
        private  SharedPrefManager sharedPrefManager;
        private ProgressDialog progressDialog;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);
            sharedPrefManager = new SharedPrefManager(getActivity());
            progressDialog = new ProgressDialog(getActivity());
            volleySingleton = VolleySingleton.getInstance();
            requestQueue = volleySingleton.getRequestQueue();

            // Need to be implemented
            PreferenceScreen rateSettings = (PreferenceScreen) findPreference("rateSettings");
            rateSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(), "Implementation needed - url for play store", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            PreferenceScreen contactSettings = (PreferenceScreen) findPreference("contactSettings");
            contactSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "focusvce@gmail.com"));
                    startActivity(intent);
                    return true;
                }
            });


            final SwitchPreference postFeedNotification = (SwitchPreference) findPreference("postFeedNotification");
                int notPostStatus = sharedPrefManager.getNotPost();

                if (notPostStatus == 1){
                    postFeedNotification.setChecked(true);
                }else
                    postFeedNotification.setChecked(false);



            final SwitchPreference sessionFeedNotification = (SwitchPreference) findPreference("sessionFeedNotification");
            int notSessStatus = sharedPrefManager.getNotSess();

            if (notSessStatus == 1)
                sessionFeedNotification.setChecked(true);
            else
                sessionFeedNotification.setChecked(false);


           postFeedNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
               @Override
               public boolean onPreferenceChange(Preference preference, Object newValue) {
                   boolean switched = ((SwitchPreference) preference).isChecked();
                   if (switched){
                        FLAG = 0;
                       sharedPrefManager.setNotPost(CANCEL);
                       updateNotPostOnServer(postFeedNotification,CANCEL, FLAG);
                   }else {
                       FLAG = 1;

                       sharedPrefManager.setNotPost(ALLOW);
                       updateNotPostOnServer(postFeedNotification, ALLOW, FLAG);
                   }
                   return true;
               }
           });


            sessionFeedNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean switched = ((SwitchPreference) preference).isChecked();
                    if (switched){
                        FLAG = 0;
                        sharedPrefManager.setNotSess(CANCEL);
                        updateNotSessOnServer(sessionFeedNotification,CANCEL, FLAG);
                    }else {
                        FLAG = 1;
                        sharedPrefManager.setNotSess(ALLOW);
                        updateNotSessOnServer(sessionFeedNotification, ALLOW, FLAG);
                    }

                    return true;
                }
            });



        }

        private void updateNotSessOnServer(final SwitchPreference sessionFeedNotification,final int value,final int flag){
            String URL = Utility.getIPADDRESS() + "updateUserNotSess";
            progressDialog  = ProgressDialog.show(getActivity(), "Syncing on server", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            progressDialog.dismiss();
                            if (flag ==0)
                                sessionFeedNotification.setChecked(false);
                            else
                                sessionFeedNotification.setChecked(true);

                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } else if (jsonObject.getBoolean("error")) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Couldn't process your request", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", sharedPrefManager.getApiKey());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("not_sess", Integer.toString(value));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }

        private void updateNotPostOnServer(final SwitchPreference postFeedNotification, final int value, final int flag)
        {
            String URL = Utility.getIPADDRESS() + "updateUserNotPost";
            progressDialog  = ProgressDialog.show(getActivity(), "Syncing on server", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            progressDialog.dismiss();
                            if (flag ==0)
                                postFeedNotification.setChecked(false);
                            else
                                postFeedNotification.setChecked(true);

                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } else if (jsonObject.getBoolean("error")) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Couldn't process your request", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", sharedPrefManager.getApiKey());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("not_post", Integer.toString(value));
                    return params;
                }

            };
            requestQueue.add(stringRequest);


        }
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

        inflater.inflate(R.menu.comment_menu_trending, menu);

        return true;
    }
}
