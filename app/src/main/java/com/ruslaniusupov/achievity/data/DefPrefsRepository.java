package com.ruslaniusupov.achievity.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class DefPrefsRepository implements DefPrefsDataSource {

    private final SharedPreferences mPrefs;

    public DefPrefsRepository(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void putBoolean(@NonNull String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    @Override
    public void delete(@NonNull String key) {
        mPrefs.edit().remove(key).apply();
    }

    @Override
    public boolean contains(@NonNull String key) {
        return mPrefs.contains(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defVal) {
        return mPrefs.getBoolean(key, defVal);
    }
}
