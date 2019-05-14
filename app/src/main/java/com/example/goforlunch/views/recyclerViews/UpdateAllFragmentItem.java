package com.example.goforlunch.views.recyclerViews;

import com.example.goforlunch.model.Resto;

public class UpdateAllFragmentItem {

    private String string;

    public UpdateAllFragmentItem() {


    }
    String setName (Resto resto){
        string = resto.getName();
        return string;
    }
    String address (Resto resto){
        string = resto.getAddress();
        return string;
    }
    String kind (Resto resto){
        string = resto.toString();
        return string;
    }

}
