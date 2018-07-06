package com.ruslaniusupov.achievity.model;

import com.google.firebase.auth.FirebaseUser;

public class Like {

    public static final String LIKES = "likes";
    public static final String USER_ID = "userId";

    private String userId;

    public Like(FirebaseUser firebaseUser) {
        this.userId = firebaseUser.getUid();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
