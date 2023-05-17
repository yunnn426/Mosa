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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.modeldownload.*;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import org.tensorflow.lite.Interpreter;


import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;



import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    String faceinfo=null;
    TextView customer_name;
    FirebaseDatabase firebaseDatabase;

    private final static int scaling_Facter=10;
    FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();

    FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
    ImageView img1;

    Button btn1;
    Button btn2;
    MenuItem bottom_1;
    MenuItem bottom_2;
    MenuItem bottom_3;


    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommended_initial_screen);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img1=findViewById(R.id.example_skin_img);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent=getIntent();

        customer_name = findViewById(R.id.title_text);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");

        myRef.child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                String value2 = value + "님,";
                customer_name.setText(value2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //String imagePath = intent.getStringExtra("img");
        //File file=new File(imagePath);
        //Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);
        /*
        try{
            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            //faceinfo=analyzePicture(bitmap);
            //여기서 ml kit을 수행해서 얼굴 이미지를 탐지해서 이용하려는데 null이 할당? 왜지?
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        */

        //여기서 선택에 따라서 하단 메뉴의 선택여부(색깔)을 다르게 해야
        //그냥 엑티비티를 이용해도 될듯?
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.bottom_menu_1:
                {
                    Toast.makeText(this,"이미 스타일 진단 화면입니다.(단, 사진등록을 먼저 해야합니다.)",Toast.LENGTH_SHORT).show();
                    //이미 스타일 진단 화면이어서 별다른 응답 없어도 된다.
                    break;
                }
                case R.id.bottom_menu_2:
                {
                    Toast.makeText(this,"스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_2=new Intent(CustomerActivity.this,styleSearchActivity.class);
                    //이거는 그냥 단순한 스타일 검색 기능이기 때문에 인텐트를 통해서 어떤 정보를 전달할 필요가 없다.
                    startActivity(intent_bottom_2);
                    break;
                    //스타일 검색 화면을 보여준다.
                }
                case R.id.bottom_menu_3:
                {
                    String user_name=user.getDisplayName();
                    String user_email=user.getEmail();
                    Toast.makeText(this,"당신의 회원정보를 보여줍니다.", Toast.LENGTH_SHORT).show();
                    Intent intent_bottom_3=new Intent(CustomerActivity.this,CustomerInfo.class);
                    intent_bottom_3.putExtra("username",user_name);
                    intent_bottom_3.putExtra("useremail",user_email);
                    startActivity(intent_bottom_3);
                    break;
                    //회원님의 정보를 보여준다
                }
            }
            return false;
        });
        btn1=findViewById(R.id.color_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(CustomerActivity.this,IntitialActivity.class);
                //if 파이어 베이스에서 String 형으로 보내줄 경우
                //String face_color_info_1=sendimg_skin_firebase(bitmap,faceinfo)
                //if 파이어 베이스에서 Json 형으로 보내줄 경우
                //JSONObject face_color_info_1=(JSONObject)sendimg_skin_firebase(bitmap,faceinfo)
                //String info_rem=sendimg_recom_firebase(bitmap,faceinfo)
                //위의 퍼스널 컬러 정보와 추천정보를 담아서 해당 엑티비티에 전달(내 생각인데 큰 파일형식(?)이 좋을듯)
                //일단 모르기 때문에 String 형으로 아무거나 전달
                String info_rem="테스트용 추천정보";
                String face_color_info_1="테스트용 제목";
                String face_color_info_2="테스트용 내용";//여기에는 제목(퍼스널 컬러)에 알맞는 정보(내용)를 보내줘야
                intent_1.putExtra("title",face_color_info_1);
                intent_1.putExtra("detail",face_color_info_2);
                intent_1.putExtra("recommend",info_rem);
                intent_1.putExtra("choice","종합진단");
                startActivity(intent_1);
            }
        });

/*
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("test").build();
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        FirebaseModelManager.getInstance().download(remoteModel,conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("test","test 성공");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("test","test 실패");
            }
        });
*/

/*
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("test").build();
        FirebaseModelInterpreter firebaseInterpreter = null;
        try {
            firebaseInterpreter = FirebaseModelInterpreter.getInstance(new FirebaseModelInterpreterOptions.Builder(remoteModel).build());
        } catch (FirebaseMLException e) {
            throw new RuntimeException(e);
        }

// Define input shape
        FirebaseModelInputOutputOptions inputOutputOptions = null;
        try {
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 784})
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 10})
                    .build();
        } catch (FirebaseMLException e) {
            throw new RuntimeException(e);
        }

// Prepare input data
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 784);
        byteBuffer.order(ByteOrder.nativeOrder());
        float[] input = new float[784];
// Fill input with your data
        byteBuffer.asFloatBuffer().put(input);

        FirebaseModelInputs inputs = null;
        try {
            inputs = new FirebaseModelInputs.Builder()
                    .add(byteBuffer)
                    .build();
        } catch (FirebaseMLException e) {
            throw new RuntimeException(e);
        }

// Run inference
        firebaseInterpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(new OnSuccessListener<FirebaseModelOutputs>() {
                    @Override
                    public void onSuccess(FirebaseModelOutputs result) {
                        // Process the inference result
                        float[][] output = result.getOutput(0);
                        // Use the output to make decisions
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
*/

    }

    public void sendimg_skin_firebase(Bitmap bmp, String faceinfo){
        //여기는 차후 구현해야(파이어베이스에 해당 이미지를 보내서 추천하는 퍼스널 컬러와 관련된 정보를 얻는다.)
        //여기의 반환형은 달라진다(파이어베이스에서 보내주는 정보의 형태(json,String,...)에 따라서 달라짐)
        Log.d("User","피부색을 분석해서 당신의 컬러를 알려드리겠습니다.");
    }
    public void sendimg_shap_firebase(Bitmap bmp, String faceinfo){
        //여기는 차후 구현해야(파이어베이스에 해당 이미지를 보내서 얼굴형과 관련된 정보를 얻는다.)
        //여기의 반환형은 달라진다(파이어베이스에서 보내주는 정보의 형태(json,String,...)에 따라서 달라짐)
        Log.d("User","얼굴의 좌표값을 분석해서 당신의 얼굴형을 알려드리겠습니다.");
    }
    public void sendimg_recom_firebase(Bitmap bmp, String faceinfo){
        String info_shap; //sendimg_shap_firebase(고객의 얼굴형 정보를 이용해야)
        String info_color; //sendimg_skin_firebase(고객의 추천된 퍼스널컬러 정보를 이용해야)
        //여기는 차후 구현해야(파이어베이스에 해당 이미지를 보내서 관련 추천정보를 얻어온다.)
        //여기의 반환형은 달라진다(파이어베이스에서 보내주는 정보의 형태(json,String,...)에 따라서 달라짐)
        Log.d("User","당신의 컬러를 바탕으로 옷,화장품,헤어,악세사리를 추천합니다.");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu); //우상단 메뉴 활성화
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

    //mlkit의 face_detection의 기능을 실행하는 메소드이다.
    private String analyzePicture(Bitmap bitmap){
        Toast.makeText(this,"최소 100X100에서 최대 1280X1280정도 크기의 이미지를 올려주세요",Toast.LENGTH_SHORT).show();
        //이미지 크기를 자동으로 조정
        Bitmap smbitmap=bitmap;
        if(smbitmap.getHeight()>1280||smbitmap.getWidth()>1280){
            smbitmap=Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.getWidth()/scaling_Facter,
                    bitmap.getHeight()/scaling_Facter,
                    false
            );
        }
        else if(smbitmap.getHeight()<100||smbitmap.getWidth()<100){
            smbitmap=Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.getWidth()*scaling_Facter,
                    bitmap.getHeight()*scaling_Facter,
                    false
            );
        }
        else{
            smbitmap=Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.getWidth()/1,
                    bitmap.getHeight()/1,
                    false
            );
        }

        InputImage inputImage=InputImage.fromBitmap(smbitmap,0);

        detector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {


                        faceinfo=String.valueOf(faces);

                        // 여기에 모든 색과 관련된 정보 추출후 서버로 보낸다.
                        detectface(bitmap,faces,String.valueOf(faces));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("yoon",e.toString());
                        Log.d("yoon","알 수 없는 원인으로 에러가 발생!!! 메일을 통해 문의");
                    }
                });
        return faceinfo;

    }
    //서버로 정보를 보내고 서버에서는 그 정보를 받아서 해석하고 나서, 해당 정보를 고객 한테 보내준다.
    private void detectface(Bitmap bitmap,List<Face> faces, String info){
        Log.d("yoon","해당 사진과, 감지된 정보를 서버로 보냅니다.");

    }

}