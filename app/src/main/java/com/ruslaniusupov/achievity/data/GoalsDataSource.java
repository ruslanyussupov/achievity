package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.models.Goal;

import java.util.List;

public interface GoalsDataSource {

    interface LoadGoalsCallback {

        void onGoalsLoaded(List<Goal> goals);

        void onDataNotAvailable(Exception e);

    }

    interface GetGoalCallback {

        void onGoalLoaded(Goal goal);

        void onDataNotAvailable(Exception e);

    }

    interface GoalSavedCallback {

        void onGoalSaved();

        void onGoalSavedError(Exception e);

    }

    interface GoalDeletedCallback {

        void onGoalDeleted();

        void onGoalDeletedError(Exception e);

    }

    interface GetUsersLikedCallback {

        void onUsersLikedLoaded(List<String> userIds);

        void onDataNotAvailable(Exception e);

    }

    interface StatusCallback {

        void onSuccess();

        void onError(Exception e);

    }

    interface GetSubscribersCallback {

        void onSubscribersLoaded(List<String> userIds);

        void onDataNotAvailable(Exception e);

    }

    interface GoalChangedListener {

        void onAdded(int oldIndex, int newIndex, Goal goal);

        void onModified(int oldIndex, int newIndex, Goal goal);

        void onRemoved(int oldIndex, int newIndex, Goal goal);

    }
    void getGoals(@NonNull LoadGoalsCallback callback, @NonNull GoalChangedListener listener);

    void getGoals(@NonNull String userId, @NonNull LoadGoalsCallback callback, @NonNull GoalChangedListener listener);

    void getGoal(@NonNull String goalId, @NonNull GetGoalCallback callback);

    void saveGoal(@NonNull Goal goal, @NonNull GoalSavedCallback callback);

    void deleteGoal(@NonNull String goalId, @NonNull GoalDeletedCallback callback);

    void getUsersLiked(@NonNull String goalId, @NonNull GetUsersLikedCallback callback);

    void addUserLiked(@NonNull String goalId, @NonNull String userId, @NonNull StatusCallback callback);

    void deleteUserLiked(@NonNull String goalId, @NonNull String userId);

    void getSubscribers(@NonNull String goalId, @NonNull GetSubscribersCallback callback);

    void addSubscriber(@NonNull String goalId, @NonNull String userId, @NonNull StatusCallback callback);

    void deleteSubscriber(@NonNull String goalId, @NonNull String userId, @NonNull StatusCallback callback);

    void stopListeningGoals();

}
