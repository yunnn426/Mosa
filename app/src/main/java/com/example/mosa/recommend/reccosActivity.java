package com.example.mosa.recommend;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//이 엑티비티는 화장품 추천정보를 보여준다.
public class reccosActivity extends AppCompatActivity {
    RecyclerView itemlist_1;
    RecyclerView itemlist_2;
    RecyclerView itemlist_3;
    FirebaseStorage storage;
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        item_Recycler recycler_1=new item_Recycler();
        item_Recycler recycler_2=new item_Recycler();
        item_Recycler recycler_3=new item_Recycler();
        ArrayList<Bitmap> itemfile_1=new ArrayList<>();
        ArrayList<Bitmap> itemfile_2=new ArrayList<>();
        ArrayList<Bitmap> itemfile_3=new ArrayList<>();
        setContentView(R.layout.recommend_screen_cos);
        //이거는 화장품의 종류 별로 각각 리사이클러 뷰를 표시한다.
        itemlist_1=findViewById(R.id.itemlist_view_1);
        itemlist_2=findViewById(R.id.itemlist_view_2);
        itemlist_3=findViewById(R.id.itemlist_view_3);
        int itemnum=10;
        //여기서 결과 값을 받고 이 결과값을 이용해서 추천아이템을 검색
        Intent intent=getIntent();
        String result=intent.getStringExtra("result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //test용입니다.
        storage=FirebaseStorage.getInstance();
        //StorageReference storageReference = storage.getReference().child("test/");
        //StorageReference storageReference1 =storage.getReference().child("text_ex/");


        StorageReference storageReference=storage.getReference().child(result+"/cosmetics/"+"rip/");
        StorageReference storageReference1=storage.getReference().child(result+"/cosmetics/"+"blusher/");
        StorageReference storageReference2=storage.getReference().child(result+"/cosmetics/"+"shadow/");

        File result_path= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                path=new File(result_path.getAbsolutePath());
                for (StorageReference item : listResult.getItems()) {
                    //다운로드 받을 파일의 이름을 지정
                    File download=new File(path,item.getName());
                    if(!path.exists()){
                        path.getParentFile().mkdirs();
                    }
                    try {
                        //파일을 생성
                        download.createNewFile();
                        //파일을 다운로드할 작업을 정의
                        FileDownloadTask fileDownloadTask=item.getFile(download);
                        fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //제대로 비트맵 이미지를 받아온 것을 확인, 실제 경로 상에 파일이 존재, 배열도 확인
                                Bitmap bitmap=BitmapFactory.decodeFile(download.getAbsolutePath());
                                itemfile_1.add(bitmap);
                                if(itemfile_1.size()==4){
                                    recycler_1.setrecycler(itemfile_1);
                                    itemlist_1.setLayoutManager(new LinearLayoutManager(reccosActivity.this, RecyclerView.HORIZONTAL, false));
                                    itemlist_1.setAdapter(recycler_1);
                                }
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
                Toast.makeText(reccosActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });



        storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                path=new File(result_path.getAbsolutePath());
                for (StorageReference item : listResult.getItems()) {
                    //다운로드 받을 파일의 이름을 지정
                    File download=new File(path,item.getName());
                    if(!path.exists()){
                        path.getParentFile().mkdirs();
                    }
                    try {
                        //파일을 생성
                        download.createNewFile();
                        //파일을 다운로드할 작업을 정의
                        FileDownloadTask fileDownloadTask=item.getFile(download);
                        fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //제대로 비트맵 이미지를 받아온 것을 확인, 실제 경로 상에 파일이 존재, 배열도 확인
                                Bitmap bitmap=BitmapFactory.decodeFile(download.getAbsolutePath());
                                itemfile_2.add(bitmap);
                                if(itemfile_2.size()==4 ){
                                    recycler_2.setrecycler(itemfile_2);
                                    itemlist_2.setLayoutManager(new LinearLayoutManager(reccosActivity.this, RecyclerView.HORIZONTAL, false));
                                    itemlist_2.setAdapter(recycler_2);
                                }
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
                Toast.makeText(reccosActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        storageReference2.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                path=new File(result_path.getAbsolutePath());
                for (StorageReference item : listResult.getItems()) {
                    //다운로드 받을 파일의 이름을 지정
                    File download=new File(path,item.getName());
                    if(!path.exists()){
                        path.getParentFile().mkdirs();
                    }
                    try {
                        //파일을 생성
                        download.createNewFile();
                        //파일을 다운로드할 작업을 정의
                        FileDownloadTask fileDownloadTask=item.getFile(download);
                        fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //제대로 비트맵 이미지를 받아온 것을 확인, 실제 경로 상에 파일이 존재, 배열도 확인
                                Bitmap bitmap=BitmapFactory.decodeFile(download.getAbsolutePath());
                                itemfile_3.add(bitmap);
                                if(itemfile_3.size()==4){
                                    recycler_3.setrecycler(itemfile_3);
                                    itemlist_3.setLayoutManager(new LinearLayoutManager(reccosActivity.this, RecyclerView.HORIZONTAL, false));
                                    itemlist_3.setAdapter(recycler_3);
                                }
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
                Toast.makeText(reccosActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });


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
