package com.example.goforlunch.model.Api.Firebase;

import com.google.firebase.firestore.Query;

public class MessageHelper {

    public static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllMessageForChat(String chat){
        return ChatHelper.getChatCollection()
                .document()
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(100);
    }
}
