package com.app.labvistilt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    private Button testA;
    private Button testB;
    private Button small;
    private Button medium;
    private Button large;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        /*testA = (Button) findViewById(R.id.buttonTestA);
        testA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivityTestA();
            }
        });
        testB = (Button) findViewById(R.id.buttonTestB);
        testB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivityTestB();
            }
        });*/

        small = (Button) findViewById(R.id.small);
        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivityTestB();
            }
        });

        medium = (Button) findViewById(R.id.medium);
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTestActivity();
            }
        });

        /*large = (Button) findViewById(R.id.large);
        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivityLarge();
            }
        });*/


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });   */
    }

    public void openNewActivityTestA(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openNewActivityTestB(){
        Intent intent = new Intent(this, ActivitytestB.class);
        intent.putExtra("option", "small");
        startActivity(intent);
    }

    public void openTestActivity(){
        Intent intent = new Intent(this, TestTilt.class);
        intent.putExtra("option", "medium");
        startActivity(intent);
    }

    public void openMainActivitySmall(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("option", "small");
        startActivity(intent);
    }

    public void openMainActivityMedium(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("option", "medium");
        startActivity(intent);
    }

    public void openMainActivityLarge(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("option", "large");
        startActivity(intent);
    }
}