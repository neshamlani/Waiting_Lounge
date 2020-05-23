package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class tokenGenerate extends AppCompatActivity {
    TextView propertyName,ownersName,ownerNumber,userNumber;
    FirebaseFirestore fs;
    String pname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_generate);
        setTitle("Selected Place");
        propertyName=(TextView) findViewById(R.id.propertyName);
        ownersName=(TextView)findViewById(R.id.ownersName);
        ownerNumber=(TextView)findViewById(R.id.ownerNumber);
        userNumber=(EditText)findViewById(R.id.userNumber);
        fs=FirebaseFirestore.getInstance();
        String user=getIntent().getStringExtra("email");
        fs.collection("Users_Cust")
                .document(user).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot post=task.getResult();
                            if(post.exists()){
                                JSONObject js=new JSONObject(post.getData());
                                try{
                                    String name=js.getString("Name");
                                    ownersName.setText("Owners Name:"+name);
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                                try{
                                    String number=js.getString("Number");
                                    ownerNumber.setText("Owners Number:"+number);
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                                try{
                                    pname=js.getString("Property_Name");
                                    propertyName.setText("Place:"+pname);
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateToken(View view){
        String categories=getIntent().getStringExtra("categories");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        Map<String, Object> data=new HashMap<>();
        String email=mAuth.getCurrentUser().getEmail();
        String number=userNumber.getText().toString().trim();
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault());
        String currentTime=timeFormat.format(new Date());
        String currentDate=dateFormat.format(new Date());
        data.clear();
        data.put("Number",number);
        data.put("Date",currentDate);
        data.put("Time",currentTime);
        fs=FirebaseFirestore.getInstance();
        fs.collection(pname).document(email).set(data);
        //Toast.makeText(getApplicationContext(),"notification",Toast.LENGTH_LONG).show();
        NotificationChannel notificationChannel=new NotificationChannel("1","Order",NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GREEN);
        Intent in=new Intent(tokenGenerate.this,home.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1970,in,PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notifyBuilder=new Notification.Builder(this,"1")
                .setSmallIcon(R.color.colorAccent)
                .setContentTitle("Waiting List")
                .setContentText(currentDate+" "+currentTime)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setChannelId("1")
                .setSound(soundUri);
        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(notificationChannel);
        nm.notify(1970,notifyBuilder.build());
    }
}
