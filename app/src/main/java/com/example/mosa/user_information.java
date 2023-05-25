package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class user_information extends AppCompatActivity {

    static int update_info=0;
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

    EditText name_edit;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference pro_storef=storage.getReference().child("user_profile/");
    FirebaseUser user_pro = FirebaseAuth.getInstance().getCurrentUser();
    String email_pro=user_pro.getEmail();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    Date date=new Date();
    String value;
    String user_img_name = email_pro+ "_" +"profile"+"_"+ format.format(date);

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

        name_edit=findViewById(R.id.name_editText);

        pro_file_name=new ArrayList<String>();


        /*
        유저의 이름을 가져온다.
        */

        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        myRef.child(user_pro.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value = snapshot.getValue(String.class);
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
        프로필 이미지 수정
        */
        btn_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_proimg = new Intent(Intent.ACTION_PICK);
                intent_proimg.setType("image/*");
                startActivityForResult(intent_proimg, 1);
                update_info=1;
            }
        });


        /*
        이름 수정
        */
        btn_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name_edit.getVisibility() == View.VISIBLE) {
                    name_edit.setVisibility(View.GONE);

                    if(!name_edit.getText().toString().isEmpty()){
                        current_name.setText(name_edit.getText());
                        update_info=1;
                    }
                    else{

                    }

                } else {
                    name_edit.setVisibility(View.VISIBLE);
                }
            }
        });

        /*
        가입일자, 최근 수정일자 보여줌
        처음에는 가입일자와 수정일자가 똑같다가, 나중에 수정할때 달라진다.
        */

        myRef.child(user_pro.getUid()).child("create_date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                join_date.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child(user_pro.getUid()).child("modify_date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                edit_date.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                /*
                수정한 이름을 저장
                */
                if(update_info==1) {
                    String update_name=current_name.getText().toString();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name",update_name);
                    myRef.child(user_pro.getUid()).updateChildren(updates);

                /*
                등록한 프로필 이미지를 저장
                */
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) profile_img.getDrawable();
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
                            Toast.makeText(user_information.this, "연결 상태가 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    /*
                    수정일자를 저장
                    */
                    Map<String, Object> updates_2 = new HashMap<>();
                    String update_day=format.format(date);
                    updates_2.put("modify_date",update_day);
                    myRef.child(user_pro.getUid()).updateChildren(updates_2);
                }
                update_info=0;
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
                        profile_img.setImageBitmap(resize);


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
        }
    }


}