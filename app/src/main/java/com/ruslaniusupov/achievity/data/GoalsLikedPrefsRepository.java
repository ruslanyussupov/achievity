package com.ruslaniusupov.achievity.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.List;


public class GoalsLikedPrefsRepository implements GoalsLikedPrefsDataSource {

    private static final String PREF_NAME = "com.ruslaniusupov.achievity.GOALS_LIKED";
    private final SharedPreferences mLikedGoalsPref;

    public GoalsLikedPrefsRepository(Context context) {
        mLikedGoalsPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void add(@NonNull String goalId) {
        mLikedGoalsPref.edit().putBoolean(goalId, true).apply();
    }

    @Override
    public void delete(@NonNull String goalId) {
        mLikedGoalsPref.edit().remove(goalId).apply();
    }

    @Override
    public boolean contains(@NonNull String goalId) {
        return mLikedGoalsPref.contains(goalId);
    }

    @Override
    public void batchAdd(List<String> goalIds) {
        SharedPreferences.Editor editor = mLikedGoalsPref.edit();
        for (String goalId : goalIds) {
            editor.putBoolean(goalId, true);
        }
        editor.apply();
    }

}
