package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import java.util.List;

public interface GoalsLikedPrefsDataSource {

    void add(@NonNull String goalId);

    void delete(@NonNull String goalId);

    boolean contains(@NonNull String goalId);

    void batchAdd(List<String> goalIds);

}
