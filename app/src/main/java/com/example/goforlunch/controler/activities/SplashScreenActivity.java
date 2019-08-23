package com.example.goforlunch.controler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goforlunch.R;

public class SplashScreenActivity extends AppCompatActivity {


    private static final int SPLASH_SCREEN_TIMEOUT = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Redirect to LoginActivity after 2.5 sec
        //handler post delayed
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
