package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.databinding.ActivityMainBinding;
import com.example.mosa.recommend.hair_recycler;
import com.example.mosa.recommend.item_Recycler;
import com.example.mosa.recommend.recclothActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.io.File;
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
    ArrayList<Bitmap> itemfile_1;
    ArrayList<Bitmap> itemfile_2;
    ArrayList<Bitmap> itemfile_4;

    ArrayList<String> itemdetail_1;
    ArrayList<String> itemdetail_2;
    ArrayList<String> itemdetail_4;

    TextView User_face;
    TextView User_color_recom;
    TextView User_color_recom2;
    TextView User_name;
    TextView Title_User_name;
    TextView Title_User_color;
    TextView color_detail;
    FirebaseDatabase firebaseDatabase;
    MotionLayout motionLayout;

    ImageView color_title;
    ImageView color_chrt;
    ImageView Face_title;
    ImageView selected_color_1;
    FirebaseFirestore db;
    CollectionReference diagnosesref;
    FirebaseStorage storage;
    RecyclerView itemlist_1;
    RecyclerView itemlist_2;
    RecyclerView itemlist_4;
    CarouselRecyclerview itemhair;
    File path;
    String name;

    //닫기용 버튼
    ImageButton closeBtn;

    Button fas_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        진단기록에서 자세히 보기를 클릭하면, 해당 하는 결과값을 토대로 결과화면을 제공한다
        img,img_name 값이 사실상 없다.
        진단기록에 등록하면 안되고, 해당 변수를 없다고 인식해야 한다.
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_color);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        color_title=findViewById(R.id.skin_img);
        color_detail=findViewById(R.id.color_detail);
        Intent intent=getIntent();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();//사진을 찍은 날짜를 저장해야,
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        fas_btn=findViewById(R.id.fashion_search);
        //닫기
        closeBtn = findViewById(R.id.close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //메인 머지 후 수정 예정
                Intent intent = new Intent(PersonalActivity.this, CustomerInfo.class);
                startActivity(intent);
            }
        });

        result_color.put(0,"spring warm_Light");
        result_color.put(1,"spring warm_Bright");
        result_color.put(2,"summer cool_Light");
        result_color.put(3,"summer cool_Bright");
        result_color.put(4,"summer cool_Mute");
        result_color.put(5,"autumn warm_Deep");
        result_color.put(6,"autumn warm_Mute");
        result_color.put(7,"autumn warm_Strong");
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
        result_face_ko.put("d","각진 얼굴형");
        result_face_ko.put("e","계란 얼굴형");

        /*

        여기에 파이어베이스에서 결과값을 가져오는 코드를 추가해야
        파이어 베이스의 파이어스토어 컬랙션 color_result로 검색을 해서
        (유저이메일+날짜+시간)이미지파일, 결과값을 받아서 결과화면으로 보여주고

        */
        String img_name=intent.getStringExtra("img_name");
        int color=intent.getIntExtra("result_color",10);
        String face_re=intent.getStringExtra("result_face");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference result_call= db.collection("color_result");
        String user_img_name;
        File file_upload;
        String img_file_path;
        //



        //이 값을 위의 배열과 매칭해서 결과를 화면에 보여줌



        String color_str=result_color.get(color);
        String color_str_ko=result_color_ko.get(color);
        String face_str=result_face.get(face_re);
        String face_str_ko=result_face_ko.get(face_re);

        img_file_path = intent.getStringExtra("img");




        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

//진단 수행중인지, 진단기록을 통해서 다시 추천아이템과 자세한 정보를 보려는지 체크
        if(!img_file_path.equals("is_record")&&!img_name.equals("is_record")) {
            user_img_name = img_name;
            Query query = result_call.whereEqualTo("file_name", user_img_name);
            file_upload = new File(img_file_path);
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
        }


        item_Recycler recycler_1=new item_Recycler();
        item_Recycler recycler_2=new item_Recycler();
        hair_recycler recyc=new hair_recycler();
        item_Recycler recycler_4=new item_Recycler();

        itemfile_1=new ArrayList<Bitmap>();
        itemfile_2=new ArrayList<Bitmap>();
        itemfile_4=new ArrayList<Bitmap>();

        itemdetail_1=new ArrayList<String>();
        itemdetail_2=new ArrayList<String>();
        itemdetail_4=new ArrayList<String>();

        User_color_recom=findViewById(R.id.recom_user_color);
        User_color_recom2 = findViewById(R.id.recom_user_color2);
        User_name=findViewById(R.id.recom_user_name);
        Title_User_name=findViewById(R.id.title_username);
        Title_User_color=findViewById(R.id.title_usercolor);

        //User_color_recom.setText(result_color_ko[0]);
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
        itemhair=findViewById(R.id.hair_carousel);
        //itemlist_4=findViewById(R.id.cloth_list);

        //나중에 경로는 따로 바꾸면 된다. 결과 값을 이전 엑티비티에서 받아서 그 결과 값에 맞는 result_color,result_face 값을 가져오고 그 값으로 스토리지랑 연결
        storage=FirebaseStorage.getInstance();
        StorageReference storageReference_1=storage.getReference().child(color_str+"/cosmetics/");
        StorageReference storageReference_2=storage.getReference().child(face_str+"/");
        StorageReference storageReference_3=storage.getReference().child(color_str+"/clothes/");
        //폴더에 파일이 없으면 안나온다;

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
                                String itemfilename=download.getName();
                                String itemname=itemfilename.substring(0, itemfilename.length() - 4);
                                itemdetail_1.add(itemname);
                                itemfile_1.add(bitmap);
                                //여기에 데이터가 더이상 남지 않으면 리사이클러 뷰로 보여주는 조건을 추가한다.
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                recycler_1.setrecycler(itemfile_1,itemdetail_1);
                                itemlist_1.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
                                itemlist_1.setAdapter(recycler_1);

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
                                String itemfilename=download.getName();
                                String itemname=itemfilename.substring(0, itemfilename.length() - 4);
                                itemdetail_2.add(itemname);
                                itemfile_2.add(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                recycler_2.setrecycler(itemfile_2,itemdetail_2);
                                recyc.setrecycler(itemfile_2);
                                //itemhair.setIntervalRatio(0.5f);
                                itemhair.setAlpha(false);
                                //itemhair.setFlat(false);
                                itemhair.setOrientation(RecyclerView.VERTICAL);
                                itemhair.setIsScrollingEnabled(true);
                                //itemhair.setInfinite(false);
                                itemhair.set3DItem(true);
                                itemhair.setAdapter(recyc);
                                //itemlist_2.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
                                //itemlist_2.setLayoutManager(layoutManager);
                                //itemlist_2.setAdapter(recycler_2);
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
                                String itemfilename=download.getName();
                                String itemname=itemfilename.substring(0, itemfilename.length() - 4);
                                itemdetail_4.add(itemname);
                                itemfile_4.add(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                //recycler_4.setrecycler(itemfile_4,itemdetail_4);
                                //itemlist_4.setLayoutManager(new LinearLayoutManager(PersonalActivity.this, RecyclerView.HORIZONTAL, false));
                                //itemlist_4.setAdapter(recycler_4);
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


        User_face=findViewById(R.id.face_title);
        User_face.setText(face_str_ko);


        switch (color_str) {
            case "spring warm_Light":
                selected_color_1.setImageResource(R.drawable.spring_warm_light_color_chart);
                color_title.setImageResource(R.drawable.spring_warm_light_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.spring_warm_light));
                User_color_recom2.setTextColor(getColor(R.color.spring_warm_light));

                User_face.setTextColor(getColor(R.color.spring_warm_light));

                Title_User_color.setTextColor(getColor(R.color.spring_warm_light));
                //color_title.setBackgroundColor(getColor(R.color.spring_worm_light));
                color_detail.setText(R.string.spring_warm_Light);
                fas_btn.setText("spring warm_Light");
                fas_btn.setTextColor(getColor(R.color.spring_warm_light));
                break;
            case "spring warm_Bright":
                selected_color_1.setImageResource(R.drawable.spring_warm_bright_color_chart);
                color_title.setImageResource(R.drawable.spring_warm_bright_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.spring_warm_bright));
                User_color_recom2.setTextColor(getColor(R.color.spring_warm_bright));

                User_face.setTextColor(getColor(R.color.spring_warm_bright));

                Title_User_color.setTextColor(getColor(R.color.spring_warm_bright));
                //color_title.setBackgroundColor(getColor(R.color.spring_worm_bright));
                color_detail.setText(R.string.spring_warm_Bright);
                fas_btn.setText("spring warm_Bright");
                fas_btn.setTextColor(getColor(R.color.spring_warm_bright));
                break;
            case "summer cool_Light":
                selected_color_1.setImageResource(R.drawable.summer_cool_light_color_chart);
                color_title.setImageResource(R.drawable.summer_cool_light_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.summer_cool_light));
                User_color_recom2.setTextColor(getColor(R.color.summer_cool_light));

                User_face.setTextColor(getColor(R.color.summer_cool_light));

                Title_User_color.setTextColor(getColor(R.color.summer_cool_light));
                //color_title.setBackgroundColor(getColor(R.color.summer_cool_light));
                color_detail.setText(R.string.summer_cool_Light);
                fas_btn.setText("summer cool_Light");
                fas_btn.setTextColor(getColor(R.color.summer_cool_light));
                break;
            case "summer cool_Bright":
                selected_color_1.setImageResource(R.drawable.summer_cool_bright_color_chart);
                color_title.setImageResource(R.drawable.summer_cool_bright_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.summer_cool_bright));
                User_color_recom2.setTextColor(getColor(R.color.summer_cool_bright));

                User_face.setTextColor(getColor(R.color.summer_cool_bright));

                Title_User_color.setTextColor(getColor(R.color.summer_cool_bright));
                //color_title.setBackgroundColor(getColor(R.color.summer_cool_bright));
                color_detail.setText(R.string.summer_cool_Bright);
                fas_btn.setText("summer cool_Bright");

                fas_btn.setTextColor(getColor(R.color.summer_cool_bright));
                break;
            case "summer cool_Mute":
                selected_color_1.setImageResource(R.drawable.summer_cool_mute_color_chart);
                color_title.setImageResource(R.drawable.summer_cool_mute_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.summer_cool_mute));
                User_color_recom2.setTextColor(getColor(R.color.summer_cool_mute));

                User_face.setTextColor(getColor(R.color.summer_cool_mute));

                Title_User_color.setTextColor(getColor(R.color.summer_cool_mute));
                //color_title.setBackgroundColor(getColor(R.color.summer_cool_mute));
                color_detail.setText(R.string.summer_cool_Mute);
                fas_btn.setText("summer cool_Mute");
                fas_btn.setTextColor(getColor(R.color.summer_cool_mute));
                break;
            case "autumn warm_Deep":
                selected_color_1.setImageResource(R.drawable.autumn_warm_deep_color_chart);
                color_title.setImageResource(R.drawable.autumn_warm_deep_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.autumn_warm_deep));
                User_color_recom2.setTextColor(getColor(R.color.autumn_warm_deep));

                User_face.setTextColor(getColor(R.color.autumn_warm_deep));

                Title_User_color.setTextColor(getColor(R.color.autumn_warm_deep));
                //color_title.setBackgroundColor(getColor(R.color.autumn_worm_deep));
                color_detail.setText(R.string.autumn_warm_Deep);
                fas_btn.setText("autumn warm_Deep");
                fas_btn.setTextColor(getColor(R.color.autumn_warm_deep));
                break;
            case "autumn warm_Mute":
                selected_color_1.setImageResource(R.drawable.autumn_warm_mute_color_chart);
                color_title.setImageResource(R.drawable.autumn_warm_mute_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.autumn_warm_mute));
                User_color_recom2.setTextColor(getColor(R.color.autumn_warm_mute));

                User_face.setTextColor(getColor(R.color.autumn_warm_mute));

                Title_User_color.setTextColor(getColor(R.color.autumn_warm_mute));
                //color_title.setBackgroundColor(getColor(R.color.autumn_worm_mute));
                color_detail.setText(R.string.autumn_warm_Mute);
                fas_btn.setText("autumn warm_Mute");
                fas_btn.setTextColor(getColor(R.color.autumn_warm_mute));
                break;
            case "autumn warm_Strong":
                selected_color_1.setImageResource(R.drawable.autumn_warm_strong_color_chart);
                color_title.setImageResource(R.drawable.autumn_warm_strong_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.autumn_warm_strong));
                User_color_recom2.setTextColor(getColor(R.color.autumn_warm_strong));

                User_face.setTextColor(getColor(R.color.autumn_warm_strong));

                Title_User_color.setTextColor(getColor(R.color.autumn_warm_strong));
                //color_title.setBackgroundColor(getColor(R.color.autumn_worm_strong));
                color_detail.setText(R.string.autumn_warm_Strong);
                fas_btn.setText("autumn warm_Strong");
                fas_btn.setTextColor(getColor(R.color.autumn_warm_strong));
                break;
            case "winter cool_Deep":
                selected_color_1.setImageResource(R.drawable.winter_cool_deep_color_chart);
                color_title.setImageResource(R.drawable.winter_cool_deep_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.winter_cool_deep));
                User_color_recom2.setTextColor(getColor(R.color.winter_cool_deep));

                User_face.setTextColor(getColor(R.color.winter_cool_deep));

                Title_User_color.setTextColor(getColor(R.color.winter_cool_deep));
                //color_title.setBackgroundColor(getColor(R.color.winter_cool_deep));
                color_detail.setText(R.string.winter_cool_Deep);
                fas_btn.setText("winter cool_Deep");
                fas_btn.setTextColor(getColor(R.color.winter_cool_deep));
                break;
            case "winter cool_Bright":
                selected_color_1.setImageResource(R.drawable.winter_cool_bright_color_chart);
                color_title.setImageResource(R.drawable.winter_cool_bright_img);

                User_color_recom.setText(color_str_ko);
                User_color_recom2.setText(color_str_ko);

                Title_User_color.setText(color_str_ko);

                User_color_recom.setTextColor(getColor(R.color.winter_cool_bright));
                User_color_recom2.setTextColor(getColor(R.color.winter_cool_bright));

                User_face.setTextColor(getColor(R.color.winter_cool_bright));

                Title_User_color.setTextColor(getColor(R.color.winter_cool_bright));
                //color_title.setBackgroundColor(getColor(R.color.winter_cool_bright));
                color_detail.setText(R.string.winter_cool_Bright);
                fas_btn.setText("winter cool_Bright");
                fas_btn.setTextColor(getColor(R.color.winter_cool_bright));
                break;
            default:
                //여기에는 오류화면 띄우면 될듯
                break;
        }


        db=FirebaseFirestore.getInstance();
        diagnosesref=db.collection("user_record");
        //아래에 db에 기록하는 코드를 추가

        fas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_fas=new Intent(PersonalActivity.this, recclothActivity.class);
                intent_fas.putExtra("your_color",fas_btn.getText().toString());
                startActivity(intent_fas);
            }
        });

    }

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
