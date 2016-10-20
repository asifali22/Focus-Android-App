package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.freeopensourceusers.Activities.FirstActivity;
import com.hybrid.freeopensourceusers.Activities.LoginActivity;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class EditProfile extends AppCompatActivity {


    private EditText name,status,about,areaofinterest,org;
    private SharedPrefManager sharedPrefManager;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private CheckBox not_post,not_sess;
    private int not_post_flag=1,not_sess_flag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();



        status = (EditText) findViewById(R.id.edit_status);
        about = (EditText) findViewById(R.id.edit_about);
        areaofinterest = (EditText) findViewById(R.id.edit_area_of_interest);
        org = (EditText) findViewById(R.id.edit_organisation);

        sharedPrefManager = new SharedPrefManager(this);
        name.setText(sharedPrefManager.getUserName());
        status.setText(sharedPrefManager.getUserStatus());
        about.setText(sharedPrefManager.getAboutUser());
        areaofinterest.setText(sharedPrefManager.getAreaOfInterest());
        org.setText(sharedPrefManager.getOrganisation());
        not_post = (CheckBox) findViewById(R.id.not_post);
        not_sess = (CheckBox) findViewById(R.id.not_sess);

    }

    public void checkboxpost(View view){
        if(not_post.isChecked())
            not_post_flag=1;
        else if(!not_post.isChecked()){
            not_post_flag=0;
        }
    }
    public void checkboxsess(View view){
        if(not_sess.isChecked())
            not_sess_flag=1;
        else if(!not_sess.isChecked())
            not_sess_flag=0;
    }
    public void update(View view){
        String URL = Utility.getIPADDRESS()+"updateUser";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")){
                        Toast.makeText(EditProfile.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    //    sharedPrefManager.updateUserProfile(name.getText().toString(),status.getText().toString(),about.getText().toString(),areaofinterest.getText().toString(),org.getText().toString());
                        finish();
                    }
                    else if(jsonObject.getBoolean("error")){
                        Toast.makeText(EditProfile.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put("status", status.getText().toString());
                params.put("about",about.getText().toString());
                params.put("interest",areaofinterest.getText().toString());
                params.put("organisation",org.getText().toString());
                params.put("not_post",Integer.toString(not_post_flag));
                params.put("not_sess",Integer.toString(not_sess_flag));
                return params;
            }

        };
        requestQueue.add(stringRequest);



    }
}
