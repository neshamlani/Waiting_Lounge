package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class services extends AppCompatActivity {
    TextView dispCat;
    ListView dispList;
    List<String> cat=new ArrayList<>();
    List<String> emails=new ArrayList<>();
    ArrayAdapter<String> adapter;
    FirebaseFirestore fs;
    SwipeRefreshLayout pull;
    String categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        dispCat=(TextView)findViewById(R.id.dispCat);
        dispList=(ListView)findViewById(R.id.dispList);
        pull=(SwipeRefreshLayout)findViewById(R.id.pullservice);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cat);
        dispList.setAdapter(adapter);
        categories=getIntent().getStringExtra("Categories");
        dispCat.setText(categories);
        fs=FirebaseFirestore.getInstance();
        fs.collection(categories).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        cat.clear();
                        emails.clear();
                        for(QueryDocumentSnapshot post:task.getResult()){
                            cat.add(post.getId());

                            try {
                                JSONObject js=new JSONObject(post.getData());
                                emails.add(js.getString("Email"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        pull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pull.setRefreshing(true);
                fs=FirebaseFirestore.getInstance();
                fs.collection(categories).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                cat.clear();
                                for(QueryDocumentSnapshot post:task.getResult()){
                                    cat.add(post.getId());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                pull.setRefreshing(false);
            }
        });
        dispList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in=new Intent(services.this,tokenGenerate.class);
                in.putExtra("email",emails.get(position));
                in.putExtra("categories",categories);
                startActivity(in);
            }
        });
    }
}
