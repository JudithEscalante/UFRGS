package com.example.second;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    private Button testA;
    private Button testB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        testA = (Button) findViewById(R.id.buttonTestA);
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
        });
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
        startActivity(intent);
    }
}