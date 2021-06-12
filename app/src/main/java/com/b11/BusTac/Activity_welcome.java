package com.b11.BusTac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_welcome extends AppCompatActivity {

    Button loginButton;
    Button signUpButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = findViewById(R.id.Welcomeloginbtn);

        signUpButton = findViewById(R.id.WelcomSignUpbtn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_welcome.this, Activity_Registration.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_welcome.this,Activity_login.class);
                startActivity(intent);
            }
        });
    }
}
