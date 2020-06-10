package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {
    FirebaseAuth mAuth;
    ListView listView;
    List<String> categories;
    ArrayAdapter<String> categoriesAdapater;
    FirebaseFirestore fs;
    SwipeRefreshLayout pull;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.logout:
                mAuth=FirebaseAuth.getInstance();
                mAuth.signOut();
                finish();
                break;
            case R.id.profile:
                Intent in=new Intent(home.this,profile.class);
                startActivity(in);
                break;
            case R.id.cancellation:
                in=new Intent(home.this,cancelToken.class);
                startActivity(in);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listView = (ListView) findViewById(R.id.listServices);
        pull=(SwipeRefreshLayout)findViewById(R.id.pullRefresh);
        categories=new ArrayList<String>();
        categories.clear();
        categories.add("Hotel");
        categories.add("Barber");
        categories.add("Clinic");
        categories.add("Dentist");
        categoriesAdapater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        listView.setAdapter(categoriesAdapater);
        fs=FirebaseFirestore.getInstance();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),categories.get(position),Toast.LENGTH_SHORT).show();
                Intent in=new Intent(home.this,services.class);
                in.putExtra("Categories",categories.get(position));
                startActivity(in);
            }
        });
        pull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pull.setRefreshing(true);
                fs=FirebaseFirestore.getInstance();
                fs.collection("Categories")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                categories.clear();
                                for(QueryDocumentSnapshot post: task.getResult()){
                                    categories.add(post.getId());
                                }
                                categoriesAdapater.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                pull.setRefreshing(false);
            }
        });
        TextPaint tp=new TextPaint();
        int color= ContextCompat.getColor(this,R.color.cardview_dark_background);
        Paint paint=new Paint();
        paint.setColor(color);
        paint.setTextSize(50);
        tp.set(paint);
        final com.github.amlcurran.showcaseview.targets.ViewTarget viewTarget= new ViewTarget(R.id.listServices,this);
        new ShowcaseView.Builder(this)
                .setTarget(viewTarget)
                .setContentTitle("Select Categories Of Shop")
                .setContentText("To View All The Related Services")
                .singleShot(1)
                .setContentTextPaint(tp)
                .setContentTitlePaint(tp)
                .build();

    }
}
