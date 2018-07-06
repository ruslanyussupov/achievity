package com.ruslaniusupov.achievity.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Map;

public class Goal {

    public static final String GOALS = "goals";

    private String userId;
    private @ServerTimestamp Date timestamp;
    private String author;
    private String text;

    public Goal() {}

    public Goal(FirebaseUser firebaseUser, String text) {
        this.userId = firebaseUser.getUid();
        this.author = firebaseUser.getDisplayName();
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
