package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocListenerInterface {
    private LocationManager locationManager;
    private TextView tvDistance, tvVelocity, tvVelocityDA;
    private Location lastLocation;
    private MyLogListener myLocListener;
    private int distance;
    private int total_distance, rest_distance;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        tvVelocity = findViewById(R.id.tvVelocity);
        tvVelocityDA = findViewById(R.id.tvVelocityDA);
        tvDistance = findViewById(R.id.tvDistance);
        pb = findViewById(R.id.progressBar);
        pb.setMax(1000);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLogListener();
        myLocListener.setLocListenerInterface(this);

        checkPermission();
    }

    private void setDistance(String dist){
        pb.setMax(Integer.parseInt(dist));
        rest_distance = Integer.parseInt(dist);
        distance = Integer.parseInt(dist);
        tvDistance.setText(dist);
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title);
        @SuppressLint("InflateParams")
        ConstraintLayout cl = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_layout, null);
        builder.setPositiveButton(R.string.dialog_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog alertDialog = (AlertDialog) dialogInterface;
                EditText editText = (EditText) alertDialog.findViewById(R.id.edText);
                if (editText != null){
                    if (!editText.getText().toString().equals("")){
                        setDistance(editText.getText().toString());
                    }
                }
            }
        });
        builder.setView(cl);
        builder.show();
    }

    public void onClickDistance(View view){
        showDialog();
    }

    private void updateDist(Location loc){
        if (loc.hasSpeed() && lastLocation != null){
            if (distance > total_distance){
                total_distance += lastLocation.distanceTo(loc);
            }
            if (rest_distance > 0){
                rest_distance -= lastLocation.distanceTo(loc);
            }
            pb.setProgress(total_distance);
        }
        lastLocation = loc;
        tvDistance.setText(String.valueOf(rest_distance));
        tvVelocity.setText(String.valueOf(total_distance));
        tvVelocityDA.setText(String.valueOf(loc.getSpeed()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK){
            checkPermission();
        }
        //  else {
        //      Toast.makeText(this, "NO GPS PERMISSION", Toast.LENGTH_SHORT).show();
        //  }
    }

    private void checkPermission2(){                     //menyalas
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 1, myLocListener);
        }
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 1, myLocListener);
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        updateDist(loc);
    }
}

