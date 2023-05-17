package com.example.mosa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CustomerInfo extends AppCompatActivity {

    ArrayList<String> pro_file_name;
    TextView customer_name;
    TextView customer_email;
    ImageButton user_proimg;
    FirebaseDatabase firebaseDatabase;
    Fragment_Adapter adapter;
    ViewPager viewPager;
    Button logout;
    File recent_profile;

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

        logout = findViewById(R.id.logout);
        user_proimg = findViewById(R.id.customer_picture);
        customer_name = findViewById(R.id.customer_name);
        customer_email = findViewById(R.id.customer_email);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("진단기록"));

        pro_file_name=new ArrayList<String>();
        adapter = new Fragment_Adapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));





        pro_storef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for(StorageReference item : listResult.getItems()){
                    String profile_name=item.getName();
                    if(profile_name.contains(email_pro)){
                        pro_file_name.add(item.getName());
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()){
                Collections.sort(pro_file_name, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

                if(!pro_file_name.isEmpty()){
                String recent_pro_filename=pro_file_name.get(pro_file_name.size()-1);
                File result_path= CustomerInfo.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/user_profile_img");
                try {
                    recent_profile=File.createTempFile(recent_pro_filename,"jpg",result_path);
                    StorageReference recent_pro_storef=storage.getReference().child("user_profile/"+recent_pro_filename);
                    recent_pro_storef.getFile(recent_profile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap pro_pitmap=BitmapFactory.decodeFile(recent_profile.getAbsolutePath());
                            user_proimg.setImageBitmap(pro_pitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } catch (IOException e) {
                    Toast.makeText(CustomerInfo.this,"해당하는 사진이 존재하지 않습니다",Toast.LENGTH_SHORT).show();
                }
            }
                }
            }
        });



        /*
        여기에는 사용자의 이메일에 해당하는 모든 프로파일 이미지를 싹다 가져오고 나서 정렬하고 가장 최근의 프로파일을 가져와서 등록하는 코드를 넣는다.
        만약 null일시 에러 말고 기본이미지를 띄우도록한다.
        */



        user_proimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_proimg = new Intent(Intent.ACTION_PICK);
                intent_proimg.setType("image/*");
                startActivityForResult(intent_proimg, 1);


                /*
                여기에 유저의 프로필 이미지를 파이어베이스 스토어에 저장하는 코드를 추가
                */


                }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout_intent = new Intent(CustomerInfo.this, Signup.class);
                startActivity(logout_intent);
            }
        });

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


        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef.child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                customer_name.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = getIntent();
        //이 화면에서 스타일 진단 화면으로 가려면 얼굴 이미지 경로, ml_kit 얼굴 분석정보가 필요하다.
        String img = intent.getStringExtra("img");
        String name = intent.getStringExtra("username");
        String email = intent.getStringExtra("useremail");


        customer_email.setText(email);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.bottom_menu_1: {
                    //여기서 스타일 진단 화면으로 넘어가려면 사진 정보를 전달할 필요가 있다.
                    Toast.makeText(this, "고객님의 이미지로 스타일 진단 화면으로 이동합니다.(사진등록을 먼저해야합니다.)", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_1 = new Intent(CustomerInfo.this, CustomerActivity.class);
                    intent_bottom_1.putExtra("img", img);

                    startActivity(intent_bottom_1);
                    break;
                }
                case R.id.bottom_menu_2: {
                    Toast.makeText(this, "스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_2 = new Intent(CustomerInfo.this, styleSearchActivity.class);
                    //이거는 그냥 단순한 스타일 검색 기능이기 때문에 인텐트를 통해서 어떤 정보를 전달할 필요가 없다.
                    startActivity(intent_bottom_2);
                    break;

                }
                case R.id.bottom_menu_3: {
                    Toast.makeText(this, "이미 회원정보 페이지 입니다", Toast.LENGTH_SHORT).show();
                    //이미 회원정보를 보여주고 있으므로 그냥 메시지만 출력
                    break;
                }
            }
            return false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && data!=null) {
                    Uri uri = data.getData();
                    try {
                        Bitmap pro_bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        int width=pro_bitmap.getWidth();
                        int height=pro_bitmap.getHeight();

                        float ratio = (float) width / height;

                        int resizedWidth, resizedHeight;
                        if (ratio > 1) {
                            // 이미지의 가로가 더 긴 경우
                            resizedWidth = 250;
                            resizedHeight = (int) (resizedWidth / ratio);
                        } else {
                            // 이미지의 세로가 더 긴 경우
                            resizedHeight = 250;
                            resizedWidth = (int) (resizedHeight * ratio);
                        }

                        Bitmap resize=Bitmap.createScaledBitmap(pro_bitmap, resizedWidth, resizedHeight, true);
                        user_proimg.setImageBitmap(resize);

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) user_proimg.getDrawable();
                        Bitmap user_img = bitmapDrawable.getBitmap();

                        File user_profile_img = BmpToFile(user_img, user_img_name);
                        Uri uri_pro = Uri.fromFile(user_profile_img);
                        StorageReference pro_storef_2 = pro_storef.child(user_img_name);

                        pro_storef_2.putFile(uri_pro).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CustomerInfo.this, "연결 상태가 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
        }
    }
}
