package com.example.mosa.customer_history;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Fragment_history extends Fragment {

    TextView msg;
    ArrayList<File> tmpfile;
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

        tmpfile=new ArrayList<File>();
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
                    // 파이어베이스에 저장된 이미지 이름을 가지고 이제 스토리지에 있는 이미지를 불러와야한다.
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
                StorageReference user_img=storage.getReference();


                File result_path= getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/user_record_img");
                if(Diagnosis_file_name.isEmpty()){
                    msg.setText("진단기록이 없습니다.");
                }
                for (String filename : Diagnosis_file_name) {
                    final File localFile;
                    try {
                        localFile = File.createTempFile(filename,"jpg",result_path);
                        StorageReference imgref = user_img.child("user_img/" + filename);

                        imgref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                tmpfile.add(localFile);
                                if (tmpfile.size() == Diagnosis_file_name.size()) {
                                    Collections.sort(tmpfile, new Comparator<File>() {
                                        @Override
                                        public int compare(File file1, File file2) {
                                            return file1.getName().compareTo(file2.getName());
                                        }

                                    });
                                    for(File file : tmpfile){
                                        Bitmap bitmap_user = BitmapFactory.decodeFile(file.getAbsolutePath());
                                        img_list.add(bitmap_user);
                                        if(tmpfile.size()==Diagnosis_file_name.size()){
                                            Context context = getContext();
                                            msg.setVisibility(View.INVISIBLE);

                                            adapter = new Recycler_history(Mail, Diagnosis, Diagnosis_result_1, Diagnosis_result_2, Dia_date, img_list);
                                            history.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                                            history.setAdapter(adapter);
                                        }
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle error
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }


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
