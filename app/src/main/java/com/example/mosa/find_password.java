package com.example.mosa;

import android.content.Intent;
import android.os.Bundle;
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
    private EditText email, password;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_password);

        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        find_button = findViewById(R.id.find_button);
        firebaseAuth = FirebaseAuth.getInstance();



        find_button.setOnClickListener(v -> {
            String email_1 = email.getText().toString().trim();
            String pw = password.getText().toString().trim();
            firebaseAuth.signInWithEmailAndPassword(email_1, pw);
            user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.updatePassword(pw).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(find_password.this, "비밀번호 변경", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(find_password.this,Signup.class);
                    startActivity(intent);
                }
            });




        });

    }
}
