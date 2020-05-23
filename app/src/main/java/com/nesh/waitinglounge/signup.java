package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class signup extends AppCompatActivity {
    ProgressBar pbSignup;
    EditText signupEmail,signupPassword,signupNumber;
    FirebaseAuth mAuth;
    FirebaseFirestore fs;
    Button verifyBTN;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verificationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pbSignup=(ProgressBar)findViewById(R.id.signupProgessBar);
        pbSignup.setVisibility(View.GONE);
        signupEmail=(EditText)findViewById(R.id.signupEmail);
        signupPassword=(EditText)findViewById(R.id.signupPassword);
        signupNumber=(EditText)findViewById(R.id.signupNumber);
        mAuth=FirebaseAuth.getInstance();
        fs=FirebaseFirestore.getInstance();
    }
    public void backToLogin(View view){
        finish();
    }
    public void signupUser(View view){
        pbSignup.setVisibility(View.VISIBLE);
        final String email=signupEmail.getText().toString().trim();
        final String password=signupPassword.getText().toString().trim();
        final String number=signupNumber.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty() || password.length()<6){
            Toast.makeText(getApplicationContext(),"Enter EmailID and Password with at least 6 characters",Toast.LENGTH_SHORT).show();
            pbSignup.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(getApplicationContext(),"Verifying Number By OTP",Toast.LENGTH_SHORT).show();
            PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
            mCallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    Toast.makeText(getApplicationContext(),"Number Verified",Toast.LENGTH_SHORT).show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Map<String, Object> data=new HashMap<>();
                                    data.put("Number","+91"+number);
                                    mAuth=FirebaseAuth.getInstance();
                                    String user=mAuth.getCurrentUser().getEmail();
                                    data.put("Email",user);
                                    fs.collection("Users_Clients").document(user).set(data, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(),"Uploading Data To Server",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    Toast.makeText(getApplicationContext(),"Sign Up Successful",Toast.LENGTH_SHORT).show();
                                    pbSignup.setVisibility(View.GONE);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    pbSignup.setVisibility(View.GONE);
                                }
                            });
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            };
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91"+number,
                    60,
                    TimeUnit.SECONDS,
                    signup.this,
                    mCallback
            );

        }
    }
}
