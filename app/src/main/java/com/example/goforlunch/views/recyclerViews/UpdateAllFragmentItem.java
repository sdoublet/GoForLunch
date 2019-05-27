package com.example.goforlunch.views.recyclerViews;

import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.model.Resto;

public class UpdateAllFragmentItem {

    private String string;

    public UpdateAllFragmentItem() {


    }
    String setName (Result resto){
        string = resto.getName();
        return string;
    }
    String address (Result resto){
        string = resto.getAdrAddress();
        return string;
    }
    String kind (Resto resto){
        string = resto.toString();
        return string;
    }

}
