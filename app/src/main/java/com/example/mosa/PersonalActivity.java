package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.recommend.item_Recycler;
import com.example.mosa.recommend.recaccActivity;
import com.example.mosa.recommend.recclothActivity;
import com.example.mosa.recommend.reccosActivity;
import com.example.mosa.recommend.rechairActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PersonalActivity  extends AppCompatActivity {

    //앱에서 나올수 있는 진단의 결과 값
    final static String[] result_color={"spring worm_Bright","spring worm_Light",
            "summer cool_Bright","summer cool_Light","summer cool_Mute","autumn worm_Deep","autumn worm_Mute",
            "autumn worm_Strong","winter_Bright","winter_Bright","winter_Deep","알 수 없는 오류 발생!!!"};
    final static String[] result_face={"square","round","oval","oblong","heart","알 수 없는 오류 발생!!!"};
    int i;
    TextView User_color_en;
    TextView User_color;
    TextView User_color_detail;
    TextView User_face_en;
    TextView User_face;
    ImageButton rec_btn_1;
    ImageButton rec_btn_2;
    ImageButton rec_btn_3;
    ImageButton rec_btn_4;
    ImageView color_title;
    ImageView color_chrt;
    ImageView Face_title;
    ImageView selected_color;
    FirebaseFirestore db;
    CollectionReference diagnosesref;
    FirebaseStorage storage;
    RecyclerView itemlist_1;
    RecyclerView itemlist_2;
    RecyclerView itemlist_3;
    RecyclerView itemlist_4;
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_color);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        color_title=findViewById(R.id.skin_img);
        Intent intent=getIntent();

        item_Recycler recycler_1=new item_Recycler();
        item_Recycler recycler_2=new item_Recycler();
        item_Recycler recycler_3=new item_Recycler();
        item_Recycler recycler_4=new item_Recycler();

        ArrayList<Bitmap> itemfile_1=new ArrayList<Bitmap>();
        ArrayList<Bitmap> itemfile_2=new ArrayList<Bitmap>();
        ArrayList<Bitmap> itemfile_3=new ArrayList<Bitmap>();
        ArrayList<Bitmap> itemfile_4=new ArrayList<Bitmap>();

        itemlist_1=findViewById(R.id.cos_list);
        itemlist_2=findViewById(R.id.hair_list);
        itemlist_3=findViewById(R.id.acc_list);
        itemlist_4=findViewById(R.id.cloth_list);

        storage=FirebaseStorage.getInstance();
        StorageReference storageReference_1=storage.getReference().child(result_color[0]+"/cosmetics/"+"rip/");
        StorageReference storageReference_2=storage.getReference().child(result_color[0]+"/cosmetics/"+"blusher/");
        StorageReference storageReference_3=storage.getReference().child(result_color[0]+"/cosmetics/"+"shadow/");
        StorageReference storageReference_4=storageReference_3;

        File result_path= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        storageReference_1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
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
                                    itemlist_1.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
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
                Toast.makeText(PersonalActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });



        storageReference_2.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
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
                                    itemlist_2.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
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
                Toast.makeText(PersonalActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        storageReference_3.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
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
                                    itemlist_3.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
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
                Toast.makeText(PersonalActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        storageReference_4.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
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
                                itemfile_4.add(bitmap);
                                if(itemfile_4.size()==4){
                                    recycler_4.setrecycler(itemfile_4);
                                    itemlist_4.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
                                    itemlist_4.setAdapter(recycler_4);
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
                Toast.makeText(PersonalActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        String image_path=intent.getStringExtra("img");
        File file=new File(image_path);
        Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);

        try{
            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            color_title.setImageBitmap(bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        //진단 실행화면에서 결과값을 받아오고 그 결과값(숫자)에 맞는 문자열을 매칭 시킨다
        //int skin_int=intent.getIntExtra("result",10);
        //String skin_title=result_color[skin_int];
        //잠시 임의로 지정
        String skin_title=result_color[0];
        String skin_detail=intent.getStringExtra("detail");
        Bitmap color_chrt_bmp;//퍼스널 컬러(skin_title)에 알맞는 chrt 이미지 정보를 bitmap의 배열 형식으로 받아온다.
        //해당 추천된 퍼스널 컬러(skin_title,skin_detail)에 알맞는 컬러 차트 이미지와, 컬러 이미지를 스토리지에서 불러옴
        //추천정보(해당 정보와 매치시켜야)를 불러와야한다. 결과값을 인텐트에 담고, 그것을 추천 클래스로 보내면 된다.
        //차후 구현 필요
        User_color_en=findViewById(R.id.user_color_en);
        User_color=findViewById(R.id.user_color);
        User_color_detail=findViewById(R.id.user_color_detail);
        color_chrt=findViewById(R.id.color_chrt);
        selected_color=findViewById(R.id.selected_color_chrt);
        /*
        color_chrt_bmp=match_color_chrt(skin_title);
        color_chrt.setImageBitmap(color_chrt_bmp);

        */

        User_color.setText(skin_title);
        User_color_detail.setText(skin_detail);

        Face_title=findViewById(R.id.facedes_img);
        User_face_en=findViewById(R.id.user_face_en);
        User_face=findViewById(R.id.user_face);

        //각각 추천 UI를 불러오는 버튼
        rec_btn_1=findViewById(R.id.recommand_btn_1);
        rec_btn_2=findViewById(R.id.recommand_btn_2);
        rec_btn_3=findViewById(R.id.recommand_btn_3);
        rec_btn_4=findViewById(R.id.recommand_btn_4);

        db=FirebaseFirestore.getInstance();
        diagnosesref=db.collection("user_record");
        //아래에 db에 기록하는 코드를 추가

        //각각 의 추천 엑티비티를 불러오는 리스너
        rec_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalActivity.this, reccosActivity.class);
                intent.putExtra("result",skin_title);
                startActivity(intent);
            }
        });
        rec_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalActivity.this, rechairActivity.class);
                //얼굴형+퍼스널 컬러의 정보를 써야할 것 같다.
            }
        });
        rec_btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalActivity.this, recaccActivity.class);
                //얼굴형+퍼스널 컬러의 정보를 써야할 것 같다.
            }
        });
        rec_btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalActivity.this, recclothActivity.class);
            }
        });
    }

    //고객의 퍼스널 컬러 정보를 받아서 해당 컬러에 맞는 chrt 정보를 불러온다.
    /*
    public Bitmap match_color_chrt(String your_color){
        Log.d("User","당신의 퍼스널 컬러에 맞는 chrt 정보를 불러옵니다.");
        Bitmap your_color_chart;
        //스토리지에 컬러 차트 이미지 저장하고 불러온다.

        return your_color_chart;
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu); //우상단 메뉴 버튼
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
