package com.example.mosa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class checkFirst extends AppCompatActivity {
    final String PREFNAME = "Preferences";

    public void isFirstTime() {
        SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (settings.getBoolean("isFirstTime", true)) {
            editor.putBoolean("isFirstTime", false);
            editor.apply();

//            Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
//            startActivity(intent);
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page1);

        isFirstTime();
    }
}
