package com.example.mosa.customer_history;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.CustomerInfo;
import com.example.mosa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_history extends Fragment {

    TextView msg;
    Recycler_history adapter;
    ArrayList<String> Mail;
    ArrayList<String> Diagnosis;
    ArrayList<String> Diagnosis_result_1;
    ArrayList<String> Diagnosis_result_2;
    ArrayList<String> Diagnosis_file_name;
    ArrayList<String> Dia_date;
    ArrayList<Bitmap> img_list;
    String name;
    RecyclerView history;
    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;
    File path;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){
        View view=inflater.inflate(R.layout.customer_history,container,false);

        msg=view.findViewById(R.id.history_fail_msg);
        history=view.findViewById(R.id.history_view);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        Mail=new ArrayList<String>();
        Diagnosis=new ArrayList<String>();
        Diagnosis_result_1=new ArrayList<String>();
        Diagnosis_result_2=new ArrayList<String>();
        Diagnosis_file_name=new ArrayList<String>();
        img_list=new ArrayList<Bitmap>();
        Dia_date=new ArrayList<String>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference diagnosis_record=db.collection("user_record");

        //'test.email.com'은 테스트용입니다.
        Query query=diagnosis_record.whereEqualTo("user_email",user.getEmail());
        //Query query=diagnosis_record.whereEqualTo("user_email","test.email.com");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryresult) {
                // 결과 처리
                for (DocumentSnapshot documentSnapshot : queryresult) {
                    // 각각의 문서에서 필요한 데이터 가져오기
                    String diagnosisType = documentSnapshot.getString("diagnosis");
                    String diagnosisResult_color = documentSnapshot.getString("diagnosis_result_color");
                    String diagnosisResult_face=documentSnapshot.getString("diagnosis_result_face");
                    String diagnosisDate = documentSnapshot.getString("diagnosis_date");
                    String diagnosisimgname=documentSnapshot.getString("img_file_name");

                    Mail.add(user.getEmail());
                    Diagnosis.add(diagnosisType);
                    Diagnosis_result_1.add(diagnosisResult_color);
                    Diagnosis_result_2.add(diagnosisResult_face);
                    Diagnosis_file_name.add(diagnosisimgname);
                    Dia_date.add(diagnosisDate);
                    // 가져온 데이터를 arraylist 형식으로 저장하고 리사이클러 뷰를 통해 사용자에게 보여준다.
                }

                Log.d("test","작동성공");


                firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference myRef = firebaseDatabase.getReference("Users");
                myRef.child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        name=value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                storage=FirebaseStorage.getInstance();
                StorageReference user_img=storage.getReference().child("user_img/"+name+"_"+user.getUid());
                File result_path= getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                user_img.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        path=new File(result_path.getAbsolutePath());
                        for(StorageReference item: listResult.getItems()){
                            File download=new File(path,item.getName());
                            if(!path.exists()){
                                path.getParentFile().mkdirs();
                            }
                            try {
                                download.createNewFile();
                                FileDownloadTask fileDownloadTask=item.getFile(download);

                                fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap bitmap= BitmapFactory.decodeFile(download.getAbsolutePath());
                                        img_list.add(bitmap);
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                Context context = getContext();
                msg.setVisibility(View.INVISIBLE);
                /*
                여기 사이에는 파일 이름과 이미지 파일의 이름을 매칭시키는 코드가 들어가야한다.
                */
                adapter = new Recycler_history(Mail, Diagnosis, Diagnosis_result_1, Diagnosis_result_2, Dia_date, img_list);
                history.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                history.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // 실패 처리
                Log.d("fail","실패");
            }
        });

    }
}
