package com.example.mosa.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    FirebaseStorage storage;
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_screen_cloth);
        color_name=findViewById(R.id.fashion_color);
        //결과 화면에서 해당 결과를 받아온다.
        Intent intent=getIntent();
        String color=intent.getStringExtra("your_color");
        String color_title="#"+color;
        color_name.setText(color_title);
        fashion_list=new ArrayList<Bitmap>();

        cloth_item clothItem=new cloth_item();

        File result_path= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        //여기에 파이어베이스 스토리지를 연결해서 해당하는 이미지들을 가져온다.
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

