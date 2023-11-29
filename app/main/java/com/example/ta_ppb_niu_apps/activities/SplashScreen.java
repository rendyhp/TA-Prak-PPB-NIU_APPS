package com.example.ta_ppb_niu_apps.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ta_ppb_niu_apps.BottomMenu;
import com.example.ta_ppb_niu_apps.R;
import com.example.ta_ppb_niu_apps.utilities.PreferenceManager;

public class SplashScreen extends AppCompatActivity {

    private PreferenceManager preferenceManager;


    private static int splashInterval = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(R.layout.activity_splashscreen);
        handler();
    }

    private void handler(){
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        }, splashInterval);
    }

}