package com.example.macbookpro.srrufriend;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.macbookpro.srrufriend.fragment.MainFragment;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // Blurry.with(this).nto((ImageView) findViewById(R.id.bg_wallpaper));

        //Add fragment to Activity
        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentMainFragment, new MainFragment())
                    .commit();
        }

    }//Main Method
}// Main Class
