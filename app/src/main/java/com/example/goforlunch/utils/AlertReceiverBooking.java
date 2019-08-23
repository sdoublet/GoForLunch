package com.example.goforlunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.goforlunch.controler.activities.PlaceDetailActivity;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;


public class AlertReceiverBooking extends BroadcastReceiver {

    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    private User user;
    private String currentUser = getCurrentUser().getUid();
    //--------------
    //DELETE BOOKING
    //--------------
    @Override
    public void onReceive(Context context, Intent intent) {


        UserHelper.getUser(currentUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                UserHelper.updateRestaurantId(currentUser, null);
                UserHelper.updateRestaurantName(currentUser, null);
            }
        });
        Log.e("alarm", "alarm receive");

    }

}
