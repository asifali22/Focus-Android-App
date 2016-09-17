package com.hybrid.freeopensourceusers.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.Utility.MyTextDrawable;


public class session_details extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    int id;

    private String title,desc,picurl,venue,coord,email,phone,rp,rpd,addr,date_time,room;
    private TextView des,resource_person,respd,ven,d_t,add,roo,coo;
    private ImageView sessionPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        MyTextDrawable myTextDrawable = new MyTextDrawable();
        collapsingToolbarLayout = (CollapsingToolbarLayout)  findViewById(R.id.collapsing_toolbarSession);
        toolbar = (Toolbar) findViewById(R.id.toolbarSession);
        collapsingToolbarLayout.setExpandedTitleMarginStart(16);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        Glide.with(this)
                .load(picurl)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(myTextDrawable.setTextDrawableForError("Error!"))
                .dontAnimate()
                .into(sessionPic);

    }

    public void callButtonClicked(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phone));
        startActivity(intent);
    }

    public void mailButtonClicked(View v){
        Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + email));
        startActivity(intent);
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
}
