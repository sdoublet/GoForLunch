package com.example.goforlunch.utils;

import java.util.ArrayList;
import java.util.List;

public class ListResto {
    private static final ListResto ourInstance = new ListResto();

    public static ListResto getInstance() {
        return ourInstance;
    }
    private List<String> myList = new ArrayList<>();

    public List<String> getMyList() {
        return myList;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }

    private ListResto() {

        //list resto avec get and set

        myList.add(getPlaceId());
    }
    private String placeId;


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
