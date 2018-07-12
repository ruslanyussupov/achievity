package com.ruslaniusupov.achievity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            signIn();
        } else {
            startMainActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Logged in as: " + mAuth.getCurrentUser().getEmail());

                // Init user data if it's first app launch
                SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(this);
                if (!defPref.getBoolean("first_launch", false)) {
                    defPref.edit().putBoolean("first_launch", true).apply();
                    initUserPrefs(mAuth.getCurrentUser());
                }

            } else {
                Log.e(TAG, response.getError().getMessage());
            }
        }

    }

    private void initUserPrefs(FirebaseUser firebaseUser) {

        final SharedPreferences likedGoalsPref =
                getSharedPreferences(getString(R.string.pref_goals_liked), Context.MODE_PRIVATE);

        FirestoreHelper.getGoalsLiked(firebaseUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                SharedPreferences.Editor editor = likedGoalsPref.edit();
                for (DocumentSnapshot snap : documentSnapshots) {
                    String goalId = (String) snap.get(FirestoreHelper.FIELD_GOAL_ID);
                    editor.putBoolean(goalId, true);
                }
                editor.apply();
                startMainActivity();
            }
        });

    }

    private void signIn() {
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
    }

    private void startMainActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }

}
