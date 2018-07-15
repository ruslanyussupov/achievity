package com.ruslaniusupov.achievity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ruslaniusupov.achievity.R;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsViewHolder> {

    private static final String TAG = "GoalsAdapter";

    private List<DocumentSnapshot> mSnapshots;
    private OnGoalSelectedListener mListener;

    public GoalsAdapter(List<DocumentSnapshot> snapshots, OnGoalSelectedListener listener) {
        mSnapshots = snapshots;
        mListener = listener;
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsViewHolder holder, int position) {
        holder.bind(mSnapshots.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mSnapshots == null ? 0 : mSnapshots.size();
    }

    public void setData(List<DocumentSnapshot> data) {
        mSnapshots = data;
        Log.d(TAG, "Items in adapter: " + data.size());
        notifyDataSetChanged();
    }

}
