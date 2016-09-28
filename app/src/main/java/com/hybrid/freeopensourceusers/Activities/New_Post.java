package com.hybrid.freeopensourceusers.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.ViewDialog;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class New_Post extends AppCompatActivity  {

    public Toolbar newPostToolbar;
    public Connection connection;
    public Document document;
    public String imgurl = null;
    public EditText user_input_link, input_title, input_desc;
    public ImageView linkPhoto;
    public String title, desc, secondDesc, getImageUrl, input, finalDesc;
    public MyApplication myApplication;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public Boolean isCLicked = false;
    public Button preview, addpost;
    private ProgressDialog mProgressDialog;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    String api_key;
    int flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_layout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        newPostToolbar   =    (Toolbar) findViewById(R.id.newaddPostToolbar);
        user_input_link = (EditText) findViewById(R.id.input_user_link);
        linkPhoto = (ImageView) findViewById(R.id.linkPhoto);
        input_title = (EditText) findViewById(R.id.input_title);
        input_desc = (EditText) findViewById(R.id.input_desc);
        preview = (Button) findViewById(R.id.submit_button);
        addpost = (Button) findViewById(R.id.add_post_button);
        input_title.setVisibility(View.INVISIBLE);
        input_desc.setVisibility(View.INVISIBLE);
        if( newPostToolbar != null)
            setSupportActionBar(newPostToolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Add Post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        // Get the action of the intent
        String action = intent.getAction();
        // Get the type of intent (Text or Image)
        String type = intent.getType();
        // When Intent's action is 'ACTION+SEND' and Tyoe is not null
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            // When tyoe is 'text/plain'
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) { // When type is 'image/*'
                handleSendImage(intent); // Handle single image being sent
            }
        }
        myApplication = MyApplication.getInstance();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        //Bundle extras = getIntent().getExtras();
        api_key = getApiKey();//extras.getString("API_KEY");
    }

    /**
     * Method to hanlde incoming text data
     *
     * @param intent
     */
    private void handleSendText(Intent intent) {
        // Get the text from intent
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        // When Text is not null
        if (sharedText != null) {
            // Show the text as Toast message
            Toast.makeText(this, sharedText, Toast.LENGTH_LONG).show();
            user_input_link.setText(sharedText);
        }
    }

    /**
     * Method to handle incoming Image
     *
     * @param intent
     */
    private void handleSendImage(Intent intent) {
        // Get the image URI from intent
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        // When image URI is not null
        if (imageUri != null) {
            // Update UI to reflect image being shared
            linkPhoto.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error occured, URI is invalid", Toast.LENGTH_LONG).show();
        }
    }

    public String getUrl(String urll) {
        connection = Jsoup.connect(urll).userAgent("Mozilla");
        try {
            document = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements metaOgImage = document.select("meta[property=og:image]");
        if (metaOgImage.size() != 0 && metaOgImage != null) {
            for (int i = 0; i < metaOgImage.size(); i++) {
                if (metaOgImage.get(i).attr("content") != "") {
                    imgurl = metaOgImage.get(i).attr("content");
                    break;
                } else
                    continue;
            }
        } else if (imgurl == "" || imgurl == null) {
            Elements pngs = document.select("img");
            for (int i = 0; i < pngs.size(); i++) {
                if (pngs.get(i).attr("src") != "") {
                    imgurl = pngs.get(i).attr("src");
                    break;
                } else
                    continue;
            }
        }

        title = document.title();

        desc = null;
        Elements metaOgDescription = document.select("meta[property=og:description]");
        if (metaOgDescription.size() != 0)
            desc = metaOgDescription.get(0).attr("content");

        if (desc != null) {
            finalDesc = desc;
        } else {
            Elements metaDescription = document.select("meta[name=description]");
            secondDesc = null;
            if (metaDescription.size() != 0)
                secondDesc = metaDescription.get(0).attr("content");

            if (secondDesc != null) {
                finalDesc = desc;
            }

        }


        return imgurl;
    }

    public void submitButton(View view) {
        flag = 1;
        isCLicked = true;
        input = user_input_link.getText().toString();
        if (!input.equals("")) {
            int start = input.indexOf("http");
            int end = 0;
            while (end < start) {
                end = input.indexOf(" ");
                if (end < start) {
                    input = input.substring(end + 1, input.length());
                    start = start - end - 1;
                    end = 0;
                } else
                    break;
            }
            end = input.indexOf(" ");
            if (end != -1)
                input = input.substring(0, end);
            else
                input = input.substring(0, input.length());
            Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
            //adarsh trivedi http://focusvce.com ada
            getImageUrl = getUrl(input);
            Glide.with(this)
                    .load(getImageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(linkPhoto);

            ViewDialog alert = new ViewDialog();
            alert.showDialog(this,"",getUserName(),title,finalDesc,getImageUrl);


        }


    }

    public void addPostButton(View view) {
        if (flag == 1) {
            if (isCLicked && !input.isEmpty()) {

                String ADDPOST_URL = Utility.getIPADDRESS() + "posts";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ADDPOST_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(myApplication.getApplicationContext(), "Post Added", Toast.LENGTH_LONG).show();
                        Log.e("ADARSH_LOG",response);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ADARSH_LOG",error.toString());
                        Toast.makeText(myApplication.getApplicationContext(), "Post Added", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", api_key + "");
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("link", input + "");
                        params.put("title", title + "");
                        params.put("description", finalDesc + "");
                        params.put("postpicurl", getImageUrl + "");
                        return params;
                    }

                };
                requestQueue.add(stringRequest);

            }
        }
        else if(flag==2){

            String UPLOAD_URL = "http://focusvce.com/api/v1/upload";
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
                            Toast.makeText(New_Post.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            Log.e("Error", volleyError.toString());
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", api_key + "");
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    String image = getStringImage(bitmap);
                    Log.e("ADARSH",image);

                    //Getting Image Name
                    String name = api_key+"-"+Long.toString(System.currentTimeMillis());
                    String title = input_title.getText().toString();
                    String desc = input_desc.getText().toString();

                    //Creating parameters
                    Map<String,String> params = new Hashtable<>();

                    //Adding parameters

                    params.put("image", image);
                    params.put("name", name);
                    params.put("title",title);
                    params.put("desc",desc);

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

    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void uploadfab(View view) {
        flag = 2;
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
                linkPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        input_title.setVisibility(View.VISIBLE);
        input_desc.setVisibility(View.VISIBLE);

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
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.new_post_activity_menu, menu);

        return true;
    }

    public String getApiKey() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String api_key = sharedPreferences.getString("api_key", null);

        if (!api_key.isEmpty()) {
            return api_key;
        } else
            return null;
    }

    public String getUserName() {

        SharedPreferences sharedPreferences = myApplication.getApplicationContext().getSharedPreferences("user_details", myApplication.getApplicationContext().MODE_PRIVATE);
        String user_name_from_sf = sharedPreferences.getString("user_name", null);

        if (!user_name_from_sf.isEmpty()) {
            return user_name_from_sf;
        } else
            return null;
    }

    public String getUserprofilePicFromSharedPref(){
        // NOTE:
        // Handle this in login activity and then just like getUsername implement it

        return null;
    }


}

