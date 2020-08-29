package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {
    FirebaseAuth mAuth;
    ListView listView;
    List<String> categories;
    ArrayAdapter<String> categoriesAdapater;
    FirebaseFirestore fs;
    SwipeRefreshLayout pull;
    SharedPreferences sp;
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
            case R.id.aboutDev:
                in=new Intent(home.this,aboutus.class);
                startActivity(in);
                break;

            case R.id.termsandconditions:
                in=new Intent(home.this,termsandconditions.class);
                startActivity(in);
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.i("Permission","Granted");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
        listView = (ListView) findViewById(R.id.listServices);
        //pull=(SwipeRefreshLayout)findViewById(R.id.pullRefresh);
        categories=new ArrayList<String>();
        categories.clear();
        categories.add("Hotel");
        categories.add("Barber");
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
        /*pull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        });*/
        final TextPaint tp=new TextPaint();
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

        sp=this.getSharedPreferences("com.nesh.waitinglounge", Context.MODE_PRIVATE);
        mAuth=FirebaseAuth.getInstance();
        String email=mAuth.getCurrentUser().getEmail();
        fs.collection("Users_Clients").document(email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot post=task.getResult();
                        JSONObject js=new JSONObject(post.getData());
                        try {
                            sp.edit().putString("Name",js.getString("Name")).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    public void listToken(View v){
        Intent in=new Intent(home.this,waiting.class);
        startActivity(in);
    }
    public void cancel(View v){
        Intent in=new Intent(home.this,cancelToken.class);
        startActivity(in);
    }
}
