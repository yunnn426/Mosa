package com.example.mosa;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IntitialActivity extends AppCompatActivity {


    Intent intent=getIntent();

    /*
    이 엑티비티는 진단 선택화면에서 넘어온다. 주로 사진을 등록하고나서, 한번더 등록버튼을 누르면
    진단 실행화면으로 넘어간다.
    */

    private final static int scaling_Facter=10;

    //현재 이미지가 올라와 있는지 여부를 확인
    boolean inImg=false;

    ImageButton backBtn;
    Button btn1;
    ImageButton btn3;
    Button check_btn;
    File file;
    String filePath;
    MenuItem bottom_1;
    MenuItem bottom_2;
    MenuItem bottom_3;
    String faceinfo=null;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_activity);
        getSupportActionBar().hide();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        Intent intent_info=getIntent();
        String choice=intent_info.getStringExtra("choice");

        //뒤로가기 버튼
        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntitialActivity.this, CustomerActivity.class);
                startActivity(intent);
            }
        });


        btn1=findViewById(R.id.img_button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                inImg = true;
            }
        });
        ActivityResultLauncher<Intent> requestCameraFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    if (bitmap != null) {
                        Bitmap resize_bitmap = resize_imageSize(this, bitmap, 800, 800, "image.png");
                        btn3.setImageBitmap(resize_bitmap);
                        inImg = true;
                    }
                });
        btn3=findViewById(R.id.cma);
        btn3.setOnClickListener(v -> {
            //camera app......................
            //파일 준비...............
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Intent intent_choice_2 = null;
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


        });
        check_btn = findViewById(R.id.check_btn);

        check_btn.setOnClickListener(view -> {
            Intent intent_choice_2 = null;
            BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
            Bitmap bitmap=bitmapDrawable.getBitmap();
            if(inImg != false) {
                if (choice.equals("종합진단")) {
                    //이제 여기에 진단 실행화면으로 넘어가는 인텐트를 넣으면 된다.
                    intent_choice_2 = new Intent(IntitialActivity.this, loadingActivity.class);
                    Bitmap bitmap_color = bitmap;
                    File file = BmpToFile(bitmap_color, "image.png");
                    intent_choice_2.putExtra("img", file.getAbsolutePath());
                    //intent_choice_2.putExtra("result_color",9);
                } else {
                    Toast.makeText(IntitialActivity.this, "알수없는 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent_choice_2);
            }else{
                Toast.makeText(IntitialActivity.this, "사진을 넣어주세요.", Toast.LENGTH_SHORT).show();
            }
        });



        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            switch (item.getItemId()){

                case R.id.bottom_menu_1:
                {
                    //Toast.makeText(this,"고객님의 이미지로 스타일 진단 화면으로 이동합니다.(다시 진단 종류를 선택하세요.)",Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_1=new Intent(IntitialActivity.this, CustomerActivity.class);
                    //이제 따로 뭘 전달할 필요는 없다, 그냥 진단 종류 선택하는 화면으로 만 이동
                    startActivity(intent_bottom_1);
                    break;
                }
                case R.id.bottom_menu_2:
                {
                    //Toast.makeText(this,"스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_2=new Intent(IntitialActivity.this,styleSearchActivity.class);
                    //이거는 그냥 단순한 스타일 검색 기능이기 때문에 인텐트를 통해서 어떤 정보를 전달할 필요가 없다.
                    startActivity(intent_bottom_2);
                    break;

                }
                case R.id.bottom_menu_3:
                {
                    String user_email=user.getEmail();
                    //Toast.makeText(this,"당신의 회원정보를 보여줍니다.", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_3=new Intent(IntitialActivity.this,CustomerInfo.class);
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                    Bitmap bitmap=bitmapDrawable.getBitmap();
                    File file=BmpToFile(bitmap,"image.png");
                    intent_bottom_3.putExtra("useremail",user_email);
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
                    Uri resize_uri = resize_imageSizeUri(this, uri, 800, 800,"image.png");
                    btn3.setImageURI(resize_uri);
                }
                break;
        }

        if(requestCode == 101  && resultCode == Activity.RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            Bitmap resize_bitmap = resize_imageSize(this, bitmap, 800, 800,"image.png");
            btn3.setImageBitmap(resize_bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public Bitmap resize_imageSize(Context context, Bitmap bitmap, int width, int height, String filename){
        Bitmap resize = null;
        try {

            //Bitmap orgImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);//비트맵 파일 겟
            resize = Bitmap.createScaledBitmap(bitmap,width, height, true);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return resize;
    }

    public Uri resize_imageSizeUri(Context context, Uri uri, int width, int height, String filename){

        try {
            Bitmap orgImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);//비트맵 파일 겟
            Bitmap resize = Bitmap.createScaledBitmap(orgImage,width, height, true);

            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = new File(storageDir,filename);

            if(image.exists()) {//만약 이미 이 파일이 존재한다면(1회 이상 했다면)
                image.delete();//중복되므로 과거 파일은 삭제
                image = new File(storageDir, filename);//그리고 다시 오픈
            }

            FileOutputStream outputStream = new FileOutputStream(image);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//sdk 24 이상, 누가(7.0)
                uri = FileProvider.getUriForFile(context,// 7.0에서 바뀐 부분은 여기다.
                        BuildConfig.APPLICATION_ID + ".provider", image);
            } else {//sdk 23 이하, 7.0 미만
                uri = Uri.fromFile(image);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return uri;
    }
}
