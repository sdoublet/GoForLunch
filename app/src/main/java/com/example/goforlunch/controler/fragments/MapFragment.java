package com.example.goforlunch.controler.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.PlaceDetailActivity;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.ListResto;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.content.ContentValues.TAG;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String POI_TYPE = "restaurant";
    @BindView(R.id.map_view)
    MapView mapView;

    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private Disposable disposable;
    private boolean mLocationPermissionGranted = false;
    private List<ResultNearbySearch> searchList = new ArrayList<>();
    //private HashMap<String, ResultNearbySearch> markerMap = new HashMap<>();

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
        //its running
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

    private void displayCurrentLocation(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task locationResult = fusedLocationProviderClient.getLastLocation();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
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

            } else {
                Log.d(TAG, "Current location is null. Using defaults.");
                Log.e(TAG, "Exception: %s", task.getException());
                Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_LONG).show();
                LatLng mDefaultLocation = new LatLng(-34, 151);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        // If request is cancelled, the result arrays are empty.
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }

    }

    //--------------------------------
    //HTTP REQUEST WITH RETROFIT
    //--------------------------------
    private void httpRequestWithRetrofit(String location) {

        this.disposable = PlaceStreams.streamFetchNearbySearch(location, 10000, POI_TYPE, BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<NearbyPlaces>() {
            @Override
            public void onNext(NearbyPlaces nearbyPlaces) {
                displayMarker(nearbyPlaces.getResults());

                //getinstance.setresto nerabyplace.getresult

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
                    marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(searchList.get(i).getGeometry().getLocation().getLat(),
                                    searchList.get(i).getGeometry().getLocation().getLng()))
                            .title(searchList.get(i).getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    //marker.setTag(searchList.get(i).getId());
                    marker.setTag(searchList.get(i).getPlaceId());
                     ListResto.getInstance().setPlaceId(searchList.get(i).getPlaceId());
                     ListResto.getInstance().setMyList(Collections.singletonList(searchList.get(i).getPlaceId()));
                    Log.e("list", ListResto.getInstance().getPlaceId());
                    Log.e("single", String.valueOf(ListResto.getInstance().getMyList()));
                    Log.e("search", String.valueOf(searchList.size()));

                }
            }
        } else {
            Log.e("Marker", "search list is null!");//anything
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        String ref = (String) marker.getTag();
        Log.e("nearby", marker.getId());
        Log.e("nearby", marker.getTitle());
        Log.e("nearby", ref);

       Intent intent = new Intent(MapFragment.this.getActivity(), PlaceDetailActivity.class);
       intent.putExtra(PlaceDetailActivity.PLACEDETAILRESTO, ref);
       startActivity(intent);
        return false;


    }
}