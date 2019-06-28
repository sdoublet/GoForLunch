package com.example.goforlunch.model;

public class User {
    private String mUid;
    private String mUsername;
    private String mUrlPicture;
    private String email;
    private String mRestaurantId;
    private String mRestaurantName;

    public User() {
    }

    public User(String uid, String username, String urlPicture, String email, String restaurantId, String restaurantName) {
        this.mUid = uid;
        this.mUsername = username;
        this.mUrlPicture = urlPicture;
        this.email = email;
        this.mRestaurantId = restaurantId;
        this.mRestaurantName=  restaurantName;
    }


    //------------GETTERS-----------
    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getmEmail() {
        return email;
    }

    public String getmRestaurantId() {
        return mRestaurantId;
    }

    public String getmRestaurantName() {
        return mRestaurantName;
    }

    //------------SETTERS-----------
    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUrlPicture() {
        return mUrlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.mUrlPicture = urlPicture;
    }

    public void setmEmail(String mEmail) {
        this.email = mEmail;
    }

    public void setmRestaurantId(String mRestaurantId) {
        this.mRestaurantId = mRestaurantId;
    }

    public void setmRestaurantName(String mRestaurantName) {
        this.mRestaurantName = mRestaurantName;
    }
}
