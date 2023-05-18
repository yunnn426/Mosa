package com.example.mosa;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class loadingActivity extends AppCompatActivity {
    private static final int IMAGE_DIMENSION = 224;
    int result_1 = 0;
    String result_2;
    String result_3;
    TextView loading_text;
    ImageView loading_img;
    ImageView res_img;
    Bitmap user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_model);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        String image_path = intent.getStringExtra("img");
        File file = new File(image_path);

        Date date = new Date();//사진을 찍은 날짜를 저장해야,
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_img_name = user.getEmail() + "_" + format.format(date);
        File user_img_file = new File(image_path);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("user_image/");
        Uri uri_upload = Uri.fromFile(user_img_file);
        StorageReference storageRef_2 = storageRef.child(user_img_name);


        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        try {
            user_img = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ConstraintLayout cons = findViewById(R.id.loading_view);
        loading_text = findViewById(R.id.loading_text);
        loading_img = findViewById(R.id.loading_img);
        res_img = findViewById(R.id.res_clr);
        final Animation rotation = AnimationUtils.loadAnimation(loadingActivity.this, R.anim.model_loading_animation);

        rotation.setRepeatCount(Animation.INFINITE);
        loading_img.startAnimation(rotation);

        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


                String modelFileName="model.tflite";
                // 모델 로드
                AssetManager assetManager = getAssets();
                Interpreter.Options options = new Interpreter.Options();
                options.setNumThreads(4); // 원하는 스레드 수 설정
                try {
                    Interpreter interpreter = new Interpreter(loadModelFile(assetManager, modelFileName), options);
                    float[][][][] inputData = preprocessImage(image_path);
                    float[][] outputArray = new float[1][5];
                    interpreter.run(inputData, outputArray);

                    int maxIndex = 0;
                    float maxValue = outputArray[0][0];
                    for (int i = 0; i < outputArray[0].length; i++) {
                        if (outputArray[0][i] > maxValue) {
                            maxValue = outputArray[0][i];
                            maxIndex = i;
                        }
                    }

                    String[] labels = {"a", "b", "c", "d", "e"};
                    String predictedLabel = labels[maxIndex];
                    Toast.makeText(loadingActivity.this,predictedLabel,Toast.LENGTH_SHORT).show();

                    result_2 = predictedLabel;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                storageRef_2.putFile(uri_upload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                /*
                여기에 color_result 컬렉션에 파일 이미지 이름 값을 넣고 모델은 그 이름을 이용해서 이미지를 찾고
                그 이미지로 진단을 하고나서 진단의 결과값을 해당 문서에 넣고, 그 값을 안드로이드 스튜디오가 읽어서 사용자의 화면상에 보여준다.
                 */
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference coloref = db.collection(user.getEmail() + "_color_result");
                        coloref.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                // "사용자의 이메일"+_color_result 컬렉션에 데이터가 추가/변경/제거되었을 때 실행되는 코드
                                // 즉 파이썬의 머신러닝 모델에 의해서 데이터가 추가될시 즉시 사용한 이미지의 이름으로 그 결과값을 검색해서
                                // 결과값을 읽어온다.
                                Query query = coloref.whereEqualTo("file_name", user_img_name);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            result_1 = documentSnapshot.getLong("color_result").intValue();
                                            //result_2=documentSnapshot.getString("face_result");

                                            /*
                                            여기에서 직접 얼굴형 진단 모델을 호출해서 실행시키는 코드를 추가해야
                                            */
                                            /*
                                            FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("faceshape").build();
                                            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

                                            FirebaseModelManager.getInstance().download(remoteModel, conditions)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // 저장된 모델 불러오는 것 성공

                                                            FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();

                                                            try {

                                                                FirebaseModelInterpreter interpreter = FirebaseModelInterpreter.getInstance(options);

                                                                // 입력받은 이미지를 전처리하는 곳이다
                                                                float[][][][] inputData = preprocessImage(image_path);

                                                                // 입력과 출력(224*224의 컬러이미지, 1~5까지의 가능성)의 형식을 정하고, 직접 전처리된 데이터를 넣는다.
                                                                FirebaseModelInputOutputOptions inputOptions = new FirebaseModelInputOutputOptions.Builder()
                                                                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, IMAGE_DIMENSION, IMAGE_DIMENSION, 3})
                                                                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 5})
                                                                        .build();
                                                                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                                                                        .add(inputData)
                                                                        .build();

                                                                // 모델을 입력데이터를 가지고 실행시킨다.
                                                                interpreter.run(inputs, inputOptions)
                                                                        .addOnSuccessListener(new OnSuccessListener<FirebaseModelOutputs>() {
                                                                            @Override
                                                                            public void onSuccess(FirebaseModelOutputs result) {
                                                                                // 모델실행 성공

                                                                                // 결과값을 받는다
                                                                                float[][] outputData = result.getOutput(0);
                                                                                String[] labels = {"a", "b", "c", "d", "e"};

                                                                                int maxIndex = getMaxIndex(outputData[0]);
                                                                                String predictedLabel = labels[maxIndex];

                                                                                //가능성이 가장 높은 라벨을 얼굴형 진단의 결과값으로 저장한다.
                                                                                result_2=predictedLabel;
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // 모델 실행 실패
                                                                            }
                                                                        });
                                                            } catch (IOException |
                                                                     FirebaseMLException e) {
                                                                // 입력 이미지 오류

                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // 모델 다운로드 실패

                                                        }
                                                    });*/
                                            result_3 = documentSnapshot.getString("file_name");

                                            //무한 애니매이션 중지
                                            rotation.cancel();
                                            //저위의 3개의 값을 인텐트로 전달해야
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(loadingActivity.this, "실행상에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션이 종료되면 실행될 작업

                loading_img.clearAnimation();
                loading_text.setText("컬러를 찾았습니다!");

                loading_img.setVisibility(View.INVISIBLE);
                res_img.setVisibility(View.VISIBLE);

                //결과 값을 받아서 그거에 맞는 ui 요소를 표시
                String rst_color="result_"+result_1;
                int drawable_id=getResources().getIdentifier(rst_color,"drawable",getPackageName());
                res_img.setImageResource(drawable_id);

                //임시로 표시
                //res_img.setImageResource(R.drawable.result_1);

                String color;
                switch (result_1) {
                    case 1:
                        color = "#FFA6AF";
                        break;
                    case 2:
                        color = "#FF6E55";
                        break;
                    case 3:
                        color = "#D6EDE5";
                        break;
                    case 4:
                        color = "#F4FFB4";
                        break;
                    case 5:
                        color = "#E0C8EF";
                        break;
                    case 6:
                        color = "#273E19";
                        break;
                    case 7:
                        color = "#97AB60";
                        break;
                    case 8:
                        color = "#987A5F";
                        break;
                    case 9:
                        color = "#023854";
                        break;
                    case 10:
                        color = "#FF53AC";
                        break;
                    default:
                        color = "#ffffff";
                        break;
                }

                cons.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //이곳을 나중에 퍼스널 컬러별 색으로 바꾸면 될듯
                        cons.setBackgroundColor(Color.parseColor(color));
                    }
                }, 2000);

                cons.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == event.ACTION_DOWN) {
                            Intent intent_result = new Intent(loadingActivity.this, PersonalActivity.class);
                            intent_result.putExtra("img", image_path);

                            intent_result.putExtra("result_color", result_1);
                            intent_result.putExtra("result_face", result_2);

                            //intent_result.putExtra("result_color",6);
                            //intent_result.putExtra("result_face",3);
                            intent_result.putExtra("img_name", user_img_name);
                            startActivity(intent_result);
                            return true;
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu); //우상단 메뉴 버튼
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(loadingActivity.this, IntitialActivity.class);
            intent.putExtra("choice", "종합진단");
            startActivity(intent);// 뒤로가기 버튼을 눌렀을 때 , 바로 사진 선택 화면으로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private float[][][][] preprocessImage(String imagePath) throws IOException {
        // Load the image from the specified path.
        File imageFile = new File(imagePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        // Resize the image to the desired input dimension.
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_DIMENSION, IMAGE_DIMENSION, true);

        // Convert the image to a float array.
        float[][][][] inputData = new float[1][IMAGE_DIMENSION][IMAGE_DIMENSION][3];
        for (int i = 0; i < IMAGE_DIMENSION; i++) {
            for (int j = 0; j < IMAGE_DIMENSION; j++) {
                int pixel = resizedBitmap.getPixel(i, j);
                inputData[0][i][j][0] = (float) ((pixel >> 16) & 0xFF) / 255.0f;  // Red channel
                inputData[0][i][j][1] = (float) ((pixel >> 8) & 0xFF) / 255.0f;   // Green channel
                inputData[0][i][j][2] = (float) (pixel & 0xFF) / 255.0f;          // Blue channel
            }
        }

        return inputData;
    }
    private static int getMaxIndex(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelFileName) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelFileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
