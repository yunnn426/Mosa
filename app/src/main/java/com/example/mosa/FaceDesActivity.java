package com.example.mosa;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;

public class FaceDesActivity extends AppCompatActivity {

    Button btn1;
    ImageView face_img;

    FirebaseFirestore db;
    CollectionReference diagnosesref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_description_screen);
        face_img=findViewById(R.id.face_img);

        db=FirebaseFirestore.getInstance();
        diagnosesref=db.collection("user_record");

        getSupportActionBar().hide();
        Intent intent=getIntent();
        String image_path=intent.getStringExtra("img");
        File file=new File(image_path);
        Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);

        try{
            Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            face_img.setImageBitmap(bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        String shape_title=intent.getStringExtra("title");
        String shape_detail=intent.getStringExtra("detail");
        //해당 피부정보(shape_title,shape_detail)를 불러옵니다.

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
