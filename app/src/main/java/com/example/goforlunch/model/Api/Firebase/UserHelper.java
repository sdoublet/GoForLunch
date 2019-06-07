package com.example.goforlunch.model.Api.Firebase;

import android.util.Log;

import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME= "users";

    //---COLLECTION REFERENCE---

    public  static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //---CREATE---
    public static Task<Void> createUser(String uid, String username, String urlPicture, String email){
        User userToCreate = new User(uid, username, urlPicture, email);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    //---GET---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
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

    //---DELETE---
    public static Task<Void> deleteUser(String uid){
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
