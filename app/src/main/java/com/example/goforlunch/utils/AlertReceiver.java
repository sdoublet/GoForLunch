package com.example.goforlunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.goforlunch.controler.activities.PlaceDetailActivity;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       PlaceDetailActivity placeDetail = new PlaceDetailActivity();
       placeDetail.updateRestaurantId(null);
       placeDetail.updateRestaurantName(null);
  //          delete();

        Log.e("alarm", "alarm receive");

    }

}
