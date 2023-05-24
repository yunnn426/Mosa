package com.example.mosa;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class subSignup extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    EditText mEmailText, mPasswordText, mPasswordcheckText, mName;
    ImageButton mregisterBtn;
    private FirebaseAuth firebaseAuth;

    private EditText email_join;
    private EditText pwd_join;
    private EditText name_join;
    private Button btn;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        //액션 바 등록하기
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Create Account");
//        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기버튼
//        actionBar.setDisplayShowHomeEnabled(true); //홈 아이콘

//        email_join = findViewById(R.id.TextInputEditText_email);
//        pwd_join = findViewById(R.id.TextInputEditText_password);
//        name_join = findViewById(R.id.TextInputEditText_name);
//        btn = findViewById(R.id.Signup_button);

        firebaseAuth = FirebaseAuth.getInstance();

        mEmailText = findViewById(R.id.TextInputEditText_email);
        mPasswordText = findViewById(R.id.TextInputEditText_password);
        mPasswordcheckText = findViewById(R.id.TextInputEditText_passwordcheck);
        mregisterBtn = findViewById(R.id.Signup_button);
        mName = findViewById(R.id.TextInputEditText_name);

        mregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailText.getText().toString();
                String pwd = mPasswordText.getText().toString();
                String pwdCheck = mPasswordcheckText.getText().toString();
                String name = mName.getText().toString().trim();

                if (pwd.equals(pwdCheck)) {
                    Log.d(TAG, "등록 버튼 " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(subSignup.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();

                    //파이어베이스에 신규계정 등록하기
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(subSignup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //가입 성공시
                            if (task.isSuccessful()) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                                Date date=new Date();

                                mDialog.dismiss();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();
                                String createuid=format.format(date);
                                String updateuid=format.format(date);

                                //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                                HashMap<Object, String> hashMap = new HashMap<>();

                                hashMap.put("uid", uid);
                                hashMap.put("email", email);
                                hashMap.put("name", name);
                                hashMap.put("create_date",createuid);
                                hashMap.put("modify_date",updateuid);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> data = new HashMap<>();
                                data.put("시작데이터", "해당 사용자의 결과를 저장합니다");
                                db.collection(email+"_color_result").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                                //가입이 이루어져을시 가입 화면을 빠져나감.
                                Intent intent = new Intent(subSignup.this, Signup.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(subSignup.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(subSignup.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;  //해당 메소드 진행을 멈추고 빠져나감.
                            }
                        }
                    });
                } else {

                    Toast.makeText(subSignup.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

//        Button find_pw = findViewById(R.id.find_button);
//        find_pw.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(),
//                    find_password.class);
//        });

        Button imageButton = findViewById(R.id.back_login);
        imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),
                    Signup.class);
            startActivity(intent);
        });
    }
}
