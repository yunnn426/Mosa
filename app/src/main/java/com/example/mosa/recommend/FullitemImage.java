package com.example.mosa.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mosa.BuildConfig;
import com.example.mosa.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;

public class FullitemImage extends AppCompatActivity {
    ImageView imageView;
    ImageButton backbtn;
    TextView item_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullitemimage);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        imageView = findViewById(R.id.full_image_view);
        item_detail=findViewById(R.id.item_detail);
        backbtn=findViewById(R.id.item_back);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("bitmap_url");
        String item_des=intent.getStringExtra("item_detail");
        File file=new File(imageUrl);
        Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);
        try{
            Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            imageView.setImageBitmap(bitmap);
            item_detail.setText(item_des);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 버튼을 눌렀을 때 , 바로 이전 화면으로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
