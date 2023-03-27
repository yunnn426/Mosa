package com.example.mosa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Start extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            Button imageButton = findViewById(R.id.GoEmailButton);
        imageButton.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(),
                Signup.class);
        startActivity(intent);
        });


    }

}



