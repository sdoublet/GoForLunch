package com.example.goforlunch.controler.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.PlaceDetailActivity;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.PlaceStreams;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final float DEFAULT_ZOOM = 12f;
    private static final String POI_TYPE = "restaurant";
    private static final String API_KEY = BuildConfig.google_maps_api_key;
    private static final String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_CODE = 1234;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.my_progree_bar)
    ProgressBar myProgressBar;


    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private String lat;
    private String lng;
    private Disposable disposable;
    private List<ResultNearbySearch> searchList = new ArrayList<>();

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


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(Objects.requireNonNull(getContext()));
        displayCurrentLocation(googleMap);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }


    @SuppressLint("MissingPermission")
    private void displayCurrentLocation(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), perms)) {
            mGoogleMap.setMyLocationEnabled(true);
            Task locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    Location currentLocation = (Location) task.getResult();
                    assert currentLocation != null;
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerOptions.title("My position");
                    marker = mGoogleMap.addMarker(markerOptions);
                    httpRequestWithRetrofit((currentLocation.getLatitude()) + "," + (currentLocation.getLongitude()));
                    lat = String.valueOf(currentLocation.getLatitude());
                    lng = String.valueOf(currentLocation.getLongitude());
                    DataHolder.getInstance().setCurrentLat(currentLocation.getLatitude());
                    DataHolder.getInstance().setCurrentLng(currentLocation.getLongitude());
                    DataHolder.getInstance().setCurrentPosiiton(lat + "," + lng);

                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.e(TAG, "Exception: %s", task.getException());
                    Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_LONG).show();
                    LatLng mDefaultLocation = new LatLng(-34, 151);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, "You must give your permission", REQUEST_CODE, perms);
        }
    }



    //--------------------------------
    //HTTP REQUEST WITH RETROFIT
    //--------------------------------
    private void httpRequestWithRetrofit(String location) {


        int rad = Integer.parseInt(DataHolder.getInstance().getRadius());
        this.disposable = PlaceStreams.streamFetchNearbySearch(location, rad, POI_TYPE, API_KEY).subscribeWith(new DisposableObserver<NearbyPlaces>() {
            @Override
            public void onNext(NearbyPlaces nearbyPlaces) {
                displayMarker(nearbyPlaces.getResults());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        {

        }

    }

    private void displayMarker(List<ResultNearbySearch> resultNearbySearches) {
        this.searchList.addAll(resultNearbySearches);
        mGoogleMap.setOnMarkerClickListener(this);
        if (searchList.size() != 0) {

            for (int i = 0; i < searchList.size(); i++) {
                if (searchList.get(i) != null) {

                    int finalI = i;

                    UserHelper.getRestoId(searchList.get(i).getPlaceId()).addOnCompleteListener(task -> {
                        myProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(searchList.get(finalI).getGeometry().getLocation().getLat(),
                                            searchList.get(finalI).getGeometry().getLocation().getLng()))
                                    .title(searchList.get(finalI).getName()));
                            marker.setTag(searchList.get(finalI).getPlaceId());
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker));

                            } else {
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker));

                            }

                        }
                    });

                } else {
                    Log.e("Marker", "search list is null!");
                }

            }
        }
    }


    //  Dispose subscription
    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

// Launch PaceDetailActivity with click on marker
    @Override
    public boolean onMarkerClick(Marker marker) {

        String ref = (String) marker.getTag();
        if (ref != null) {
            Intent intent = new Intent(MapFragment.this.getActivity(), PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.PLACEDETAILRESTO, ref);
            startActivity(intent);
        }
        return false;


    }


    @Override

    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mGoogleMap != null) {
            this.displayCurrentLocation(mGoogleMap);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        this.disposeWhenDestroy();
    }
}