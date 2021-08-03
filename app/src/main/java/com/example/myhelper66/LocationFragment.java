package com.example.myhelper66;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.security.PrivateKey;
import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationFragment extends Fragment {

    private LatLng latLng;
    private String latitude;
    private String longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private Context mContext;
    private Activity mActivity;
    private View view;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SHARED_LONG = "longitude";
    private static final String SHARED_LAT = "latitude";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        load_location_data();


// TODO: 04/07/2021 when press on location navbar check if has persmssion to location


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        startLocationUpdates();







        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, supportMapFragment).commit();
        }



        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
            double latitudeValue = Double.parseDouble(latitude);
            double longitudeValue= Double.parseDouble(longitude);
            latLng = new LatLng(latitudeValue,longitudeValue);

            //When map is loaded
                //Initialize marker options
                MarkerOptions markerOptions = new MarkerOptions();
                //Set postion of marker
                markerOptions.position(latLng);
                //Set title of marker
                markerOptions.title((latLng.latitude + " : " + latLng.longitude));
                //Remove all marker
                googleMap.clear();
                //Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
                //Add marker on map
                googleMap.addMarker(markerOptions);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                    }
                });
            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }


    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        //location
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 60 * 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 2000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                    }
                },
                Looper.myLooper());
    }

    public void load_location_data() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE );

        longitude = sharedPreferences.getString(SHARED_LONG, "0");
        latitude = sharedPreferences.getString(SHARED_LAT, "0");
        Toast.makeText(mContext, "http://maps.google.com/?q=" + latitude + "," + longitude, Toast.LENGTH_SHORT).show();
    }

    //END Location function





    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }
}
