package com.example.goforlunch.utils;

//import com.example.goforlunch.model.Api.Autocomplete.Prediction;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Distance.DistanceMatrix;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceServices {
    @GET("maps/api/place/nearbysearch/json?")
    Observable<NearbyPlaces> getNearbyPlaces(@Query("location") String location,
                                             @Query("radius") int radius,
                                             @Query("type") String type,
                                             @Query("key") String key);

    @GET("maps/api/place/details/json?")
    Observable<PlaceDetail> getPlaceDetails(@Query("placeid") String placeId,
                                            @Query("key") String key);

//    @GET("maps/api/place/autocomplete/json?")
//    Observable<Prediction> getPrediction(@Query("input") String input,
//                                         @Query("location") String location,
//                                         @Query("radius") int radius,
//                                         @Query("key") String key);

    @GET("maps/api/distancematrix/json?")
    Observable<DistanceMatrix> getDistance(@Query("origins") String originLatLng,
                                           @Query("destinations") String destinationLatLng,
                                           @Query("key") String key);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();


}
