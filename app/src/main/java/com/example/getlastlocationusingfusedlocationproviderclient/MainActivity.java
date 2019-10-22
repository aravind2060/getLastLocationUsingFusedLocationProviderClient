package com.example.getlastlocationusingfusedlocationproviderclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnTaskCompleted {

    private static final int REQUEST_LOCATION_PERMISSION =1 ;
    Button LastLocation;
    TextView mLocationTextView;
    Location mLastLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap googleMap;
    SupportMapFragment supportMapFragment;
    Location location;
    PlacesClient placesClient;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationTextView=findViewById(R.id.textView);
        LastLocation=findViewById(R.id.button);
        if (!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(),"AIzaSyCYdY6IAHRD1QkxisenY2ik_dN1i0EAops");
        }
        placesClient=Places.createClient(this);

         supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getApplicationContext());

        LastLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Toast.makeText(getApplicationContext(),"Permission Granted!! ",Toast.LENGTH_LONG).show();
            flag=true;
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location locationa) {
                    if (locationa != null) {
                        location = locationa;

                        new FetchAddressAsyncTask(MainActivity.this,
                                MainActivity.this).execute(locationa);
                        initMap();
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
                    flag=true;

                } else {
                    Toast.makeText(this, "Permission denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        mLocationTextView.setText(getString(R.string.address_text,
                result, System.currentTimeMillis()));
    }

    public void initMap()
    {
          supportMapFragment.getMapAsync(new OnMapReadyCallback() {
              @Override
              public void onMapReady(GoogleMap googleMaps) {
                  googleMap = googleMaps;
                  if (flag) {
                      LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                      googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                      googleMap.setMyLocationEnabled(true);
                      googleMap.addMarker(new MarkerOptions().title("Your here").position(latLng));
                  }
              }
          });
    }
}
