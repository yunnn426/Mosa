package com.example.mosa.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mosa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class recclothActivity extends AppCompatActivity {

    ArrayList<Bitmap> fashion_list;
    TextView color_name;
    ImageButton bck_btn;
    RecyclerView fashion_view;
    FirebaseStorage storage;
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.recommend_screen_cloth);
        color_name=findViewById(R.id.fashion_color);
        bck_btn=findViewById(R.id.fashion_back);
        //결과 화면에서 해당 결과를 받아온다.
        Intent intent=getIntent();
        String color=intent.getStringExtra("your_color");
        String color_title="#"+color;
        color_name.setText(color_title);
        fashion_list=new ArrayList<Bitmap>();
        fashion_view=findViewById(R.id.fashion_recyc);
        cloth_item clothItem=new cloth_item();


        bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); //바로 전 화면으로 이동
            }
        });

        File result_path= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        //여기에 파이어베이스 스토리지를 연결해서 해당하는 이미지들을 가져온다.
        storage=FirebaseStorage.getInstance();
        StorageReference storageReference=storage.getReference().child(color+"/clothes/");
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                path=new File(result_path.getAbsolutePath());
                for(StorageReference item : listResult.getItems()){
                    File download=new File(path,item.getName());
                    if(!path.exists()){
                        path.getParentFile().mkdirs();
                    }
                    try {
                        download.createNewFile();
                        FileDownloadTask fileDownloadTask=item.getFile(download);

                        fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap= BitmapFactory.decodeFile(download.getAbsolutePath());
                                fashion_list.add(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                clothItem.setcloth_item(fashion_list);
                                StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL);
                                fashion_view.setLayoutManager(staggeredGridLayoutManager);
                                fashion_view.setAdapter(clothItem);
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}

