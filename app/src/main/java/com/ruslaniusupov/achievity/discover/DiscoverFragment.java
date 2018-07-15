package com.ruslaniusupov.achievity.discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ruslaniusupov.achievity.NotesActivity;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapter.FirestoreGoalsAdapter;
import com.ruslaniusupov.achievity.adapter.OnGoalSelectedListener;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoverFragment extends Fragment implements OnGoalSelectedListener {

    private static final String TAG = "DiscoverFragment";
    private static final long LIMIT = 50;

    private Context mContext;
    private FirestoreGoalsAdapter mAdapter;

    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;

    public DiscoverFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query query = FirestoreHelper.getGoalsReference()
                .orderBy(Goal.FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .limit(LIMIT);
        FirestoreRecyclerOptions<Goal> options = new FirestoreRecyclerOptions.Builder<Goal>()
                .setQuery(query, Goal.class)
                .build();
        mAdapter = new FirestoreGoalsAdapter(options, this);
        mGoalsRv.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onGoalSelected(DocumentSnapshot goal) {
        Intent startNotesActivity = new Intent(mContext, NotesActivity.class);
        startNotesActivity.putExtra(NotesActivity.EXTRA_GOAL_ID, goal.getId());
        startNotesActivity.putExtra(NotesActivity.EXTRA_USER_ID, goal.getString(Goal.FIELD_USER_ID));
        startActivity(startNotesActivity);
    }
}
