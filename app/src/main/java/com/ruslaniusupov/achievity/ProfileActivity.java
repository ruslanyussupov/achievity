package com.ruslaniusupov.achievity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ruslaniusupov.achievity.adapter.GoalsAdapter;
import com.ruslaniusupov.achievity.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity implements GoalsAdapter.OnGoalSelectedListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private GoalsAdapter mAdapter;

    @BindView(R.id.name)TextView mNameTv;
    @BindView(R.id.bio)TextView mBioTv;
    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.add_goal_fab)FloatingActionButton mAddGoalFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        updateUi(mAuth.getCurrentUser());

        Query query = FirestoreHelper.getGoalsReference()
                .whereEqualTo(Goal.FIELD_USER_ID, mAuth.getCurrentUser().getUid())
                .orderBy(Goal.FIELD_TIMESTAMP, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Goal> options = new FirestoreRecyclerOptions.Builder<Goal>()
                .setQuery(query, Goal.class)
                .build();
        mAdapter = new GoalsAdapter(options, this);
        mGoalsRv.setAdapter(mAdapter);

        mAddGoalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AddGoalActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
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
        FirestoreHelper.getUserDataReference(firebaseUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mBioTv.setText(documentSnapshot.getString(FirestoreHelper.FIELD_BIO));

            }
        });

    }

    private void signOut() {
        AuthUI.getInstance().signOut(this);
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void editProfile() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    @Override
    public void onGoalSelected(DocumentSnapshot goal) {
        Intent startNotesActivity = new Intent(this, NotesActivity.class);
        startNotesActivity.putExtra(NotesActivity.EXTRA_GOAL_ID, goal.getId());
        startActivity(startNotesActivity);
    }
}
