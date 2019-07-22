package com.example.goforlunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.goforlunch.controler.activities.PlaceDetailActivity;


public class AlertReceiverBooking extends BroadcastReceiver {

    //--------------
    //DELETE BOOKING
    //--------------
    @Override
    public void onReceive(Context context, Intent intent) {

        PlaceDetailActivity placeDetail = new PlaceDetailActivity();
        placeDetail.updateRestaurantId(null);
        placeDetail.updateRestaurantName(null);

        Log.e("alarm", "alarm receive");

    }

}
