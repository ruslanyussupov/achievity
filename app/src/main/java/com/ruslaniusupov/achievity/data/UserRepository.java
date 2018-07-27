package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserRepository implements UserDataSource {

    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public String getName() {
        return mUser.getDisplayName();
    }

    @Override
    public void saveName(@NonNull String newName) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        mUser.updateProfile(changeRequest);
    }

    @Override
    public String getUserId() {
        return mUser.getUid();
    }

    @Override
    public String getEmail() {
        return mUser.getEmail();
    }

    @Override
    public boolean isAuthorized() {
        return mUser != null;
    }

}
