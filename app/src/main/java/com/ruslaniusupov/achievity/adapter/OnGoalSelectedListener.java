package com.ruslaniusupov.achievity.adapter;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnGoalSelectedListener {
    void onGoalSelected(DocumentSnapshot goal);
}
