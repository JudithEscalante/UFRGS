package com.example.second;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

public class Splash_screen extends AppCompatActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler splashScreen = new Handler();
        splashScreen.postDelayed(Splash_screen.this, 3000);

    }

    public void run(){
        startActivity(new Intent(Splash_screen.this, Menu.class));
        finish();

        }
}