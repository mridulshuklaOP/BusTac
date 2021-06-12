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
import com.google.firebase.auth.FirebaseAuth;

public class Activity_forgotpassword extends AppCompatActivity {
    private EditText Uemail;
    private Button resetlink;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Uemail = findViewById(R.id.editTextTextEmailAddress);
        resetlink = findViewById(R.id.sendresetlinkbtn);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progBar);


        resetlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


               String email = Uemail.getText().toString();


               if (TextUtils.isEmpty(email))
               {
                   Toast.makeText(Activity_forgotpassword.this,"E-mail is required", Toast.LENGTH_SHORT).show();

               }
               else {

                   progressBar.setVisibility(view.VISIBLE);

                   //---------------------forgotpassword-----------------//


                 mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             Toast.makeText(Activity_forgotpassword.this,"Reset link sent to your E-mail", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(Activity_forgotpassword.this,Activity_login.class));
                             progressBar.setVisibility(view.INVISIBLE);
                         }
                         else
                         {
                             String message = task.getException().getMessage();
                             Toast.makeText(Activity_forgotpassword.this,"Error : "+message, Toast.LENGTH_SHORT).show();
                             progressBar.setVisibility(view.INVISIBLE);
                         }

                     }
                 });
               }
               }
        });



    }
}