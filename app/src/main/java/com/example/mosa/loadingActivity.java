package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class loadingActivity extends AppCompatActivity {
    int result_color;
    int result_face;
    TextView loading_text;
    ImageView loading_img;
    ImageView res_img;
    Bitmap user_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_model);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        String image_path=intent.getStringExtra("img");
        File file=new File(image_path);
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


        loading_img.startAnimation(rotation);

        //둘중 한곳에 모델 실행 코드를 넣자.
        /*
        여기에 퍼스널 컬러 모델,얼굴형 측정 모델을 실행하는 코드를 넣어서 실행을 하고 결과 값을 받으면 될것 같다. user_img를 이용
        이 값들을 이용해서 적절한 res_img를 매칭시킨다.
        result_color=color_model(user_img);
        result_face=face_model(user_img);

        res_img.setImageBitmap(R.drawable.color_result_1);
        */

        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            /*
                여기에 퍼스널 컬러 모델,얼굴형 측정 모델을 실행하는 코드를 넣어서 실행을 하고 결과 값을 받으면 될것 같다. user_img를 이용
                이 값들을 이용해서 적절한 res_img를 매칭시킨다.
                result_color=color_model(user_img);
                result_face=face_model(user_img);

                res_img.setImageBitmap(R.drawable.color_result_1);
             */
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션이 종료되면 실행될 작업
                loading_text.setText("컬러를 찾았습니다!!");

                loading_img.setVisibility(View.INVISIBLE);
                res_img.setVisibility(View.VISIBLE);

                cons.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //이곳을 나중에 퍼스널 컬러별 색으로 바꾸면 될듯
                        cons.setBackgroundColor(Color.RED);
                    }
                }, 3000);
                cons.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction()==event.ACTION_DOWN){
                            Intent intent_result=new Intent(loadingActivity.this,PersonalActivity.class);
                            intent_result.putExtra("img",image_path);
                            intent_result.putExtra("result_color",6);
                            intent_result.putExtra("result_face",3);
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
