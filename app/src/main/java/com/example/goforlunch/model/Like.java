package com.example.goforlunch.model;

public class Like {
    private String mRestoId;
    private int mLike;

    public Like(String mRestoId, int mLike) {
        this.mRestoId = mRestoId;
        this.mLike = mLike;
    }


    //----------GETTERS AND SETTERS------------
    public String getmRestoId() {
        return mRestoId;
    }

    public void setmRestoId(String mRestoId) {
        this.mRestoId = mRestoId;
    }

    public int getmLike() {
        return mLike;
    }

    public void setmLike(int mLike) {
        this.mLike = mLike;
    }
}
