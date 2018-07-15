package com.ruslaniusupov.achievity.adapter;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.model.Goal;


public class FirestoreGoalsAdapter extends FirestoreRecyclerAdapter<Goal, GoalsViewHolder> {

    private static final String TAG = FirestoreGoalsAdapter.class.getSimpleName();

    private final OnGoalSelectedListener mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreGoalsAdapter(@NonNull FirestoreRecyclerOptions<Goal> options, OnGoalSelectedListener listener) {
        super(options);
        mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull GoalsViewHolder holder, int position, @NonNull Goal model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener);
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalsViewHolder(rootView);
    }

}
