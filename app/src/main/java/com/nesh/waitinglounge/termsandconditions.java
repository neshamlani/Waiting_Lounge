package com.nesh.waitinglounge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class termsandconditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandconditions);
        TextView termsOfUse,dataPolicy;
        termsOfUse=findViewById(R.id.termsOfUse);
        dataPolicy=findViewById(R.id.dataPolicy);
        String use="Welcome to Waiting Lounge.\nThese terms of use will govern your use of this application and provide the information" +
                " about the service,outlined below, By creating your account or use of this application" +
                " you agree to this terms and conditions with all the data policies.\n\n\n" +
                "Our services.\n\nWe agree to provide you following services.\n\n-> Generating waiting token for the places who are using " +
                "our services: \nThe user can get the token for the waiting queue while they are at home because of this user need not to be" +
                " present at the place all the time and can arrive at the place when there turn is about to come." +
                "\n\n-> Alerting the user for there turn:\nWhen the own of the place calls the next person in the waiting queue that person" +
                " will get a text message from that place regarding and reminding about there turn.\n" +
                "\n-> Acknowledging the time left:\nEach user who has generated the token will get a countdown timer displaying the time left" +
                " for there turn and the timer is updated in realtime so that if someone left queue the timer will display the new time" +
                " and will refresh the queue." +
                "\n\n-> Verifying the user using OTP:\nEach time when a user or the owner of the place creates and account they have to" +
                " provide there contact number which is verified during the time of the registration on the application. In case of user" +
                " there also have to verify there number while generating the token for the queue." +
                "\n\n-> Role of owner of a place:\nAs soon as someone generates a token owner will be notified and owner can see the people" +
                " in the queue which will contain a number of things like:EmailID,Contact Number,Token etc. As the owner clicks on next" +
                " customer button the queue will be updated and all the people in that queue will be notified.";
        termsOfUse.setText(use);

        String dataPol="This data policy describes how your data is used throughout the application and how it helps the owners of the place" +
                "\n\nWhat kind of information do we collect:\nTo provide all the service we offer we need user's some information. Following" +
                " are the information we need:\n1.Email ID.\n2.Contact Number\n3.Address.\n4.Name.\n5.Profile Photo(optional)." +
                "\n\nIn case of the owners we need:\n1.Email ID.\n2.Business Number.\n3.Address of the business property.\n4.Business Name." +
                "\n5.Owner's Name.\n6.Profile Photo(optional).\n7.Time they allot per customer in minutes." +
                "\n\nHow we use this information:\nWe need this information so that the owner of the place can recognise you. This information" +
                " is used to first verify the user, as it would be problematic if there are fake users using this application.\n" +
                "-> User's number and emailID is displayed to the owner as soon as user generates the token for that particular place." +
                " And this information is used to make contact with the user in any case required or needed by the owner of the place " +
                "or by the organization running this application.\n" +
                "\n-> Verifying the user:\nUser will be verified first while creating account or registering in this application." +
                " And then user will be verified each time when user generates the token.\nFirst verification is done for the safety of" +
                " organization of this application. And verification done while generating token is done for the safety of owner of the place." +
                "\n\n-> Using the data:\nData given by the user will be used only when user generates the token." +
                "While in the case when user has not generated any token there data will not be in use at any place and is secure at the servers.";

        dataPolicy.setText(dataPol);
    }
}
