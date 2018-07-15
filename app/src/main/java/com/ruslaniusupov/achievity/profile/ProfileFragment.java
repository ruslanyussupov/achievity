package com.ruslaniusupov.achievity.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ruslaniusupov.achievity.AddGoalActivity;
import com.ruslaniusupov.achievity.EditProfileActivity;
import com.ruslaniusupov.achievity.NotesActivity;
import com.ruslaniusupov.achievity.ProfileViewModel;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.SplashActivity;
import com.ruslaniusupov.achievity.adapter.FirestoreGoalsAdapter;
import com.ruslaniusupov.achievity.adapter.OnGoalSelectedListener;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements OnGoalSelectedListener {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private FirestoreGoalsAdapter mAdapter;
    private Context mContext;

    @BindView(R.id.name)TextView mNameTv;
    @BindView(R.id.bio)TextView mBioTv;
    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.add_goal_fab)FloatingActionButton mAddGoalFab;

    public ProfileFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        updateUi(mAuth.getCurrentUser());

        ProfileViewModel viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        LiveData<DocumentSnapshot> liveData = viewModel.getLiveData();
        liveData.observe(this, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
                mBioTv.setText(documentSnapshot.getString(FirestoreHelper.FIELD_BIO));
            }
        });

        Query query = FirestoreHelper.getGoalsReference()
                .whereEqualTo(Goal.FIELD_USER_ID, mAuth.getCurrentUser().getUid())
                .orderBy(Goal.FIELD_TIMESTAMP, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Goal> options = new FirestoreRecyclerOptions.Builder<Goal>()
                .setQuery(query, Goal.class)
                .build();
        mAdapter = new FirestoreGoalsAdapter(options, this);
        mGoalsRv.setAdapter(mAdapter);

        mAddGoalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AddGoalActivity.class));
            }
        });

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_edit_profile:
                editProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateUi(FirebaseUser firebaseUser) {

        mNameTv.setText(firebaseUser.getDisplayName());

    }

    private void signOut() {
        AuthUI.getInstance().signOut(mContext);
        startActivity(new Intent(mContext, SplashActivity.class));
        getActivity().finish();
    }

    private void editProfile() {
        startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    @Override
    public void onGoalSelected(DocumentSnapshot goal) {
        Intent startNotesActivity = new Intent(mContext, NotesActivity.class);
        startNotesActivity.putExtra(NotesActivity.EXTRA_GOAL_ID, goal.getId());
        startActivity(startNotesActivity);
    }

}
