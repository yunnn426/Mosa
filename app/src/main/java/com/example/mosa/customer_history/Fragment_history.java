package com.example.mosa.customer_history;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.CustomerInfo;
import com.example.mosa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Fragment_history extends Fragment {

    TextView msg;
    Recycler_history adapter;
    ArrayList<String> Mail;
    ArrayList<String> Diagnosis;
    ArrayList<String> Diagnosis_result;
    ArrayList<String> Dia_date;
    RecyclerView history;
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
        Diagnosis_result=new ArrayList<String>();
        Dia_date=new ArrayList<String>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference diagnosis_record=db.collection("user_record");

        //'test.email.com'은 테스트용입니다.
        //Query query=diagnosis_record.whereEqualTo("user_email",user.getEmail());
        Query query=diagnosis_record.whereEqualTo("user_email","test.email.com");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryresult) {
                // 결과 처리
                for (DocumentSnapshot documentSnapshot : queryresult) {
                    // 각각의 문서에서 필요한 데이터 가져오기
                    String diagnosisType = documentSnapshot.getString("diagnosis");
                    String diagnosisResult = documentSnapshot.getString("diagnosis_result");
                    String diagnosisDate = documentSnapshot.getString("diagnosis_date");

                    Mail.add(user.getEmail());
                    Diagnosis.add(diagnosisType);
                    Diagnosis_result.add(diagnosisResult);
                    Dia_date.add(diagnosisDate);
                    // 가져온 데이터를 arraylist 형식으로 저장하고 리사이클러 뷰를 통해 사용자에게 보여준다.
                }
                Log.d("test","작동성공");


                Context context = getContext();
                msg.setVisibility(View.INVISIBLE);
                adapter = new Recycler_history(Mail, Diagnosis, Diagnosis_result, Dia_date);
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
