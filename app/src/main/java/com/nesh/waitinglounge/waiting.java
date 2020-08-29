package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class waiting extends AppCompatActivity {
    SharedPreferences sp;
    List<String> name=new ArrayList<>();
    FirebaseFirestore fs;
    TextView tokdisp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        sp=this.getSharedPreferences("com.nesh.waitinglounge", Context.MODE_PRIVATE);
        String property=sp.getString("Property",null);
        final int token=sp.getInt("token",0);
        tokdisp=findViewById(R.id.tokdisp);
        tokdisp.setText("Your Token Number:"+Integer.toString(token));
        final RecyclerView rv=findViewById(R.id.recycleWaiting);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        fs=FirebaseFirestore.getInstance();
        try{
            fs.collection(property).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot post:task.getResult()) {
                                    JSONObject js = new JSONObject(post.getData());
                                    try {
                                        int t=Integer.parseInt(js.getString("Token"));
                                        if(t<=token){
                                            name.add(String.valueOf(t));
                                        }
                                        else{
                                            return;
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                                carddisplay c=new carddisplay(name);
                                rv.setAdapter(c);
                            }
                        }
                    });
        }catch (Exception e){
            tokdisp.setText("No Token Generated");
        }
    }
}
