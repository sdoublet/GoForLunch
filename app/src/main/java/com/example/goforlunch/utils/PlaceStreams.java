package com.example.goforlunch.utils;


import com.example.goforlunch.model.Api.Autocomplete.Prediction;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Distance.DistanceMatrix;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlaceStreams {


    private static  PlaceServices placeservices = PlaceServices.retrofit.create(PlaceServices.class);
    private static PlaceServices placeServicesMatrix = PlaceServices.retrofitDistanceMatrix.create(PlaceServices.class);

    public static Observable<NearbyPlaces> streamFetchNearbySearch(String location, int radius, String type, String key ){
        return placeservices.getNearbyPlaces(location, radius, type, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetail> streamFetchPlaceDetails(String placeId, String key){
        return placeservices.getPlaceDetails(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }



    public static Observable<Prediction> streamFetchPrediction(String input, String location, int radius, String key){
        return placeservices.getPrediction(input, location, radius, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<DistanceMatrix> streamfetchDistanceMatrix(String origins, String destinations, String key){
        return placeservices.getDistance(origins, destinations, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }



}
