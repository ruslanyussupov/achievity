package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

public interface DefPrefsDataSource {

    void putBoolean(@NonNull String key, boolean value);

    void delete(@NonNull String key);

    boolean contains(@NonNull String key);

    boolean getBoolean(String key, boolean defVal);

}
