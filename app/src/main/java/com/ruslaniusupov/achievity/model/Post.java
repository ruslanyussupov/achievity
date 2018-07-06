package com.ruslaniusupov.achievity.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {

    private String userId;
    private String goalId;
    private @ServerTimestamp Date timestamp;
    private String author;
    private String text;

    public Post() {}

    public Post(FirebaseUser firebaseUser, DocumentSnapshot documentSnapshot, String text) {
        this.userId = firebaseUser.getUid();
        this.author = firebaseUser.getDisplayName();
        this.goalId = documentSnapshot.getId();
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
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
