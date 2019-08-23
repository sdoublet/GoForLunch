package com.example.goforlunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;


public class AlertReceiverBooking extends BroadcastReceiver {

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private String currentUser = getCurrentUser().getUid();

    //--------------
    //DELETE BOOKING
    //--------------
    @Override
    public void onReceive(Context context, Intent intent) {


        UserHelper.getUser(currentUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    UserHelper.updateRestaurantId(currentUser, null);
                    UserHelper.updateRestaurantName(currentUser, null);
                }
            }
        });

    }

}
