package com.ruslaniusupov.achievity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.NotesActivity;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.adapter.GoalsAdapter;
import com.ruslaniusupov.achievity.adapter.OnGoalSelectedListener;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.data.models.Goal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements OnGoalSelectedListener {

    private static final String TAG = "HomeFragment";

    private Context mContext;
    private GoalsAdapter mAdapter;
    private List<DocumentSnapshot> mGoalSnapshots;

    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;

    public HomeFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new GoalsAdapter(mGoalSnapshots, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirestoreHelper.getSubscriptionsReference(FirebaseAuth.getInstance().getCurrentUser())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mGoalSnapshots = new ArrayList<>();
                ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    String goalId = snap.getString(FirestoreHelper.FIELD_GOAL_ID);
                    Log.d(TAG, "Goal ID: " + goalId);
                    Task<DocumentSnapshot> task = FirestoreHelper.getGoalReference(goalId).get();
                    task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            mGoalSnapshots.add(documentSnapshot);
                        }
                    });
                    tasks.add(task);
                }
                Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAdapter.setData(mGoalSnapshots);
                    }
                });
            }
        });

        mGoalsRv.setAdapter(mAdapter);

    }

    @Override
    public void onGoalSelected(DocumentSnapshot goal) {
        Intent startNotesActivity = new Intent(mContext, NotesActivity.class);
        startNotesActivity.putExtra(NotesActivity.EXTRA_GOAL_ID, goal.getId());
        startNotesActivity.putExtra(NotesActivity.EXTRA_USER_ID, goal.getString(Goal.FIELD_USER_ID));
        startActivity(startNotesActivity);
    }
}
