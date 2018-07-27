package com.ruslaniusupov.achievity.data.models;

public class User {

    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_FULL_NAME = "fullName";
    public static final String FIELD_BIRTH_DATE = "birthDate";
    public static final String FIELD_BIO = "bio";

    private String userId;
    private String fullName;
    private String birthDate;
    private String bio;

    public User() {}

    public User(String userId, String fullName, String birthDate, String bio) {
        this.userId = userId;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.bio = bio;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
