package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

public interface UserDataSource {

    String getName();

    void saveName(@NonNull String newName);

    String getUserId();

    String getEmail();

    boolean isAuthorized();

}
