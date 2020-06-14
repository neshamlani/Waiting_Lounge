package com.nesh.waitinglounge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class aboutus extends AppCompatActivity {
    TextView about,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        setTitle("About Us / Contact Us");
        about=findViewById(R.id.about);
        contact=findViewById(R.id.contact);
        String abt="With the help of this app you will not have to worry about the queue at your favorite as with this app " +
                "you will get a unique token at will be able to see how much time is left before your turn comes hence need not to worry " +
                "as in this pandemic situation no one wants to stand in queue and maintaining social distance is difficult with the help" +
                " of this app you can stay at home and get a token for the waiting list and have not to worry about not finding a place";
        about.setText(abt);
        String cnt="Contact us for bug reports or help for account related problems\nOur official mailID: waitingloungebymitesh@gmail.com" +
                "\nor call us at: +917043733177";
        contact.setText(cnt);
    }
}
