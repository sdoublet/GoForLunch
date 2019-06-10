package com.example.goforlunch.model.Api.Firebase;

import com.example.goforlunch.model.Message;
import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllMessageForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    //--- CREATE ---
    public static Task<DocumentReference>createMessageForChat(String textMessage, String chat, User userSender){
        //Create the message object
        Message message = new Message(textMessage, userSender);

        //Store Message to Firebase
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }

}
