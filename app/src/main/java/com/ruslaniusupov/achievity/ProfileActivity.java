package com.ruslaniusupov.achievity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ruslaniusupov.achievity.adapter.GoalsAdapter;
import com.ruslaniusupov.achievity.model.Goal;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements GoalsAdapter.OnGoalSelectedListener {

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private GoalsAdapter mAdapter;

    @BindView(R.id.name)TextView mUserNameTv;
    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        Query query = mDb.collection("goals")
                .orderBy("timestamp", Query.Direction.DESCENDING);
        mAdapter = new GoalsAdapter(query, this);
        mGoalsRv.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUi(mAuth.getCurrentUser());

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.d(LOG_TAG, "Logged in as: " + mAuth.getCurrentUser().getEmail());
                updateUi(mAuth.getCurrentUser());
            } else {

            }
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

/*    public void addGoal() {

        String text = mGoalEt.getText().toString();
        Goal goal = new Goal(mAuth.getCurrentUser(), text);
        mDb.collection("goals").document().set(goal);

    }*/

    private void updateUi(FirebaseUser firebaseUser) {

        if (firebaseUser == null) {

            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

        } else {

            mUserNameTv.setText(firebaseUser.getDisplayName());

        }

    }

    private void signOut() {
        AuthUI.getInstance().signOut(this);
        updateUi(mAuth.getCurrentUser());
    }

    private void editProfile() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    @Override
    public void onGoalSelected(DocumentSnapshot goal) {

    }
}
