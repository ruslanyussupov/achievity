package com.ruslaniusupov.achievity.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.List;

public class SubscriptionsPrefsRepository implements SubscriptionsPrefsDataSource {

    private static final String PREF_NAME = "com.ruslaniusupov.achievity.SUBSCRIPTIONS";

    private final SharedPreferences mPrefs;

    public SubscriptionsPrefsRepository(Context context) {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void add(@NonNull String subscriptionId) {
        mPrefs.edit().putBoolean(subscriptionId, true).apply();
    }

    @Override
    public void delete(@NonNull String subscriptionId) {
        mPrefs.edit().remove(subscriptionId).apply();
    }

    @Override
    public boolean contains(@NonNull String subscriptionId) {
        return mPrefs.contains(subscriptionId);
    }

    @Override
    public void batchAdd(List<String> subscriptionIds) {
        SharedPreferences.Editor editor = mPrefs.edit();
        for (String subscriptionId : subscriptionIds) {
            editor.putBoolean(subscriptionId, true);
        }
        editor.apply();
    }
}
