package com.example.goforlunch.model;

public class Resto {
    private String name;
    private String address;
    private String kind;
    private String openingTime;
    private int distance;
    private int photo;
    private int personSelected;
    private int rating;

    public Resto(String name, String address, String kind, String openingTime, int distance, int photo, int personSelected, int rating) {
        this.name = name;
        this.address = address;
        this.kind = kind;
        this.openingTime = openingTime;
        this.distance = distance;
        this.photo = photo;
        this.personSelected = personSelected;
        this.rating = rating;
    }

    public Resto(String name, String address, String kind, String openingTime, int distance, int personSelected, int rating) {
        this.name = name;
        this.address = address;
        this.kind = kind;
        this.openingTime = openingTime;
        this.distance = distance;
        this.personSelected = personSelected;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getPersonSelected() {
        return personSelected;
    }

    public void setPersonSelected(int personSelected) {
        this.personSelected = personSelected;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
