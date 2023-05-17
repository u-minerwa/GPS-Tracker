package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocListenerInterface {
    private LocationManager locationManager;
    private TextView tvDistance, tvVelocity;
    private Location lastLocation;
    private MyLogListener myLocListener;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLogListener();
        myLocListener.setLocListenerInterface(this);
        tvVelocity = findViewById(R.id.tvVelocity);
        tvDistance = findViewById(R.id.tvDistance);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK){
            checkPermission();
        } else {
            Toast.makeText(this, "NO GPS PERMISSION", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission(){                     //menyalas
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 125, 1, myLocListener);
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc.hasSpeed() && lastLocation != null){
            distance += lastLocation.distanceTo(loc);
        }
        lastLocation = loc;
        tvDistance.setText(String.valueOf(distance));
        tvVelocity.setText(String.valueOf(loc.getSpeed()));
    }
}

