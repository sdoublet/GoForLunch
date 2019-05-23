package com.example.goforlunch.model;

public class User {
    private String mUid;
    private String mUsername;
    private String mUrlPicture;
    private String mEmail;

    public User() {
    }

    public User(String uid, String username, String urlPicture, String email) {
        this.mUid = uid;
        this.mUsername = username;
        this.mUrlPicture = urlPicture;
        this.mEmail = email;
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
        return mEmail;
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
        this.mEmail = mEmail;
    }
}
