package com.hybrid.freeopensourceusers.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.hybrid.freeopensourceusers.UserProfileStuff.UserProfile;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;
import com.like.LikeButton;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class New_Post extends AppCompatActivity implements View.OnClickListener {

    private static final String RESULT_CODE = "true";
    private Toolbar newPostToolbar;
    private Connection connection;
    private Document document;
    private String imgurl = null;
    private EditText user_input_link, input_title, input_desc;
    private ImageView linkPhoto;
    private String title, desc, secondDesc, getImageUrl, input, finalDesc;
    private MyApplication myApplication;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private Boolean isCLicked = false;
    private ImageButton preview;
    private ProgressDialog mProgressDialog;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private MyTextDrawable myTextDrawable;
    private String api_key;
    private int flag =1;
    private RelativeLayout revealLayout, toggleLayout;
    private ScrollView simpleLayout;
    private AppCompatButton advancedButton;
    private SharedPrefManager sharedPrefManager;
    Uri filePath;
    int flag_for_image=0;
    ProgressDialog loading = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_layout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        myTextDrawable = new MyTextDrawable();
        newPostToolbar   =    (Toolbar) findViewById(R.id.newaddPostToolbar);
        user_input_link = (EditText) findViewById(R.id.input_user_link);
        linkPhoto = (ImageView) findViewById(R.id.linkPhoto);
        input_title = (EditText) findViewById(R.id.input_title);
        input_desc = (EditText) findViewById(R.id.input_desc);
        preview = (ImageButton) findViewById(R.id.submit_button);
        revealLayout = (RelativeLayout) findViewById(R.id.parentReveal);
        advancedButton = (AppCompatButton) findViewById(R.id.testAdvanced);
        simpleLayout = (ScrollView) findViewById(R.id.simpleRelative);
        toggleLayout = (RelativeLayout) findViewById(R.id.toggleLayout);
        sharedPrefManager = new SharedPrefManager(this);
        filePath = null;

        linkPhoto.setImageDrawable(myTextDrawable.setTextDrawableForPost("Zebra", "Image"));
        if( newPostToolbar != null)
            setSupportActionBar(newPostToolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Add post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        preview.setOnClickListener(this);
        advancedButton.setOnClickListener(this);

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
        api_key = sharedPrefManager.getApiKey();//extras.getString("API_KEY");
    }

    @Override
    public void onPause(){

        super.onPause();
        if(loading != null)
            loading.dismiss();
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
//            Toast.makeText(this, sharedText, Toast.LENGTH_LONG).show();
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
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
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
                }
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

    public void submitButton() {
        isCLicked = true;
        input = user_input_link.getText().toString();
        if (flag == 1) {
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
                getImageUrl = getUrl(input);
//            Glide.with(this)
//                    .load(getImageUrl)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .crossFade()
//                    .into(linkPhoto);
                if(finalDesc!=null)
                finalDesc = finalDesc.trim();

                if ( finalDesc == null || finalDesc.isEmpty() || finalDesc.equals(""))
                    finalDesc = title;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("", sharedPrefManager.getUserName(), title, finalDesc, getImageUrl);
                    }
                });



            }
        }else if (flag ==2){
                final String title = input_title.getText().toString();
                final String desc = input_desc.getText().toString();
                if(!(title.isEmpty() || desc.isEmpty())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog("", sharedPrefManager.getUserName(), title, desc, "");
                        }
                    });

                }
                else if (title.isEmpty())
                    input_title.setError("Title can't be empty");
                else if (desc.isEmpty())
                    input_desc.setError("Description can't be empty");
//                else if(bitmap == null) {
//                    // handle this event for the post without images
//                }

        }


    }

    private void showDialog(String userImageText, String userName, String title, String finalDesc, String getImageUrl) {


        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogMainView = factory.inflate(R.layout.layout_for_dialog, null);

        final AlertDialog myDialog = new AlertDialog.Builder(this).create();

        CircleImageView circleImageView;
        TextView user_name, post_title, post_description, like_count, comment_count;
        ImageView post_pic;
        TextView addButton, cancelButton;
        LikeButton plus_like, minus_dislike;



        circleImageView = (CircleImageView) dialogMainView.findViewById(R.id.user_profile_image);
        user_name = (TextView) dialogMainView.findViewById(R.id.user_name);
        post_title = (TextView) dialogMainView.findViewById(R.id.post_title);
        post_description = (TextView) dialogMainView.findViewById(R.id.post_description);
        like_count = (TextView) dialogMainView.findViewById(R.id.like_count);
        comment_count = (TextView) dialogMainView.findViewById(R.id.comment_count);
        post_pic = (ImageView) dialogMainView.findViewById(R.id.post_pic);
        addButton = (TextView) dialogMainView.findViewById(R.id.addButtonDialog);
        cancelButton = (TextView) dialogMainView.findViewById(R.id.cancelButtonDialog);
        plus_like = (LikeButton) dialogMainView.findViewById(R.id.plus_like);
        minus_dislike = (LikeButton) dialogMainView.findViewById(R.id.minus_dislike);


        myDialog.setView(dialogMainView);

        if (flag == 1)
        Glide.with(this)
                .load(getImageUrl)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(myTextDrawable.setTextDrawableForError("Error!"))
                .crossFade()
                .into(post_pic);
        else if (flag ==2 ) {

                post_pic.setImageBitmap(bitmap);

        }


        if (userImageText.isEmpty())
            userImageText = userName;

        Glide.with(this)
                .load(userImageText)
                .fitCenter()
                .dontAnimate()
                .placeholder(R.drawable.blank_person_final)
                .error(myTextDrawable.setTextDrawable(userName))
                .into(circleImageView);

        user_name.setText(userName);
        post_title.setText(title);
        post_description.setText(finalDesc);
        like_count.setText("0");
        comment_count.setText("0");
        plus_like.setEnabled(false);
        minus_dislike.setEnabled(false);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPostButton();
                myDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();

    }





    public void addPostButton() {
        if (flag == 1) {
            if (isCLicked && !input.isEmpty()) {

                String ADDPOST_URL = Utility.getIPADDRESS() + "posts";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ADDPOST_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(myApplication.getApplicationContext(), "Post added", Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", RESULT_CODE);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(myApplication.getApplicationContext(), "Error occurred!", Toast.LENGTH_LONG).show();
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
            final String photoLink;
            if (input.isEmpty())
                photoLink = "abc";
            else
                photoLink = input;

            if(flag_for_image==0)
                Toast.makeText(New_Post.this,"No Image",Toast.LENGTH_SHORT).show();

            String UPLOAD_URL = "http://focusvce.com/api/v1/upload";
            final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            loading.dismiss();
                            Toast.makeText(myApplication.getApplicationContext(), "Post Added", Toast.LENGTH_LONG).show();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", RESULT_CODE);
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
                            Toast.makeText(New_Post.this,"Error occurred! Try again.", Toast.LENGTH_LONG).show();
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

                    //Getting Image Name
                    String name = api_key+"-"+Long.toString(System.currentTimeMillis());
                    String title = input_title.getText().toString();
                    String desc = input_desc.getText().toString();

                    //Creating parameters
                    Map<String,String> params = new Hashtable<>();

                    //Adding parameters
                    params.put("link", photoLink);
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
        bmp.compress(Bitmap.CompressFormat.JPEG,30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void uploadfab(View view) {
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
             filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                linkPhoto.setImageBitmap(bitmap);
                flag_for_image=1;
            } catch (IOException e) {
                e.printStackTrace();
            }
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





    public String getUserprofilePicFromSharedPref(){
        // NOTE:
        // Handle this in login activity and then just like getUsername implement it

        return null;
    }

    public void pasteFromCopy(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = null;
        String pasteData = "";


        ClipData cData = clipboard.getPrimaryClip();
        if (cData != null)
            item = cData.getItemAt(0);
        if (item != null)
        pasteData = item.getText().toString();
        if (!pasteData.isEmpty())
            user_input_link.setText(pasteData);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_button:
                new BackGroundTask().execute("submit_button");
                break;

            case R.id.testAdvanced:
                advancedBottonClickListener();
                break;

        }
    }

    public void advancedBottonClickListener(){

        if (revealLayout.getVisibility() == View.INVISIBLE) {
            tintSystemBars();
            flag = 2;
            if (Build.VERSION.SDK_INT >= 21) {
                // get the center for the clipping circle
                int cx = revealLayout.getWidth() / 2;
                int cy = 0;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(revealLayout.getWidth(), revealLayout.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, 0, finalRadius);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        advancedButton.setText("Simple?");
                    }
                });

                // make the view visible and start the animation
                revealLayout.setVisibility(View.VISIBLE);
                anim.start();

            } else {
                revealLayout.setVisibility(View.VISIBLE);
                advancedButton.setText("Simple?");
            }
        }else{
                tintSystemBarsBack();
                flag =1;
            if (Build.VERSION.SDK_INT >= 21) {
                // get the center for the clipping circle
                int cx = revealLayout.getWidth() / 2;
                int cy = 0;

                // get the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(revealLayout.getWidth(), revealLayout.getHeight());

                // create the animation (the final radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, initialRadius, 0);

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        revealLayout.setVisibility(View.INVISIBLE);
                        advancedButton.setText("Custom?");
                    }
                });

                // start the animation
                anim.start();

            }else {
                revealLayout.setVisibility(View.INVISIBLE);
                advancedButton.setText("Custom?");
            }

        }
    }


    private void tintSystemBars() {
        // Initial colors of each system bar.
        final int statusBarColor = getColorFunction(this,R.color.status_bar_color);
        final int toolbarColor = getColorFunction(this,R.color.toolbar_color);

        // Desired final colors of each bar.
        final int statusBarToColor = getColorFunction(this, R.color.status_bar_to_color);
        final int toolbarToColor = getColorFunction(this, R.color.toolbar_to_color);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the status bar.
                int blended = blendColors(statusBarColor, statusBarToColor, position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(blended);

                }

                // Apply blended color to the ActionBar.
                blended = blendColors(toolbarColor, toolbarToColor, position);
                ColorDrawable background = new ColorDrawable(blended);
                getSupportActionBar().setBackgroundDrawable(background);
                simpleLayout.setBackgroundColor(blended);
                toggleLayout.setBackgroundColor(blended);

            }
        });

        anim.setDuration(300).start();
    }

    private void tintSystemBarsBack() {
        // Initial colors of each system bar.
        final int statusBarColor = getColorFunction(this,R.color.status_bar_to_color);
        final int toolbarColor = getColorFunction(this,R.color.toolbar_to_color);

        // Desired final colors of each bar.
        final int statusBarToColor = getColorFunction(this, R.color.status_bar_color);
        final int toolbarToColor = getColorFunction(this, R.color.toolbar_color);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the status bar.
                int blended = blendColors(statusBarColor, statusBarToColor, position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(blended);

                }

                // Apply blended color to the ActionBar.
                blended = blendColors(toolbarColor, toolbarToColor, position);
                ColorDrawable background = new ColorDrawable(blended);
                getSupportActionBar().setBackgroundDrawable(background);
                simpleLayout.setBackgroundColor(blended);
                toggleLayout.setBackgroundColor(blended);

            }
        });

        anim.setDuration(500).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


    public static final int getColorFunction(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    private class BackGroundTask extends AsyncTask<String, Integer, String> {



        @Override
        protected void onPreExecute() {
           loading  = ProgressDialog.show(New_Post.this, "Extracting", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("submit_button"))
                submitButton();

            return "All Done!";
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.dismiss();
        }
    }

}

