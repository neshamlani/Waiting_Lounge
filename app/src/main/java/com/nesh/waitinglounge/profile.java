package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class profile extends AppCompatActivity {
    FirebaseFirestore fs;
    FirebaseAuth mAuth;
    TextView displayName,displayEmail,displayNumber,displayAddress;
    String name="",email="",url="",number="",address="";
    ImageView im;
    SwipeRefreshLayout srl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");
        displayName=(TextView)findViewById(R.id.displayName);
        displayEmail=(TextView)findViewById(R.id.displayEmail);
        displayNumber=(TextView)findViewById(R.id.displayNumber);
        displayAddress=(TextView)findViewById(R.id.displayAddress);
        im=(ImageView)findViewById(R.id.displayPhoto);
        srl=(SwipeRefreshLayout)findViewById(R.id.profileRefersh);
        dataQuery();
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataQuery();
                srl.setRefreshing(false);
            }
        });
    }
    public void changeProfilePhoto(View view){
        Intent in=new Intent(profile.this,changePhoto.class);
        in.putExtra("Url",url);
        startActivity(in);
    }
    public void dataQuery(){
        mAuth=FirebaseAuth.getInstance();
        fs=FirebaseFirestore.getInstance();
        String user=mAuth.getCurrentUser().getEmail();
        fs.collection("Users_Clients").document(user)
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
                                    email = js.getString("Email");
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                try{
                                    url=js.getString("Profile_Photo");
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
                            displayName.setText(name);
                            displayEmail.setText(email);
                            displayNumber.setText(number);
                            displayAddress.setText(address);
                            if(url.isEmpty()){}
                            else {
                                Picasso.with(getApplicationContext()).load(url).into(im);
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
