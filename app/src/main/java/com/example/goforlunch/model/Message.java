package com.example.goforlunch.model;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    private String message;
    private Date dateCreated;
    private User userSender;
    private String urlImage;

    public Message(){}

    public Message(String message, User userSender) {
        this.message = message;
        this.userSender = userSender;
    }

    public Message(String message, User userSender, String urlImage) {
        this.message = message;
        this.userSender = userSender;
        this.urlImage = urlImage;
    }

    //--------GETTERS---------

    public String getMessage() {
        return message;
    }

    @ServerTimestamp // Permet d'indiquer la date de creation de l'objet
    public Date getDateCreated() {
        return dateCreated;
    }

    public User getUserSender() {
        return userSender;
    }

    public String getUrlImage() {
        return urlImage;
    }

    //-------SETTERS---------

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}