package com.example.mosa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class find_password extends AppCompatActivity {
    private Button find_button;
    private EditText email;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_password);
        getSupportActionBar().hide();

        email = findViewById(R.id.input_email);

        find_button = findViewById(R.id.find_button);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        find_button.setOnClickListener(v -> {
            String email_1 = email.getText().toString().trim();

            firebaseAuth.sendPasswordResetEmail(email_1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(find_password.this, "이메일을 전송했습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(find_password.this,Signup.class);
                                startActivity(intent);
                            }
                        }
                    });
        });

    }
}
