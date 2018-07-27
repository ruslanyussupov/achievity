package com.ruslaniusupov.achievity.adapter;

import android.util.Log;

import com.ruslaniusupov.achievity.data.GoalsDataSource;
import com.ruslaniusupov.achievity.data.GoalsLikedPrefsDataSource;
import com.ruslaniusupov.achievity.data.SubscriptionsPrefsDataSource;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.models.User;

public class GoalsViewHolderPresenter implements GoalsViewHolderContract.Presenter {

    private static final String TAG = GoalsViewHolderPresenter.class.getSimpleName();

    private final GoalsViewHolderContract.View mView;
    private final UserDataDataSource mUserData;
    private final GoalsLikedPrefsDataSource mGoalsLikedPrefs;
    private final SubscriptionsPrefsDataSource mSubscriptionsPrefs;
    private final GoalsDataSource mGoals;

    GoalsViewHolderPresenter(GoalsViewHolderContract.View view, UserDataDataSource userData,
                             GoalsLikedPrefsDataSource goalsLikedPrefs,
                             SubscriptionsPrefsDataSource subscriptionsPrefs,
                             GoalsDataSource goals) {
        mView = view;
        mUserData = userData;
        mGoalsLikedPrefs = goalsLikedPrefs;
        mSubscriptionsPrefs = subscriptionsPrefs;
        mGoals = goals;
    }

    @Override
    public void initButtonsState(String goalId) {

        if (mGoalsLikedPrefs.contains(goalId)) {
            mView.setLikeBtnState(true);
        } else {
            mView.setLikeBtnState(false);
        }

        if (mSubscriptionsPrefs.contains(goalId)) {
            mView.setSubscribeBtnState(true);
        } else {
            mView.setSubscribeBtnState(false);
        }

    }

    @Override
    public void like(final String goalId) {
        mUserData.getCurrentUser(new UserDataDataSource.GetUserProfileCallback() {
            @Override
            public void onUserProfileLoaded(final User user) {
                mUserData.addGoalLiked(goalId, user.getUserId(), new UserDataDataSource.AddGoalLikedCallback() {
                    @Override
                    public void onGoalLikedAdded() {
                        mGoalsLikedPrefs.add(goalId);
                    }

                    @Override
                    public void onGoalLikedAddError(Exception e) {
                        Log.e(TAG, "Add liked goal error", e);
                    }
                });
                mGoals.addUserLiked(goalId, user.getUserId(), new GoalsDataSource.StatusCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "User is added to the likes collection");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Can't add user to likes collection", e);
                    }
                });

            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't get current user", e);
            }
        });
    }

    @Override
    public void cancelLike(final String goalId) {
        mUserData.getCurrentUser(new UserDataDataSource.GetUserProfileCallback() {
            @Override
            public void onUserProfileLoaded(User user) {
                mUserData.deleteGoalLike(user.getUserId(), goalId);
                mGoalsLikedPrefs.delete(goalId);
                mGoals.deleteUserLiked(goalId, user.getUserId());
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't get current user", e);
            }
        });
    }

    @Override
    public void subscribe(final String goalId) {

        mUserData.getCurrentUser(new UserDataDataSource.GetUserProfileCallback() {
            @Override
            public void onUserProfileLoaded(User user) {
                mUserData.addSubscription(goalId, user.getUserId(), new UserDataDataSource.AddSubscriptionCallback() {
                    @Override
                    public void onSubscriptionAdded() {

                    }

                    @Override
                    public void onSubscriptionAddError(Exception e) {

                    }
                });
            }

            @Override
            public void onDataNotAvailable(Exception e) {

            }
        });

    }

    @Override
    public void unsubscribe(String goalId) {

    }
}
