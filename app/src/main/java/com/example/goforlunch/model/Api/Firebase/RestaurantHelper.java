package com.example.goforlunch.model.Api.Firebase;

import com.example.goforlunch.model.Booking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHelper {
    public static final String COLLECTION_NAME = "booking";


    //--- COLLECTION REFERENCE ---
    public static CollectionReference getBookingRestaurantCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //--- CREATE ---
    public static Task<Void> createBookingRestaurant(String userId, String restaurantId, String restaurantName, String date){//perhaps add boolean like
        Booking bookingToCreate = new Booking(userId, restaurantId, restaurantName, date);
        return RestaurantHelper.getBookingRestaurantCollection().document(userId).set(bookingToCreate);
    }

    //--- GET ---
    public static Task<DocumentSnapshot> getBooking(String userId, String restaurantId){
        return RestaurantHelper.getBookingRestaurantCollection().document(restaurantId).get();
    }

    //--- DELETE ---
    public static Task<Void> deleteBooking(String userId){
        return RestaurantHelper.getBookingRestaurantCollection().document(userId).delete();
    }
}
