package com.developer.jatin.text;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Start extends AppCompatActivity {
Button navigate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        getSupportActionBar().hide();
        navigate=(Button)findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(Start.this,ScrollingActivity.class);
                startActivity(i);
            }
        });
    }
}
