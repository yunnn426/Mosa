package com.example.mosa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class Signup extends AppCompatActivity {
    private Button login;
    private EditText email_login;
    private EditText pwd_login;
    FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login_button);
        email_login = findViewById(R.id.TextInputEditText_email);
        pwd_login = findViewById(R.id.TextInputEditText_password);
        firebaseAuth = FirebaseAuth.getInstance();//firebaseAuth의 인스턴스를 가져옴

        login.setOnClickListener(v -> {
            String email = email_login.getText().toString().trim();
            String pwd = pwd_login.getText().toString().trim();
            //String형 변수 email.pwd(edittext에서 받오는 값)으로 로그인하는것
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(Signup.this, task -> {
                        if (task.isSuccessful()) {//성공했을때
                            Toast.makeText(Signup.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, Sucesslogin.class);
                            startActivity(intent);
                        } else {//실패했을때
                            Toast.makeText(Signup.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        Button imageButton = findViewById(R.id.Layout_signup);
        imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),
                    subSignup.class);
            startActivity(intent);
        });


    }
}
