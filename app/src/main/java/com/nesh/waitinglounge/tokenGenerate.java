package com.nesh.waitinglounge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class tokenGenerate extends AppCompatActivity {
    TextView propertyName,ownersName,ownerNumber,userNumber,dispTimer;
    FirebaseFirestore fs;
    String pname,currentTime,val;
    Button btn,tBtn;
    SharedPreferences sp;
    FirebaseAuth mAuth;
    CountDownTimer cdt;
    int waitingTime,cancelSucess=0,wait;
    long timeLeft=0;
    ListenerRegistration registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_generate);
        setTitle("Selected Place");
        sp=this.getSharedPreferences("com.nesh.waitinglounge",Context.MODE_PRIVATE);
        propertyName=(TextView) findViewById(R.id.propertyName);
        ownersName=(TextView)findViewById(R.id.ownersName);
        ownerNumber=(TextView)findViewById(R.id.ownerNumber);
        userNumber=(EditText)findViewById(R.id.userNumber);
        dispTimer=(TextView)findViewById(R.id.dispTimer);
        dispTimer.setVisibility(View.GONE);
        btn=(Button)findViewById(R.id.cancel);
        tBtn=(Button)findViewById(R.id.tokenBtn);
        btn.setVisibility(View.GONE);
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
                                    waitingTime= Integer.parseInt(js.getString("Time"));
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
        TextPaint tp=new TextPaint();
        int color= ContextCompat.getColor(this,R.color.cardview_dark_background);
        Paint paint=new Paint();
        paint.setColor(color);
        paint.setTextSize(50);
        tp.set(paint);
        com.github.amlcurran.showcaseview.targets.ViewTarget viewTarget= new ViewTarget(R.id.userNumber,this);
        new ShowcaseView.Builder(this)
                .setTarget(viewTarget)
                .setContentTitle("Enter Number")
                .setContentText("To Get Token")
                .singleShot(3)
                .setContentTextPaint(tp)
                .setContentTitlePaint(tp)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateToken(View view){
        String categories=getIntent().getStringExtra("categories");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        final Map<String, Object> data=new HashMap<>();
        final String email=mAuth.getCurrentUser().getEmail();
        fs=FirebaseFirestore.getInstance();
        String number=userNumber.getText().toString().trim();
        final SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault());
        currentTime=timeFormat.format(new Date());
        final String currentDate=dateFormat.format(new Date());
        final String order=sp.getString("Order",null);
        data.clear();
        data.put("Number",number);
        data.put("Email",email);
        data.put("Date",currentDate);
        data.put("Time",currentTime);
        val=currentTime+email;
        if(order==null){
            fs=FirebaseFirestore.getInstance();
            fs.collection(pname).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            int i;
                            i=0;
                            for(DocumentSnapshot post:task.getResult()){
                                i++;
                            }
                            Toast.makeText(getApplicationContext(),Integer.toString(i),Toast.LENGTH_LONG).show();
                            data.put("Token",i+1);
                            i=i+1;
                            wait=i*waitingTime*60*1000;
                            data.put("Property_Name",pname);
                            fs=FirebaseFirestore.getInstance();
                            fs.collection(pname).document(val).set(data);
                            cdt=new CountDownTimer(wait,1000) {
                                @Override
                                public void onTick(long wait) {
                                    timeLeft=wait;
                                    updateTimer((int)wait/1000);
                                }

                                @Override
                                public void onFinish() {

                                }
                            }.start();
                        }
                    });


            sp.edit().putString("Order",val).commit();
            sp.edit().putString("Property",pname).commit();
            Toast.makeText(getApplicationContext(),sp.getString("Order",null),Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(),"notification",Toast.LENGTH_LONG).show();
            NotificationChannel notificationChannel=new NotificationChannel("1","Order",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.GREEN);
            Intent in=new Intent(tokenGenerate.this,tokenGenerate.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1970,in,PendingIntent.FLAG_ONE_SHOT);
            Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder notifyBuilder=new Notification.Builder(getApplicationContext(),"1")
                    .setSmallIcon(R.color.colorAccent)
                    .setContentTitle("Your Token")
                    .setContentText("Date:"+currentDate+" Time:"+currentTime)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setChannelId("1")
                    .setSound(soundUri);
            NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(notificationChannel);
            nm.notify(1970,notifyBuilder.build());
            dispTimer.setVisibility(View.VISIBLE);

            userNumber.setText(null);
            tBtn.setEnabled(false);
            btn.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(getApplicationContext(),"Cannot get token twice",Toast.LENGTH_LONG).show();
            finish();
        }
        Query q=fs.collection(pname);
        registration=q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        for(DocumentChange dc:queryDocumentSnapshots.getDocumentChanges()){
                            switch (dc.getType()){
                                case REMOVED:
                                    String removed=dc.getDocument().getId();
                                    NotificationChannel notificationChannel=new NotificationChannel("2","Order",NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationChannel.enableLights(true);
                                    notificationChannel.enableVibration(true);
                                    notificationChannel.setLightColor(Color.GREEN);
                                    Intent in=new Intent(tokenGenerate.this,tokenGenerate.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1971,in,PendingIntent.FLAG_ONE_SHOT);
                                    Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    Notification.Builder notifyBuilder=new Notification.Builder(getApplicationContext(),"2")
                                            .setSmallIcon(R.color.colorAccent)
                                            .setContentTitle("Someone Left")
                                            .setContentText(removed)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(false)
                                            .setChannelId("2")
                                            .setSound(soundUri);
                                    NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                    nm.createNotificationChannel(notificationChannel);
                                    nm.notify(1971,notifyBuilder.build());
                                    break;
                            }
                        }
                    }
                });
    }
    public void cancelToken(View view){
        cdt.cancel();
        sp.edit().remove("timeleft").commit();
        sp.edit().remove("Order").commit();
        sp.edit().remove("Property").commit();
        dispTimer.setVisibility(View.GONE);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        final String email=mAuth.getCurrentUser().getEmail();
        fs.collection(pname).document(val).delete()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Cancel Successful",Toast.LENGTH_LONG).show();
                    fs.collection(pname).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int i=0;
                                    for(QueryDocumentSnapshot post:task.getResult()){
                                        i++;
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("Token", i);
                                        fs.collection(pname).document(post.getId()).set(data, SetOptions.merge());
                                    }
                                }
                            });
                    btn.setVisibility(View.GONE);
                    tBtn.setEnabled(true);
                    cancelSucess=1;
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    btn.setVisibility(View.GONE);
                    tBtn.setEnabled(true);
                }
            }
        });
    }
    public void updateTimer(int timerValue){
        int min=timerValue/60;
        int sec=timerValue-(min*60);
        String secString=Integer.toString(sec);
        if(secString.equals("0")){
            secString="00";
        }
        dispTimer.setText("Time left till your turn:  "+min+":"+secString);
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeLeft=sp.getLong("timeleft",0);
        dispTimer.setVisibility(View.VISIBLE);
        timeLeft=timeLeft-System.currentTimeMillis();
        cdt=new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long wait) {
                timeLeft=wait;
                updateTimer((int)wait/1000);
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cancelSucess==0) {
            timeLeft = timeLeft + System.currentTimeMillis();
            sp.edit().putLong("timeleft", timeLeft).commit();
        }
        registration.remove();
    }
}
