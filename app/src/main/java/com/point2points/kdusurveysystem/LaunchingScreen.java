package com.point2points.kdusurveysystem;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.point2points.kdusurveysystem.R;
import com.point2points.kdusurveysystem.RecylcerView.RecyclerViewExample;

public class LaunchingScreen extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 500;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_launching_screen);

        if(CheckNetwork.isInternetAvailable(LaunchingScreen.this)) //returns true if internet available
        {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                                startActivity(new Intent(LaunchingScreen.this, Login.class));
                                finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
        else
        {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaunchingScreen.this);
            alertDialogBuilder.setTitle("Connection error");
            alertDialogBuilder.setMessage("Unable to connect with the server.\nCheck your internet connection and try again");
            alertDialogBuilder.setNeutralButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
                }
            }, SPLASH_DISPLAY_LENGTH);

    }
}
}