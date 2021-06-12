package com.b11.BusTac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class Activity_login extends AppCompatActivity {

    Button signUpB, forgotPassB, loginB;
    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginB = findViewById(R.id.LoginLoginbtn);
        forgotPassB = findViewById(R.id.LoginForgotpassbtn);
        signUpB = findViewById(R.id.LoginSignUpbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progBar);


        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final String user_Email = email.getText().toString().trim();
                String user_Pass = password.getText().toString().trim();



                if(TextUtils.isEmpty(user_Email)) {
                    Toast.makeText(getApplicationContext(), "E-mail is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(user_Pass)) {
                    Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    progressBar.setVisibility(view.VISIBLE);

                    //---------------------E-mail/Password Authentication-------------------//

                    firebaseAuth.signInWithEmailAndPassword(user_Email, user_Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if(user.isEmailVerified()) {

                                    Intent intent = new Intent(Activity_login.this, Activity_drawer.class);
                                    startActivity(intent);
                                    progressBar.setVisibility(view.INVISIBLE);
                                    finishAffinity();

                                }
                                else {
                                    user.sendEmailVerification();
                                    Toast.makeText(getApplicationContext(), "E-mail not verified. E-mail sent again", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(view.INVISIBLE);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "User Not registered", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(view.INVISIBLE);

                            }



                        }
                    });



                }



            }
        });

        forgotPassB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_login.this, Activity_forgotpassword.class);
                startActivity(intent);
            }
        });

        signUpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_login.this, Activity_Registration.class);
                startActivity(intent);
            }
        });



    }

}