package com.example.footspa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StoreRegisterActivity extends AppCompatActivity {

    TextInputLayout latitudeEditText, longitudeEditText, countryEditText, localityEditText, addressEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register);

        latitudeEditText = findViewById(R.id.storeLatitudeRegister);
        longitudeEditText = findViewById(R.id.storeLongitudeRegister);
        countryEditText = findViewById(R.id.storeCountryRegister);
        localityEditText = findViewById(R.id.storeLocalityRegister);
        addressEditText = findViewById(R.id.storeAddressRegister);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewRegister);
    }

    public void getAddress(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location!=null){
                    try {
                        Geocoder geocoder = new Geocoder(StoreRegisterActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1
                        );
                        latitudeEditText.getEditText().setText(String.valueOf(addresses.get(0).getLatitude()));
                        longitudeEditText.getEditText().setText(String.valueOf(addresses.get(0).getLongitude()));
                        countryEditText.getEditText().setText(addresses.get(0).getCountryName());
                        localityEditText.getEditText().setText(addresses.get(0).getLocality());
                        addressEditText.getEditText().setText(addresses.get(0).getAddressLine(0));
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(latLng).title("Shop is here");
                                googleMap.getUiSettings().setZoomControlsEnabled(true);
                                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                googleMap.getUiSettings().setCompassEnabled(true);
                                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                                googleMap.addMarker(options);
                            }
                        });
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void goToWelcomePage(View view) {
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }
}