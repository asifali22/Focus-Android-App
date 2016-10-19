package com.hybrid.freeopensourceusers.UserProfileStuff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.hybrid.freeopensourceusers.R;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class EditProfile extends AppCompatActivity {

    private static final String SHOWCASE_ID = "like_dislike";
    Button showCase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        showCase = (Button) findViewById(R.id.showCase);

        new MaterialShowcaseView.Builder(this)
                .setTarget(showCase)
                .setDismissText("GOT IT")
                .setContentText("This is some amazing feature you should know about")
                .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();


    }
}
