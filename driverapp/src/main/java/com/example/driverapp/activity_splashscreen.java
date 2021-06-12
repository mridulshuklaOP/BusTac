package com.example.driverapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_splashscreen extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.splashLogo);
        name = findViewById(R.id.splashName);

        image.setAnimation(topAnim);
        name.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    Intent i = new Intent(activity_splashscreen.this, DriverActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(activity_splashscreen.this, Activity_login.class);
                    startActivity(i);
                    finish();
                }
                } }, 1000);
    }
}


