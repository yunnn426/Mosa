package com.example.mosa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Start extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        boolean checkFirst = pref.getBoolean("checkFirst", false);

        if(checkFirst==false){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("checkFirst", true);
            editor.commit();

            Intent intent = new Intent(Start.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }else{
//            Button imageButton = findViewById(R.id.GoEmailButton);
//            imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),
                    Signup.class);
            startActivity(intent);
//            });

        }



    }

}



