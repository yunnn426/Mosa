package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class loadingActivity extends AppCompatActivity {
    int result_color;
    int result_face;

    String name;
    int result_1=0;
    String result_2;
    String result_3;
    TextView loading_text;
    ImageView loading_img;
    ImageView res_img;
    Bitmap user_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_model);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        String image_path=intent.getStringExtra("img");
        File file=new File(image_path);

        Date date = new Date();//사진을 찍은 날짜를 저장해야,
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String user_img_name=user.getEmail()+"_"+format.format(date);
        File user_img_file=new File(image_path);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("user_image/");
        Uri uri_upload=Uri.fromFile(user_img_file);
        StorageReference storageRef_2=storageRef.child(user_img_name);


        Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);
        try{
            user_img= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        ConstraintLayout cons=findViewById(R.id.loading_view);
        loading_text=findViewById(R.id.loading_text);
        loading_img=findViewById(R.id.loading_img);
        res_img=findViewById(R.id.res_clr);
        final Animation rotation = AnimationUtils.loadAnimation(loadingActivity.this,R.anim.model_loading_animation);

        rotation.setRepeatCount(Animation.INFINITE);
        loading_img.startAnimation(rotation);

        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                /*
                여기에 사용자의 이미지를 전송하는 코드를 집어넣어야
                */

                storageRef_2.putFile(uri_upload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                /*
                여기에 color_result 컬렉션에 파일 이미지 이름 값을 넣고 모델은 그 이름을 이용해서 이미지를 찾고
                그 이미지로 진단을 하고나서 진단의 결과값을 해당 문서에 넣고, 그 값을 안드로이드 스튜디오가 읽어서 사용자의 화면상에 보여준다.
                 */
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference coloref = db.collection(user.getEmail()+"_color_result");
                        coloref.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value,FirebaseFirestoreException error) {
                                // "사용자의 이메일"+_color_result 컬렉션에 데이터가 추가/변경/제거되었을 때 실행되는 코드
                                // 즉 파이썬의 머신러닝 모델에 의해서 데이터가 추가될시 즉시 사용한 이미지의 이름으로 그 결과값을 검색해서
                                // 결과값을 읽어온다.
                                Query query=coloref.whereEqualTo("file_name",user_img_name);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            result_1=documentSnapshot.getLong("color_result").intValue();
                                            result_2=documentSnapshot.getString("face_result");
                                            result_3=documentSnapshot.getString("file_name");

                                            rotation.cancel();
                                            //저위의 3개의 값을 인텐트로 전달해야
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(loadingActivity.this,"실행상에 오류가 있습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });

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
            public void onAnimationEnd(Animation animation) {
                // 애니메이션이 종료되면 실행될 작업

                loading_img.clearAnimation();
                loading_text.setText("컬러를 찾았습니다!!");

                loading_img.setVisibility(View.INVISIBLE);
                res_img.setVisibility(View.VISIBLE);

                //결과 값을 받아서 그거에 맞는 ui 요소를 표시
                /*
                String rst_color="result_"+result_color;
                int drawable_id=getResources().getIdentifier(rst_color,"drawable",getPackageName());
                res_img.setImageResource(drawable_id);
                */
                //임시로 표시
                res_img.setImageResource(R.drawable.result_1);

                cons.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //이곳을 나중에 퍼스널 컬러별 색으로 바꾸면 될듯
                        cons.setBackgroundColor(Color.parseColor("#FF4D4D"));
                    }
                }, 2000);
                cons.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction()==event.ACTION_DOWN){
                            Intent intent_result=new Intent(loadingActivity.this,PersonalActivity.class);
                            intent_result.putExtra("img",image_path);

                            intent_result.putExtra("result_color",result_1);
                            intent_result.putExtra("result_face",result_2);

                            //intent_result.putExtra("result_color",6);
                            //intent_result.putExtra("result_face",3);
                            intent_result.putExtra("img_name",user_img_name);
                            intent_result.putExtra("Isdiag","yes");
                            startActivity(intent_result);
                            return true;
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
            Intent intent=new Intent(loadingActivity.this,IntitialActivity.class);
            intent.putExtra("choice","종합진단");
            startActivity(intent);// 뒤로가기 버튼을 눌렀을 때 , 바로 사진 선택 화면으로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
