package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetPassword extends AppCompatActivity {
    FirebaseAuth mAuth;
    ProgressBar pbresetProgessBar;
    String user;
    EditText resetEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("Reset Password");
        mAuth=FirebaseAuth.getInstance();
        pbresetProgessBar=(ProgressBar)findViewById(R.id.resetProgessBar);
        pbresetProgessBar.setVisibility(View.GONE);
        resetEmail=(EditText)findViewById(R.id.resetEmail);
    }
    public void resetPwd(View view){
        pbresetProgessBar.setVisibility(View.VISIBLE);
        user=resetEmail.getText().toString().trim();
        mAuth.sendPasswordResetEmail(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Reset Link Sent",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        pbresetProgessBar.setVisibility(View.GONE);
                        finish();
                    }
                });
    }
}
