package com.example.goforlunch.controler.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.goforlunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final float DEFAULT_ZOOM =15f ;
    @BindView(R.id.map_view)
    MapView mapView;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Place place;
    private boolean mLocationPermissionGranted = false;

    private double latitude;
    private double longitude;

    public static Fragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(Objects.requireNonNull(getContext()));
        displayCurrentLocation(googleMap);
    }

    @SuppressLint("MissingPermission")
    private void displayCurrentLocation(GoogleMap googleMap) {
        @SuppressLint("MissingPermission") Task locationResult = fusedLocationProviderClient.getLastLocation();
        googleMap.setMyLocationEnabled(true);
        locationResult.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    Location currentLocation = (Location) task.getResult();
                    assert currentLocation != null;
                    googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM));

                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.e(TAG, "Exception: %s", task.getException());
                    Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_LONG).show();
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    //googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
        });
    }

    private void getLocationPermission() {
                /*
                 * Request location permission, so that we can get the location of the
                 * device. The result of the permission request is handled by a callback,
                 * onRequestPermissionsResult.
                 */
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.e("permission", " permission ok");
                } else {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode,
                                                   @NonNull String permissions[],
                                                   @NonNull int[] grantResults) {
                mLocationPermissionGranted = false;
                switch (requestCode) {
                    case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = true;
                        }
                    }
                }
                updateLocationUI();
            }

            private void updateLocationUI() {
                if (googleMap == null) {
                    return;
                }
                try {
                    if (mLocationPermissionGranted) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        Log.e("permission", " permission ok");
                    } else {
                        Log.e("permission", " no permission ");
                        googleMap.setMyLocationEnabled(false);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        // mLastKnownLocation = null;
                        getLocationPermission();
                    }
                } catch (SecurityException e) {
                    Log.e("Exception: %s", e.getMessage());
                }
            }




        }
