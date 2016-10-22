package com.hybrid.freeopensourceusers.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hybrid.freeopensourceusers.R;

public class PassThroughActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_through);
        Intent intent = new Intent(this,FirstActivity.class);
        intent.setAction("FIRST_ACTIVITY");
        sendBroadcast(intent);


    }
}
