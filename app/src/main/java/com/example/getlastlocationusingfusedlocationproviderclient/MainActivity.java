package com.example.getlastlocationusingfusedlocationproviderclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION_PERMISSION =1 ;
    Button LastLocation;
    TextView mLocationTextView;
    Location mLastLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationTextView=findViewById(R.id.textView);
        LastLocation=findViewById(R.id.button);


        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getApplicationContext());

        LastLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Toast.makeText(getApplicationContext(),"Permission Granted!! ",Toast.LENGTH_LONG).show();

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null)
                    {
                        mLastLocation = location;
                        mLocationTextView.setText(
                                getString(R.string.location_text,
                                        mLastLocation.getLatitude(),
                                        mLastLocation.getLongitude(),
                                        mLastLocation.getTime()));
                    }else
                    {
                        mLocationTextView.setText("Location Not available Turn on/off gps or open GoogleMaps");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Permission denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
