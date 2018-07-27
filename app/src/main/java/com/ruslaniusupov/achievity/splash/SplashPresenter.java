package com.ruslaniusupov.achievity.splash;

import android.content.Intent;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.ruslaniusupov.achievity.data.DefPrefsDataSource;
import com.ruslaniusupov.achievity.data.GoalsLikedPrefsDataSource;
import com.ruslaniusupov.achievity.data.SubscriptionsPrefsDataSource;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.UserDataRepository;
import com.ruslaniusupov.achievity.data.UserRepository;

import java.util.Arrays;
import java.util.List;

public class SplashPresenter implements SplashContract.Presenter {

    private static final String TAG = "SplashPresenter";
    private static final String PREF_VAL_FIRST_LAUNCH = "first_launch";

    private final UserRepository mUser;
    private final DefPrefsDataSource mDefPrefs;
    private final GoalsLikedPrefsDataSource mGoalsLikedPrefs;
    private final SubscriptionsPrefsDataSource mSubscriptionsPrefs;
    private final SplashContract.View mView;

    SplashPresenter(SplashContract.View view, DefPrefsDataSource defPrefsRepository,
                           GoalsLikedPrefsDataSource goalsLikedPrefs,
                           SubscriptionsPrefsDataSource subscriptionsPrefs) {
        mUser = new UserRepository();
        mDefPrefs = defPrefsRepository;
        mGoalsLikedPrefs = goalsLikedPrefs;
        mSubscriptionsPrefs = subscriptionsPrefs;
        mView = view;
    }

    @Override
    public void checkAuthStatus() {
        if (mUser.isAuthorized()) {
            mView.authorizationPassed();
        } else {
            createAuthIntent();
        }
    }

    private void createAuthIntent() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();

        mView.initAuthorization(intent);

    }

    private void initUserPrefs() {

        UserDataRepository userDataRepository = new UserDataRepository();
        userDataRepository.getGoalsLiked(mUser.getUserId(), new UserDataDataSource.GetGoalsLikedCallback() {
            @Override
            public void onGoalsLikedLoaded(List<String> goalsLikedIds) {
                mGoalsLikedPrefs.batchAdd(goalsLikedIds);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "UserDataRepository:getGoalsLiked", e);
            }
        });

        userDataRepository.getSubscriptions(mUser.getUserId(), new UserDataDataSource.GetSubscriptionsCallback() {
            @Override
            public void onSubscriptionsLoaded(List<String> subscriptionsIds) {
                mSubscriptionsPrefs.batchAdd(subscriptionsIds);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "UserDataRepository:getSubscriptions", e);
            }
        });

    }

    @Override
    public void authFailed(Intent data) {

    }

    @Override
    public void authSuccess() {
        initFirstSettings();
        mView.authorizationPassed();
    }

    private void initFirstSettings() {
        if (!mDefPrefs.getBoolean(PREF_VAL_FIRST_LAUNCH, false)) {
            mDefPrefs.putBoolean(PREF_VAL_FIRST_LAUNCH, true);
            initUserPrefs();
        }
    }
}
