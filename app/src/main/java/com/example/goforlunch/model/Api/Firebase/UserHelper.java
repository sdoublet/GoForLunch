package com.example.goforlunch.model.Api.Firebase;

import android.util.Log;

import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserHelper {

    private static final String COLLECTION_NAME= "users";

    //---COLLECTION REFERENCE---

    public  static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //---CREATE---
    public static Task<Void> createUser(String uid, String username, String urlPicture, String email, String restaurantId, String restaurantName ){
        User userToCreate = new User(uid, username, urlPicture, email, restaurantId, restaurantName);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    //---GET---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getRestoId(String restoId){
        return UserHelper.getUsersCollection().whereEqualTo("mRestaurantId",restoId ).get();
    }

    public static CollectionReference getAllUsers(){
        return UserHelper.getUsersCollection();
    }


    //---UPDATE---
    public static Task<Void> updateUser(String username, String uid){
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }
    public static Task<Void>updateEmail(String email, String uid){
        return UserHelper.getUsersCollection().document(uid).update("email", email);
    }

    public static Task<Void>updateRestaurantId(String uid, String restaurantId){
        return UserHelper.getUsersCollection().document(uid).update("mRestaurantId", restaurantId);
    }
 public static Task<Void>updateRestaurantName(String uid, String restaurantName){
        return UserHelper.getUsersCollection().document(uid).update("mRestaurantName", restaurantName);
    }

    //---DELETE---
    public static Task<Void> deleteUser(String uid){
        return UserHelper.getUsersCollection().document(uid).delete();
    }



}
