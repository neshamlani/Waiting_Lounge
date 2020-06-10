package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

public class cancelToken extends AppCompatActivity {
    FirebaseFirestore fs;
    TextView placeName,tokenDetails;
    SharedPreferences sp;
    String details=null,order,property;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_token);
        placeName=(TextView)findViewById(R.id.placeName);
        tokenDetails=(TextView)findViewById(R.id.tokenDetails);
        sp=this.getSharedPreferences("com.nesh.waitinglounge", Context.MODE_PRIVATE);
        order=sp.getString("Order",null);
        property=sp.getString("Property",null);
        fs=FirebaseFirestore.getInstance();
        fs.collection(property).document(order).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot post=task.getResult();
                            if(post.exists()) {
                                JSONObject js = new JSONObject(post.getData());
                                try {
                                    details = "Email-"+js.getString("Email")
                                            + "\n" +"Date-"+ js.getString("Date")
                                            + "\n" +"Time-"+ js.getString("Time")
                                            + "\n" +"Token Number-"+ js.getString("Token");
                                    tokenDetails.setText(details);
                                    placeName.setText(property);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"No Token Available",Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

    }

    public void cancelTokenByUser(View view){
        fs=FirebaseFirestore.getInstance();
        fs.collection(property).document(order).delete()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Token Cancelled",Toast.LENGTH_LONG).show();
                    tokenDetails.setText(null);
                    placeName.setText(null);
                    sp.edit().remove("timeleft").commit();
                }
            }
        });

    }
}
