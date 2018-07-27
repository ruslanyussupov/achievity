package com.ruslaniusupov.achievity.profile;

import android.util.Log;

import com.ruslaniusupov.achievity.data.GoalsDataSource;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.models.Goal;
import com.ruslaniusupov.achievity.data.models.User;

import java.util.List;

public class ProfilePresenter implements ProfileContract.Presenter {

    private static final String TAG = "ProfilePresenter";

    private final ProfileContract.View mView;
    private final GoalsDataSource mGoals;
    private final UserDataDataSource mUserData;
    private final String mUserId;

    ProfilePresenter(ProfileContract.View view, String userId,
                     GoalsDataSource goalsData, UserDataDataSource userData) {
        mView = view;
        mGoals = goalsData;
        mUserData = userData;
        mUserId = userId;
    }


    @Override
    public void loadGoals() {

        GoalsDataSource.LoadGoalsCallback loadGoalsCallback = new GoalsDataSource.LoadGoalsCallback() {
            @Override
            public void onGoalsLoaded(List<Goal> goals) {
                if (goals == null || goals.isEmpty()) {
                    mView.showNoGoals();
                } else {
                    mView.showGoals(goals);
                }
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Loading goals error", e);
                mView.showLoadingGoalsError();
            }
        };

        GoalsDataSource.GoalChangedListener goalChangedListener = new GoalsDataSource.GoalChangedListener() {
            @Override
            public void onAdded(int oldIndex, int newIndex, Goal goal) {

            }

            @Override
            public void onModified(int oldIndex, int newIndex, Goal goal) {

            }

            @Override
            public void onRemoved(int oldIndex, int newIndex, Goal goal) {

            }
        };

        mGoals.getGoals(mUserId, loadGoalsCallback, goalChangedListener);
    }


    @Override
    public void loadUserData() {
        mUserData.getUserProfile(mUserId, new UserDataDataSource.GetUserProfileCallback() {
            @Override
            public void onUserProfileLoaded(User user) {
                mView.showUserData(user);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Get user data error", e);
            }
        });
    }

    @Override
    public void signOut() {

    }
}
