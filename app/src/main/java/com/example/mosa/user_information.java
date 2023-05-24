package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class user_information extends AppCompatActivity {

    ArrayList<String> pro_file_name;
    File recent_pro_img;
    String version;

    ImageView profile_img;

    Button btn_prof;
    Button btn_name;
    Button btn_setting;

    TextView my_page_name;
    TextView current_name;
    TextView join_date_name;
    TextView join_date;
    TextView edit_date_name;
    TextView edit_date;
    TextView version_info;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference pro_storef=storage.getReference().child("user_profile/");
    FirebaseUser user_pro = FirebaseAuth.getInstance().getCurrentUser();
    String email_pro=user_pro.getEmail();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_inform);
        getSupportActionBar().hide();

        my_page_name=findViewById(R.id.user_name_1);

        profile_img=findViewById(R.id.user_profile);
        btn_prof=findViewById(R.id.user_profile_edit);

        current_name=findViewById(R.id.user_name_2);
        btn_name=findViewById(R.id.user_name_edit);

        join_date_name=findViewById(R.id.user_name_3);
        join_date=findViewById(R.id.user_join);

        edit_date_name=findViewById(R.id.user_name_4);
        edit_date=findViewById(R.id.user_edit_date);

        version_info=findViewById(R.id.app_version);

        btn_setting=findViewById(R.id.edit_set_btn);

        pro_file_name=new ArrayList<String>();


        /*
        유저의 이름을 가져온다.
        */

        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        myRef.child(user_pro.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                my_page_name.setText(value);
                current_name.setText(value);
                join_date_name.setText(value);
                edit_date_name.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    /*
    유저의 프로파일 이미지를 가져와서 세팅
    */
        pro_storef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for(StorageReference item : listResult.getItems()){
                    String profile_name=item.getName();

                    SpannableString spannableString = new SpannableString(profile_name);

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
                        File result_path=user_information.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/user_profile_img");
                        try {
                            recent_pro_img=File.createTempFile(recent_pro_filename,"jpg",result_path);
                            StorageReference recent_pro_storef=storage.getReference().child("user_profile/"+recent_pro_filename);
                            recent_pro_storef.getFile(recent_pro_img).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap pro_bitmap= BitmapFactory.decodeFile(recent_pro_img.getAbsolutePath());
                                    profile_img.setImageBitmap(pro_bitmap);
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
            }
        });

        /*
        앱의 버전 정보를 보여준다.
        */
        version=BuildConfig.VERSION_NAME;
        version_info.setText(version);

        /*
        사용자의 변경사항을 앱에 적용한다.
        */
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        /*
        하단 매뉴
        */

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.bottom_menu_1:
                {
                    Intent intent_bottom_1=new Intent(user_information.this, CustomerActivity.class);
                    startActivity(intent_bottom_1);
                    overridePendingTransition(0,0);
                    break;
                }
                case R.id.bottom_menu_2:
                {
                    break;
                }
                case R.id.bottom_menu_3:
                {
                    Intent intent_bottom_3=new Intent(user_information.this,CustomerInfo.class);
                    startActivity(intent_bottom_3);
                    overridePendingTransition(0,0);
                    break;
                }
            }
            return false;
        });

    }
}