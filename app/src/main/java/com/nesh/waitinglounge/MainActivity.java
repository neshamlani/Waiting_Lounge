package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    EditText loginEmail,loginPassword;
    ProgressBar pbLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore fs;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null) {
                Intent in = new Intent(MainActivity.this, home.class);
                startActivity(in);
                finish();
        }
        pbLogin=(ProgressBar)findViewById(R.id.loginProgessBar);
        pbLogin.setVisibility(View.GONE);
        loginEmail=(EditText)findViewById(R.id.loginEmail);
        loginPassword=(EditText)findViewById(R.id.loginPassword);
        mAuth=FirebaseAuth.getInstance();
    }
    public void login(View view){

        pbLogin.setVisibility(View.VISIBLE);
        String email=loginEmail.getText().toString().trim();
        String password=loginPassword.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty() || password.length()<6){
            Toast.makeText(getApplicationContext(),"Enter EmailID and Password with at least 6 characters",Toast.LENGTH_SHORT).show();
            pbLogin.setVisibility(View.GONE);
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(MainActivity.this, home.class);
                                startActivity(in);
                                finish();
                                pbLogin.setVisibility(View.GONE);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            pbLogin.setVisibility(View.GONE);
                        }
                    });
        }
    }
    public void signup(View view){
        Intent in=new Intent(MainActivity.this,signup.class);
        startActivity(in);
    }
    public void forgotPassword(View view) {
        Intent in=new Intent(MainActivity.this,resetPassword.class);
        startActivity(in);
    }
}
