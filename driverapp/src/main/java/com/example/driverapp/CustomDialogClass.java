package com.example.driverapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CustomDialogClass extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    String[] array={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
    HashMap<Object,String> map=new HashMap<>();
    public Button save;
    public Spinner busNoSpinner;
    DatabaseReference databaseReference;
    public CustomDialogClass(@NonNull Activity context) {
        super(context);
        this.c=context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_set_dialog);
        save = (Button) findViewById(R.id.btnSaveNo);
        busNoSpinner = (Spinner) findViewById(R.id.spinnerBusNo);
        save.setOnClickListener(this);
        ArrayAdapter aa = new ArrayAdapter(c, R.layout.spinner_text, array);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        busNoSpinner.setAdapter(aa);



        FirebaseAuth auth=FirebaseAuth.getInstance();
        String uid=auth.getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("location").child("BusNo").child(uid);
    }

    public static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,resource.getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveNo:
                String text = busNoSpinner.getSelectedItem().toString();
                map.put("BusNo",text.trim());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.setValue(map);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dismiss();
                break;
            case R.id.spinnerBusNo:
                break;
            default:
                break;
        }
        dismiss();
    }
}
