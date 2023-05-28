package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mosa.customer_history.Fragment_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CustomerInfo extends AppCompatActivity {

    ArrayList<String> pro_file_name;
    Fragment_Adapter adapter;
    ViewPager viewPager;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference pro_storef=storage.getReference().child("user_profile/");
    FirebaseUser user_pro = FirebaseAuth.getInstance().getCurrentUser();
    String email_pro=user_pro.getEmail();
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    String user_img_name = email_pro+ "_" +"profile"+"_"+ format.format(date);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_info);
        getSupportActionBar().hide();


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("진단기록"));

        pro_file_name=new ArrayList<String>();
        adapter = new Fragment_Adapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        /*
        여기에는 사용자의 이메일에 해당하는 모든 프로파일 이미지를 싹다 가져오고 나서 정렬하고 가장 최근의 프로파일을 가져와서 등록하는 코드를 넣는다.
        만약 null일시 에러 말고 기본이미지를 띄우도록한다.
        */



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        Intent intent = getIntent();
        //이 화면에서 스타일 진단 화면으로 가려면 얼굴 이미지 경로, ml_kit 얼굴 분석정보가 필요하다.
        String img = intent.getStringExtra("img");
        String name = intent.getStringExtra("username");




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_menu_3);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottom_menu_1: {
                        //여기서 스타일 진단 화면으로 넘어가려면 사진 정보를 전달할 필요가 있다.
                        //Toast.makeText(this, "고객님의 이미지로 스타일 진단 화면으로 이동합니다.(사진등록을 먼저해야합니다.)", Toast.LENGTH_SHORT).show();
                        Intent intent_bottom_1 = new Intent(CustomerInfo.this, CustomerActivity.class);
                        intent_bottom_1.putExtra("img", img);

                        File directory_1=CustomerInfo.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                        File directory_2=CustomerInfo.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/user_record_img");
                        File directory_3=CustomerInfo.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/user_profile_img");
                        if (directory_1.exists() && directory_1.isDirectory()) {
                            File[] files = directory_1.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    file.delete();
                                }
                            }
                        }
                        if (directory_2.exists() && directory_2.isDirectory()) {
                            File[] files = directory_2.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    file.delete();
                                }
                            }
                        }
                        if (directory_3.exists() && directory_3.isDirectory()) {
                            File[] files = directory_3.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    file.delete();
                                }
                            }
                        }
                        startActivity(intent_bottom_1);
                        overridePendingTransition(0,0);
                        return true;
                    }
                    case R.id.bottom_menu_2: {
                        //Toast.makeText(this, "스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                        Intent intent_bottom_2 = new Intent(CustomerInfo.this, user_information.class);
                        //이거는 그냥 단순한 스타일 검색 기능이기 때문에 인텐트를 통해서 어떤 정보를 전달할 필요가 없다.
                        startActivity(intent_bottom_2);
                        overridePendingTransition(0,0);
                        return true;

                    }
                    case R.id.bottom_menu_3: {
                        //Toast.makeText(this, "이미 회원정보 페이지 입니다", Toast.LENGTH_SHORT).show();
                        //이미 회원정보를 보여주고 있으므로 그냥 메시지만 출력
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu); //우상단 메뉴 활성화
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

    public File BmpToFile(Bitmap bmp, String filename) {
        File file = new File(getExternalFilesDir(null), filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
