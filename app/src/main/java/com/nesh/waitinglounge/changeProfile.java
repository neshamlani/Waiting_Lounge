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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class changeProfile extends AppCompatActivity {
    EditText editname,editNumber,editAddress;
    String name="",number="",address="",user;
    FirebaseFirestore fs;
    FirebaseAuth mAuth;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        editname=(EditText)findViewById(R.id.editName);
        editNumber=(EditText)findViewById(R.id.editNumber);
        editAddress=(EditText)findViewById(R.id.editAddress);
        pb=findViewById(R.id.updateProgess);
        pb.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        fs=FirebaseFirestore.getInstance();
        user=mAuth.getCurrentUser().getEmail();
        fs.collection("Users_Client").document(user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot post=task.getResult();
                            if(post.exists()){
                                JSONObject js=new JSONObject(post.getData());
                                try {
                                    name = js.getString("Name");
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                try{
                                    number=js.getString("Number");
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                try{
                                    address=js.getString("Address");
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            editname.setText(name);
                            editNumber.setText(number);
                            editAddress.setText(address);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void updateProfile(View view){
        pb.setVisibility(View.VISIBLE);
        name=editname.getText().toString().trim();
        number=editNumber.getText().toString().trim();
        address=editAddress.getText().toString().trim();
        if(name.isEmpty() ||  number.isEmpty() || address.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter ALl The Values",Toast.LENGTH_LONG).show();
            pb.setVisibility(View.GONE);
        }
        else {
            fs=FirebaseFirestore.getInstance();
            Map<String, Object> data=new HashMap<>();
            data.clear();
            data.put("Name",name);
            data.put("Number",number);
            data.put("Address",address);
            fs=FirebaseFirestore.getInstance();
            user=mAuth.getCurrentUser().getEmail();
                fs.collection("Users_Cust").document(user).set(data, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Data Update",Toast.LENGTH_LONG).show();
                                    pb.setVisibility(View.GONE);
                                    Intent in=new Intent(changeProfile.this,profile.class);
                                    startActivity(in);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    pb.setVisibility(View.GONE);
                                }
                            }
                        });

        }
    }
}
