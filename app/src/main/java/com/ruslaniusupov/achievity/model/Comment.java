package com.ruslaniusupov.achievity.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {

    private String userId;
    private String postId;
    private @ServerTimestamp Date timestamp;
    private String author;
    private String text;

    public Comment() {}

    public Comment(FirebaseUser firebaseUser, DocumentSnapshot documentSnapshot, String text) {
        this.userId = firebaseUser.getUid();
        this.author = firebaseUser.getDisplayName();
        this.postId = documentSnapshot.getId();
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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
