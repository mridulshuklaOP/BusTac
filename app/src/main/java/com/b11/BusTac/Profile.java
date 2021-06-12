package com.b11.BusTac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";
    private TextView name, enrol, mobile, email;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fstore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //setup UI
        hooks();
        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = fstore.collection("USERS").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProfileModel profileModel=documentSnapshot.toObject(ProfileModel.class);
                email.setText(profileModel.getEmail());
                enrol.setText(profileModel.getEnrollmentNumber());
                name.setText(profileModel.getFullName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed");
            }
        });
    }

    private void hooks() {
        name = findViewById(R.id.sname);
        enrol = findViewById(R.id.senrol);
        email = findViewById(R.id.semail);
    }
}