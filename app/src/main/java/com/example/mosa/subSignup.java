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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class subSignup extends AppCompatActivity {
    private EditText email_join;
    private EditText pwd_join;
    private EditText name_join;
    private Button btn;
    FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email_join = findViewById(R.id.TextInputEditText_email);
        pwd_join = findViewById(R.id.TextInputEditText_password);
        name_join = findViewById(R.id.TextInputEditText_name);
        btn = findViewById(R.id.Signup_button);

        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(v -> {
            final String email = email_join.getText().toString().trim();
            final String pwd = pwd_join.getText().toString().trim();
            final String name = name_join.getText().toString().trim();
            //공백인 부분을 제거하고 보여주는 trim();


            firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(subSignup.this, task -> {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(subSignup.this, Signup.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(subSignup.this, "등록 에러 :"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

            });
        Button imageButton = findViewById(R.id.back_login);
        imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),
                    Signup.class);
            startActivity(intent);
        });
    }
}
