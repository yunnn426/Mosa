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

import com.example.mosa.customer_history.Fragment_Adapter;
import com.example.mosa.recommend.item_Recycler;
import com.example.mosa.recommend.recaccActivity;
import com.example.mosa.recommend.recclothActivity;
import com.example.mosa.recommend.reccosActivity;
import com.example.mosa.recommend.rechairActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PersonalActivity  extends AppCompatActivity {

    //앱에서 나올수 있는 진단의 결과 값
    Map<String,String> result_face=new HashMap<>();
    Map<String,String> result_face_ko=new HashMap<>();

    Map<Integer, String> result_color = new HashMap<>();
    Map<Integer, String> result_color_ko=new HashMap<>();
    TextView User_face;
    TextView User_color_recom;
    TextView User_name;
    TextView Title_User_name;
    TextView Title_User_color;
    FirebaseDatabase firebaseDatabase;

    /*
    ImageButton rec_btn_1;
    ImageButton rec_btn_2;
    ImageButton rec_btn_3;
    ImageButton rec_btn_4;
    */

    ImageView color_title;
    ImageView color_chrt;
    ImageView Face_title;
    ImageView selected_color_1;
    FirebaseFirestore db;
    CollectionReference diagnosesref;
    FirebaseStorage storage;
    RecyclerView itemlist_1;
    RecyclerView itemlist_2;
    RecyclerView itemlist_3;
    RecyclerView itemlist_4;
    File path;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_color);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        color_title=findViewById(R.id.skin_img);
        Intent intent=getIntent();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();//사진을 찍은 날짜를 저장해야,
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");


        result_color.put(0,"spring worm_Light");
        result_color.put(1,"spring worm_Bright");
        result_color.put(2,"summer cool_Light");
        result_color.put(3,"summer cool_Bright");
        result_color.put(4,"summer cool_Mute");
        result_color.put(5,"autumn worm_Deep");
        result_color.put(6,"autumn worm_Mute");
        result_color.put(7,"autumn worm_Strong");
        result_color.put(8,"winter cool_Deep");
        result_color.put(9,"winter cool_Bright");

        result_color_ko.put(0,"봄웜 라이트");
        result_color_ko.put(1,"봄웜 브라이트");
        result_color_ko.put(2,"여름쿨 라이트");
        result_color_ko.put(3,"여름쿨 브라이트");
        result_color_ko.put(4,"여름쿨 뮤트");
        result_color_ko.put(5,"가을웜 딥");
        result_color_ko.put(6,"가을웜 뮤트");
        result_color_ko.put(7,"가을웜 스트롱");
        result_color_ko.put(8,"겨울쿨 딥");
        result_color_ko.put(9,"겨울쿨 브라이트");

        result_face.put("a", "long shape");
        result_face.put("b", "heart shape");
        result_face.put("c", "round shape");
        result_face.put("d", "angular(rhombus) shape");
        result_face.put("e", "egg shape");

        result_face_ko.put("a","긴 얼굴형");
        result_face_ko.put("b","하트 얼굴형");
        result_face_ko.put("c","둥근 얼굴형");
        result_face_ko.put("d","각진 얼굴형(마름모)");
        result_face_ko.put("e","계란 얼굴형");

        /*

        여기에 파이어베이스에서 결과값을 가져오는 코드를 추가해야
        파이어 베이스의 파이어스토어 컬랙션 color_result로 검색을 해서
        (유저이메일+날짜+시간)이미지파일, 결과값을 받아서 결과화면으로 보여주고

        */
        String img_name=intent.getStringExtra("img_name");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference result_call= db.collection("color_result");
        String user_img_name=img_name;
        Query query=result_call.whereEqualTo("file_name",user_img_name);


        //이 값을 위의 배열과 매칭해서 결과를 화면에 보여줌
        int color=intent.getIntExtra("result_color",10);
        String face_re=intent.getStringExtra("result_face");
        String img_file_path=intent.getStringExtra("img");

        String color_str=result_color.get(color);
        String color_str_ko=result_color_ko.get(color);
        String face_str=result_face.get(face_re);
        String face_str_ko=result_face_ko.get(face_re);


        File file_upload=new File(img_file_path);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

//진단 수행중인지, 진단기록을 통해서 다시 추천아이템과 자세한 정보를 보려는지 체크

        myRef.child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                name = value;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("user_img/");
                Uri uri_upload = Uri.fromFile(file_upload);
                //이미지 파일이름 수정해야 (유저이메일+날짜+시간)
                //다시 진단 기록내에 '진단명','진단날짜','진단결과(컬러)','진단결과(얼굴)','이미지파일이름','사용자의 이메일'로 저장
                //String file_name=name+"_"+user.getUid()+"_"+format.format(date);
                String file_name = user_img_name;
                StorageReference imgRef = storageRef.child(file_name);

                imgRef.putFile(uri_upload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String collection = "user_record";
                        String document = "diag_record_" + file_name;
                        String time = format.format(date);
                        Map<String, String> data = new HashMap<>();
                        data.put("diagnosis", "color_and_face");
                        data.put("diagnosis_date", time);
                        data.put("diagnosis_result_color", color_str_ko);
                        data.put("diagnosis_result_face", face_str_ko);
                        data.put("img_file_name", file_name);
                        data.put("user_email", user.getEmail());

                        firebaseFirestore.collection(collection).document(document).set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        item_Recycler recycler_1=new item_Recycler();
        item_Recycler recycler_2=new item_Recycler();
        item_Recycler recycler_3=new item_Recycler();
        item_Recycler recycler_4=new item_Recycler();

        ArrayList<Bitmap> itemfile_1=new ArrayList<Bitmap>();
        ArrayList<Bitmap> itemfile_2=new ArrayList<Bitmap>();
        ArrayList<Bitmap> itemfile_3=new ArrayList<Bitmap>();
        ArrayList<Bitmap> itemfile_4=new ArrayList<Bitmap>();

        User_color_recom=findViewById(R.id.recom_user_color);
        User_name=findViewById(R.id.recom_user_name);
        Title_User_name=findViewById(R.id.title_username);
        Title_User_color=findViewById(R.id.title_usercolor);

        //User_color_recom.setText(result_color_ko[0]);
        User_color_recom.setText(color_str_ko);
        Title_User_color.setText(color_str_ko);
        // 여기서 실제 이름을 가져와서 넣는다.
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef.child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                User_name.setText(value);
                Title_User_name.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        itemlist_1=findViewById(R.id.cos_list);
        itemlist_2=findViewById(R.id.hair_list);
        itemlist_3=findViewById(R.id.acc_list);
        itemlist_4=findViewById(R.id.cloth_list);

        //나중에 경로는 따로 바꾸면 된다. 결과 값을 이전 엑티비티에서 받아서 그 결과 값에 맞는 result_color,result_face 값을 가져오고 그 값으로 스토리지랑 연결
        storage=FirebaseStorage.getInstance();
        //StorageReference storageReference_color_cos=storage.getReference().child(color_str+"cosmetics/");
        //StorageReference storageReference_face_hair=storage.getReference().child(face_str+"/hair/");
        //StorageReference storageReference_color_cloth=storage.getReference().child(color_str+"clothes/");
        //StorageReference storageReference_face_acc=storage.getReference().child(face_str+"/accessory/");

        StorageReference storageReference_1=storage.getReference().child(result_color.get(0)+"/cosmetics/"+"rip/");
        StorageReference storageReference_2=storage.getReference().child(result_color.get(0)+"/cosmetics/"+"blusher/");
        StorageReference storageReference_3=storage.getReference().child(result_color.get(0)+"/cosmetics/"+"shadow/");
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
                                if(itemfile_1.size()==8){
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
                                if(itemfile_2.size()==8){
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
                                if(itemfile_3.size()==8){
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
                                if(itemfile_4.size()==8){
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
        String skin_title=result_color.get(0);
        String skin_detail=intent.getStringExtra("detail");
        Bitmap color_chrt_bmp;//퍼스널 컬러(skin_title)에 알맞는 chrt 이미지 정보를 bitmap의 배열 형식으로 받아온다.
        //해당 추천된 퍼스널 컬러(skin_title,skin_detail)에 알맞는 컬러 차트 이미지와, 컬러 이미지를 스토리지에서 불러옴
        //추천정보(해당 정보와 매치시켜야)를 불러와야한다. 결과값을 인텐트에 담고, 그것을 추천 클래스로 보내면 된다.
        //차후 구현 필요

        //color_chrt=findViewById(R.id.color_chrt);
        selected_color_1=findViewById(R.id.selected_color_chrt_1);


        switch (color) {
            case 0:
                selected_color_1.setImageResource(R.drawable.spring_worm_light_color_chart);
                break;
            case 1:
                selected_color_1.setImageResource(R.drawable.spring_worm_bright_color_chart);
                break;
            case 2:
                selected_color_1.setImageResource(R.drawable.summer_cool_light_color_chart);
                break;
            case 3:
                selected_color_1.setImageResource(R.drawable.summer_cool_bright_color_chart);
                break;
            case 4:
                selected_color_1.setImageResource(R.drawable.summer_cool_mute_color_chart);
                break;
            case 5:
                selected_color_1.setImageResource(R.drawable.autumn_worm_deep_color_chart);
                break;
            case 6:
                selected_color_1.setImageResource(R.drawable.autumn_worm_mute_color_chart);
                break;
            case 7:
                selected_color_1.setImageResource(R.drawable.autumn_worm_strong_color_chart);
                break;
            case 8:
                selected_color_1.setImageResource(R.drawable.winter_cool_deep_color_chart);
                break;
            case 9:
                selected_color_1.setImageResource(R.drawable.winter_cool_bright_color_chart);
                break;
            default:
                //여기에는 오류화면 띄우면 될듯
                break;
        }
        /*
        color_chrt_bmp=match_color_chrt(skin_title);
        color_chrt.setImageBitmap(color_chrt_bmp);

        */

        Face_title=findViewById(R.id.facedes_img);
        User_face=findViewById(R.id.user_face);
        User_face.setText(face_str_ko);

        db=FirebaseFirestore.getInstance();
        diagnosesref=db.collection("user_record");
        //아래에 db에 기록하는 코드를 추가

        /*
        //각각 추천 UI를 불러오는 버튼
        rec_btn_1=findViewById(R.id.recommand_btn_1);
        rec_btn_2=findViewById(R.id.recommand_btn_2);
        rec_btn_3=findViewById(R.id.recommand_btn_3);
        rec_btn_4=findViewById(R.id.recommand_btn_4);

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
        });*/
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
            Intent intent=new Intent(PersonalActivity.this,IntitialActivity.class);
            intent.putExtra("choice","종합진단");
            startActivity(intent);// 뒤로가기 버튼을 눌렀을 때 , 바로 사진 선택 화면으로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
