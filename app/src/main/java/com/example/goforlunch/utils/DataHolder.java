package com.example.goforlunch.utils;

import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;

import java.util.List;

public class DataHolder {

    private String currentPosiiton;
    private String restaurantPosition;
    private int distance;
    private String placeId;
    private List<String> stringList;
    private static final DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private DataHolder() {
    }

    public String getCurrentPosiiton() {
        return currentPosiiton;
    }

    public void setCurrentPosiiton(String currentPosiiton) {
        this.currentPosiiton = currentPosiiton;
    }

    public String getRestaurantPosition() {
        return restaurantPosition;
    }

    public void setRestaurantPosition(String restaurantPosition) {
        this.restaurantPosition = restaurantPosition;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
