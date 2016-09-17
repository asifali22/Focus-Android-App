package com.hybrid.freeopensourceusers.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class new_session_add extends AppCompatActivity {


    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    ImageView ses_image;
    MyApplication myApplication;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    EditText title,desc,venue,coord,email,phone,rp,rpd,d_t,addr,room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session_add);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        myApplication = MyApplication.getInstance();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
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
    }
    public void addSesImage(View view){
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


    }
    public void submit_ses(View view){


        String UPLOAD_URL = "http://focusvce.com/api/v1/upload_session";
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(myApplication.getApplicationContext(), "Post Added", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(new_session_add.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error", volleyError.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", getApiKey() + "");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String stitle,sdesc,svenue,scoord,semail,sphone,srp,srpd,sd_t,saddr,sroom;
                stitle = title.getText().toString();
                sdesc = desc.getText().toString();
                svenue = venue.getText().toString();
                scoord = coord.getText().toString();
                semail = email.getText().toString();
                sphone = phone.getText().toString();
                srp = rp.getText().toString();
                srpd = rpd.getText().toString();
                sd_t = d_t.getText().toString();
                saddr = addr.getText().toString();
                sroom = room.getText().toString();
                String api_key = getApiKey();
                String name = api_key+"-"+Long.toString(System.currentTimeMillis());
                String image = getStringImage(bitmap);




                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters

                params.put("title", stitle);
                params.put("desc", sdesc);
                params.put("image",image);
                params.put("name",name);
                params.put("venue",svenue);
                params.put("coord",scoord);
                params.put("email",semail);
                params.put("phone",sphone);
                params.put("rp",srp);
                params.put("rpd",srpd);
                params.put("addr",saddr);
                params.put("room",sroom);
                params.put("dos",sd_t);

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
    public String getApiKey() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String api_key = sharedPreferences.getString("api_key", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
