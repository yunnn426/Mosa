package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomerInfo extends AppCompatActivity {

    TextView customer_name;
    TextView customer_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_info);
        customer_name=findViewById(R.id.customer_name);
        customer_email=findViewById(R.id.customer_email);

        Intent intent=getIntent();
        //이 화면에서 스타일 진단 화면으로 가려면 얼굴 이미지 경로, ml_kit 얼굴 분석정보가 필요하다.
        String img=intent.getStringExtra("img");
        String name=intent.getStringExtra("username");
        String email=intent.getStringExtra("useremail");

        customer_name.setText(name);
        customer_email.setText(email);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            switch (item.getItemId()){

                case R.id.bottom_menu_1:
                {
                    //여기서 스타일 진단 화면으로 넘어가려면 사진 정보를 전달할 필요가 있다.
                    Toast.makeText(this,"고객님의 이미지로 스타일 진단 화면으로 이동합니다.(사진등록을 먼저해야합니다.)",Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_1=new Intent(CustomerInfo.this, CustomerActivity.class);
                    intent_bottom_1.putExtra("img",img);
                    startActivity(intent_bottom_1);
                    break;
                }
                case R.id.bottom_menu_2:
                {
                    Toast.makeText(this,"스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_2=new Intent(CustomerInfo.this,styleSearchActivity.class);
                    //이거는 그냥 단순한 스타일 검색 기능이기 때문에 인텐트를 통해서 어떤 정보를 전달할 필요가 없다.
                    startActivity(intent_bottom_2);
                    break;

                }
                case R.id.bottom_menu_3:
                {
                    Toast.makeText(this,"이미 회원정보 페이지 입니다", Toast.LENGTH_SHORT).show();
                    //이미 회원정보를 보여주고 있으므로 그냥 메시지만 출력
                    break;
                }
            }
            return false;
        });
    }
    public File BmpToFile(Bitmap bmp, String filename)
    {
        File file=new File(getExternalFilesDir(null),filename);
        try{
            FileOutputStream fos=new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }
}
