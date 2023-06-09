package com.example.mosa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

//이거를 main쪽의 같은 이름의 클래스로 바꿔서 생각
public class Signup extends AppCompatActivity {
    private ImageButton login;
    private EditText email_login;
    private EditText pwd_login;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login_button);
        email_login = findViewById(R.id.TextInputEditText_email);
        pwd_login = findViewById(R.id.TextInputEditText_password);
        firebaseAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(v -> {
            String email = email_login.getText().toString().trim();
            String pwd = pwd_login.getText().toString().trim();
            //String형 변수 email.pwd(edittext에서 받오는 값)으로 로그인하는것
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(Signup.this, task -> {
                        if (task.isSuccessful()) {//성공했을때 이때는 유저의 로그인 정보 받아서 다음 엑티비티에 전달
                            Toast.makeText(Signup.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Signup.this,CustomerActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Signup.this, "잘못된 정보를 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        Button find_pw = findViewById(R.id.find_password);
        find_pw.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), find_password.class);
            startActivity(intent);
        });

        Button signUp = findViewById(R.id.Layout_signup);
        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),
                    subSignup.class);
            startActivity(intent);
        });
    }
}
