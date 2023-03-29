package com.example.mosa;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IntitialActivity extends AppCompatActivity {


    Intent intent=getIntent();

    /*
    로그인 화면을 통해서 여기로 넘어오고, 여기서 회원 관련 정보를 받아야한다.
    Signup.java에서 인텐트를 통해서 로그인에 성공한 경우 여기로 넘어온다.
    그리고 회원 관련정보(닉네임, 이메일, 최초 가입기간 등)도 인텐트로 받아야한다.
    추가로 시간이 되면 자동 로그인 기능도 넣어도 될듯
    추후협의
    */

    private final static int scaling_Facter=10;

    //현재 이미지가 올라와 있는지 여부를 확인
    boolean inImg=false;

    Button btn1;
    ImageButton btn3;
    Button btn4;
    File file;
    String filePath;
    String faceinfo=null;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_activity);

        Intent intent_login = getIntent();
        FirebaseUser user = (FirebaseUser) intent_login.getSerializableExtra("user");

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        btn1=findViewById(R.id.img_button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                inImg=true;
            }
        });

        ActivityResultLauncher<Intent> requestCameraFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    if (bitmap != null) {
                        btn3.setImageBitmap(bitmap);
                    }
                });
        btn3=findViewById(R.id.cma);
        btn3.setOnClickListener(v -> {
            //camera app......................
            //파일 준비...............
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile(
                        "JPEG_" + timeStamp + "_",
                        ".jpg",
                        storageDir
                );
                filePath = file.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.mosa.fileprovider",
                        file
                );
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                requestCameraFileLauncher.launch(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
            Bitmap bitmap=bitmapDrawable.getBitmap();
            inImg=true;
        });

        //이거는 테스트용 초기화면으로 넘어가기 위한 인텐트 입니다.(비트맵 형식의 이미지를 보냄)
        btn4=findViewById(R.id.test_screen);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IntitialActivity.this, CustomerActivity.class);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                File file=BmpToFile(bitmap,"image.png");
                intent.putExtra("img",file.getAbsolutePath());
                startActivity(intent);
            }
        });



        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            switch (item.getItemId()){

                case R.id.bottom_menu_1:
                {
                    Toast.makeText(this,"고객님의 이미지로 스타일 진단 화면으로 이동합니다.(사진등록을 먼저해야합니다.)",Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_1=new Intent(IntitialActivity.this, CustomerActivity.class);
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                    Bitmap bitmap=bitmapDrawable.getBitmap();
                    File file=BmpToFile(bitmap,"image.png");
                    intent_bottom_1.putExtra("img",file.getAbsolutePath());
                    startActivity(intent_bottom_1);
                    break;
                }
                case R.id.bottom_menu_2:
                {
                    Toast.makeText(this,"스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_2=new Intent(IntitialActivity.this,styleSearchActivity.class);
                    //이거는 그냥 단순한 스타일 검색 기능이기 때문에 인텐트를 통해서 어떤 정보를 전달할 필요가 없다.
                    startActivity(intent_bottom_2);
                    break;

                }
                case R.id.bottom_menu_3:
                {
                    Toast.makeText(this,"당신의 회원정보를 보여줍니다.", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_3=new Intent(IntitialActivity.this,CustomerInfo.class);
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                    Bitmap bitmap=bitmapDrawable.getBitmap();
                    File file=BmpToFile(bitmap,"image.png");
                    intent_bottom_3.putExtra("img",file.getAbsolutePath());
                    startActivity(intent_bottom_3);
                    break;
                }
            }
            return false;
        });
    }

    //비트맵 이미지를 파일 형태로 변환
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    btn3.setImageURI(uri);
                }
                break;
        }

        if(requestCode == 101  && resultCode == Activity.RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            btn3.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


}
