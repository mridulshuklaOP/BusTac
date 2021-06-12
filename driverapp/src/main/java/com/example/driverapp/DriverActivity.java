package com.example.driverapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DriverActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    String provider;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    DatabaseReference ref;
    String uid;
    ImageView setBusNo;
    TextView showBusNo;
    Button logout;
    private double driverLat, driverLong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        setBusNo = (ImageView) findViewById(R.id.setBusNo);
        showBusNo=(TextView)findViewById(R.id.showBusNo);
        setBusNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBusNo();
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SwitchMaterial toggle = (SwitchMaterial) findViewById(R.id.switchLocation);
        logout = (Button) findViewById(R.id.logout);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("location").child("driver");
        ref=FirebaseDatabase.getInstance().getReference().child("location").child("BusNo").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<Object,String> map=(HashMap)snapshot.getValue();
                    showBusNo.setText(map.get("BusNo"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggle.isChecked()) {
                    locationManager.removeUpdates(DriverActivity.this);
                    Toast.makeText(DriverActivity.this, "Location Stopped", Toast.LENGTH_SHORT).show();
                } else if (toggle.isChecked()) {
                    if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DriverActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        return;
                    }
                    locationManager.requestLocationUpdates(provider, 400, 1, DriverActivity.this);
                    Toast.makeText(DriverActivity.this, "Location Started", Toast.LENGTH_SHORT).show();
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (auth != null) {
                    auth.signOut();
                    startActivity(new Intent(getApplicationContext(), Activity_login.class));
                    finishAffinity();
                } else {
                    Toast.makeText(DriverActivity.this, "Account not exist", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Toast.makeText(this, "Location not fetched", Toast.LENGTH_SHORT).show();
        }

    }

    private void setBusNo() {
        CustomDialogClass cdd=new CustomDialogClass(this);
        cdd.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = (double) (location.getLatitude());
        double lng = (double) (location.getLongitude());
        driverLat = lat;
        driverLong = lng;
        Toast.makeText(this, "" + lat + "," + lng, Toast.LENGTH_SHORT).show();
        LocationModel driver = new LocationModel(driverLong, driverLat);
//        databaseReference.child("driver").setValue(driver);
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.setLocation(uid, new GeoLocation(driver.getLatitude(), driver.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}