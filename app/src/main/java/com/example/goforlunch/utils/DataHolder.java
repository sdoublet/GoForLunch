package com.example.goforlunch.utils;

import java.util.List;

public class DataHolder {

    private String currentPosiiton;
    private double currentLat;
    private double currentLng;
    private String restaurantPosition;
    private int distance;
    private String placeId;
    private String restaurantId;
    private String restoName;
    private String userUid;
    private List<String> stringList;
    private String radius = "10000";
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

    public String getRadius() {
        return radius;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getRestoName() {
        return restoName;
    }

    public void setRestoName(String restoName) {
        this.restoName = restoName;
    }
}
