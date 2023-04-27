package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mosa.recommend.recaccActivity;
import com.example.mosa.recommend.recclothActivity;
import com.example.mosa.recommend.reccosActivity;
import com.example.mosa.recommend.rechairActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;

public class PersonalActivity  extends AppCompatActivity {

    //앱에서 나올수 있는 진단의 결과 값
    final static String[] result_color={"springworm_Bright","springworm_Light",
            "summer cool_Bright","summer cool_Light","summer cool_Mute","autumn worm_Deep","autumn worm_Mute",
            "autumn worm_Strong","winter_Bright","winter_Bright","winter_Deep"};
    final static String[] result_face={"square","round","oval","oblong","heart"};
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_color);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        color_title=findViewById(R.id.skin_img);
        Intent intent=getIntent();

        db=FirebaseFirestore.getInstance();
        diagnosesref=db.collection("user_record");
        //아래에 db에 기록하는 코드를 추가

        String image_path=intent.getStringExtra("img");
        File file=new File(image_path);
        Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);

        try{
            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            color_title.setImageBitmap(bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        //String skin_title=intent.getStringExtra("title");
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
        //여기도 차후에 구현
        //일단 아무거나 넣었다.

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
