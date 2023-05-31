package com.example.mosa;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.BuildConfig;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class user_information extends AppCompatActivity {

    static int update_info=0;
    ArrayList<String> pro_file_name;
    File recent_pro_img;
    String version;
    CircleImageView profile_img;

    ImageButton btn_change_profile; //프로필 사진 변경 버튼
    ImageButton btn_setting;

    TextView join_date_name;
    TextView join_date;
    TextView edit_date_name;
    TextView edit_date;
    TextView version_info;
    EditText user_mail;
    TextView logout;
    EditText name_edit;
    String before_name;
    ExifInterface exif = null;
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

        user_mail=findViewById(R.id.user_email);

        profile_img=findViewById(R.id.user_profile);
        btn_change_profile = findViewById(R.id.change_profile);

        join_date_name=findViewById(R.id.user_name_3);
        join_date=findViewById(R.id.user_join);

        edit_date_name=findViewById(R.id.user_name_4);
        edit_date=findViewById(R.id.user_edit_date);

        version_info=findViewById(R.id.app_version);

        btn_setting=findViewById(R.id.edit_set_btn);

        name_edit=findViewById(R.id.name_editText);

        pro_file_name=new ArrayList<String>();

        logout=findViewById(R.id.logout_text);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(user_information.this,Signup.class);
                startActivity(intent);
            }
        });

        /*
        유저의 이름을 가져온다.
        */

        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        myRef.child(user_pro.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value = snapshot.getValue(String.class);
                join_date_name.setText(value);
                edit_date_name.setText(value);
                name_edit.setText(value);
                user_mail.setText(user_pro.getEmail());
                before_name=value;
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
                                    //프로필 이미지 리사이징
                                    int width=pro_bitmap.getWidth();
                                    int height=pro_bitmap.getHeight();

                                    float ratio = (float) width / height;

                                    int resizedWidth, resizedHeight;
                                    if (ratio > 1) {
                                        // 이미지의 가로가 더 긴 경우
                                        resizedWidth = 500;
                                        resizedHeight = (int) (resizedWidth / ratio);
                                    } else {
                                        // 이미지의 세로가 더 긴 경우
                                        resizedHeight = 500;
                                        resizedWidth = (int) (resizedHeight * ratio);
                                    }

                                    Bitmap resize=Bitmap.createScaledBitmap(pro_bitmap, resizedWidth, resizedHeight, true);

                                    profile_img.setImageBitmap(resize);
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
        btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_proimg = new Intent(Intent.ACTION_PICK);
                intent_proimg.setType("image/*");
                startActivityForResult(intent_proimg, 1);
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
        version= BuildConfig.VERSION_NAME;
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
                //이미지와 이름에 대한 조건 검사
                if(update_info==1 || (!name_edit.getText().toString().isEmpty() && !before_name.equals(name_edit.getText().toString())) ) {
                    String update_name=name_edit.getText().toString();
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
                else{
                    Toast.makeText(user_information.this,"새로운 이름이나 프로필 이미지를 넣어주세요.",Toast.LENGTH_SHORT).show();
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
                        Uri photoUri = Uri.parse(getRealPathFromURI(uri));
                        exif = new ExifInterface(photoUri.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        int width=pro_bitmap.getWidth();
                        int height=pro_bitmap.getHeight();

                        float ratio = (float) width / height;

                        int resizedWidth, resizedHeight;
                        if (ratio > 1) {
                            // 이미지의 가로가 더 긴 경우
                            resizedWidth = (int) (500*1.3);
                            resizedHeight = 500;
                        } else if(ratio < 1){
                            // 이미지의 세로가 더 긴 경우
                            resizedHeight = (int) (500*1.3);
                            resizedWidth = 500;
                        } else {
                            resizedHeight = 500;
                            resizedWidth = 500;
                        }

                        Bitmap resize=Bitmap.createScaledBitmap(pro_bitmap, resizedWidth, resizedHeight, true);
                        Bitmap rotate = rotateBitmap(resize, orientation);
                        profile_img.setImageBitmap(rotate);
                        update_info=1;


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
        }
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getRealPathFromURI(Uri contentUri){
        String result;

        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if(cursor==null) {
            result = contentUri.getPath();
        } else{
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}