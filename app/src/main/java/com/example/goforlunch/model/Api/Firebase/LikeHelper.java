package com.example.goforlunch.model.Api.Firebase;

import com.example.goforlunch.model.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LikeHelper {
    private static final String COLLECTION_NAME = "like";

    //---COLLECTION REFERENCE---
    public static CollectionReference getLikeCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //---CREATE---
    public static Task<Void> createLike(String restoId, int like){
        Like likeToCreate = new Like(restoId, like);
        return LikeHelper.getLikeCollection().document(restoId).set(likeToCreate);
    }

    //---GET---
    public static Task<DocumentSnapshot>getRestoLiked(String restoId){
        return LikeHelper.getLikeCollection().document(restoId).get();
    }


    //---UPDATE---
    public  static Task<Void>updateLike(String restoId, int like){
        return LikeHelper.getLikeCollection().document(restoId).update("mLike", like+1);
    }
}
