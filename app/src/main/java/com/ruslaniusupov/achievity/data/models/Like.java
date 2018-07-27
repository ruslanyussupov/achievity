package com.ruslaniusupov.achievity.data.models;

import com.google.firebase.auth.FirebaseUser;

public class Like {

    public static final String FIELD_USER_ID = "userId";

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
