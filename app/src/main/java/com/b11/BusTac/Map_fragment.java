package com.b11.BusTac;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map_fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final String TAG = "Map_fragment";
    private final Context mContext;
    GoogleMap mMap;
    Marker mark;
    String uid;
    LatLng markerLoc;
    FirebaseAuth auth;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    TextView busNo,ArriveTime,distance;
    BottomSheetBehavior bottomSheetBehavior;
    CoordinatorLayout coordinatorLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference databaseReference;

    public Map_fragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.coordinateLayout);
        View bottomSheet = view.findViewById(R.id.bottomSheet);
        busNo=bottomSheet.findViewById(R.id.BusNo);
        distance=bottomSheet.findViewById(R.id.distance);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("location").child("driver");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            } else {
                checkLocationPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        //current location of user
        mMap.setMyLocationEnabled(true);
    }

    //location of current device
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getContext() != null) {
                    mLastLocation = location;
                    if (!getDriversAroundStarted) {
                        getAroundBuses();//get buses around the radius
                    }
                }
            }
        }
    };
    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    HashMap<Object, String> map;
    String BusNo="";

    private void getAroundBuses() {
        getDriversAroundStarted = true;
        GeoFire geoFire = new GeoFire(databaseReference);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {

                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        return;
                    }
                }

                final LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child("BusNo");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            map = (HashMap) snapshot.child(key).getValue();
                            BusNo = map.get("BusNo");
                            Marker mdriver = mMap.addMarker(new MarkerOptions().position(driverLocation).title("BusNo " + BusNo).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                            mdriver.setTag(key);
                            LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            moveToCurrentLocation(myLocation);
                            markers.add(mdriver);
                            Log.d(TAG, "onDataChange: " + map.get("BusNo"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Marker markerIt : markers) {
            if (markerIt.getTag().equals(marker.getTag())) {
                driverInfo(marker);
                Toast.makeText(getContext(), "Clicked" + marker.getTitle(), Toast.LENGTH_SHORT).show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        }
        return false;
    }

    private void driverInfo(Marker marker) {
        busNo.setText(""+marker.getTitle());
        Location location=new Location("");
        location.setLatitude(mLastLocation.getLatitude());
        location.setLongitude(mLastLocation.getLongitude());

        Location location2=new Location("");
        LatLng driver=marker.getPosition();
        location2.setLatitude(driver.latitude);
        location2.setLongitude(driver.longitude);

        float cal=location.distanceTo(location2);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double km = cal * 0.001;
        // distance
        distance.setText("Distance "+df.format(km)+" KM");
    }
}

