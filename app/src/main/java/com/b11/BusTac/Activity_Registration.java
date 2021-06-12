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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Activity_Registration extends AppCompatActivity {


    private EditText name, email, enroll, pass, conPas;
    Button regB;
    String userID;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        name = findViewById(R.id.registrationPersonName);
        email = findViewById(R.id.registrationEmailAddress);
        enroll = findViewById(R.id.registrationEnrollmentNo);
        pass = findViewById(R.id.registrationPassword);
        conPas = findViewById(R.id.registrationConfirmPassword);
        regB = findViewById(R.id.RegistrationRegisterbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progBar);



        regB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final String user_name = name.getText().toString().trim();
                final String user_email = email.getText().toString().trim();
                final String user_enrolno = enroll.getText().toString().trim();
                final String user_pass = pass.getText().toString().trim();
                final String user_conpass = conPas.getText().toString().trim();



                if(TextUtils.isEmpty(user_name)) {
                        Toast.makeText(getApplicationContext(), "Name is required", Toast.LENGTH_SHORT).show();
                        return;
                }

                if(TextUtils.isEmpty(user_email)) {
                    Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(user_enrolno)) {
                    Toast.makeText(getApplicationContext(), "Enrollment no. is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(user_pass)) {
                    Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(user_conpass)) {
                    Toast.makeText(getApplicationContext(), "Please confirm the password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!user_pass.equals(user_conpass)) {
                    Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    progressBar.setVisibility(view.VISIBLE);


                    //--------------USER_REGESTRATION------------//

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //Toast.makeText(getApplicationContext(), "Verification email has been sent.", Toast.LENGTH_SHORT).show();

                                        //----------------firestore---------------//
                                        userID = firebaseAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fstore.collection("USERS").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("FullName", user_name);
                                        user.put("Email", user_email);
                                        user.put("EnrollmentNumber", user_enrolno);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Account Created. Check your E-mail", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        startActivity(new Intent(Activity_Registration.this, Activity_login.class));
                                        progressBar.setVisibility(view.INVISIBLE);

                                    }

                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(view.INVISIBLE);
                            }

                        }
                    });
                }


            }
        });

    }

}