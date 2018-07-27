package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.models.User;

import java.util.List;

public interface UserDataDataSource {

    interface GetUserProfileCallback {

        void onUserProfileLoaded(User user);

        void onDataNotAvailable(Exception e);

    }

    interface SaveUserProfileCallback {

        void onUserDataSaved();

        void onUserProfileSaveError(Exception e);

    }

    interface DeleteUserProfileCallback {

        void onUserProfileDeleted();

        void onUserProfileDeleteError(Exception e);

    }

    interface GetSubscriptionsCallback {

        void onSubscriptionsLoaded(List<String> subscriptionsIds);

        void onDataNotAvailable(Exception e);

    }

    interface AddSubscriptionCallback {

        void onSubscriptionAdded();

        void onSubscriptionAddError(Exception e);

    }

    interface GetGoalsLikedCallback {

        void onGoalsLikedLoaded(List<String> goalsLikedIds);

        void onDataNotAvailable(Exception e);

    }

    interface AddGoalLikedCallback {

        void onGoalLikedAdded();

        void onGoalLikedAddError(Exception e);

    }

    interface GetNotesLikedCallback {

        void onNoteLikedCallback(List<String> notesLikedIds);

        void onDataNotAvailable(Exception e);

    }

    interface AddNoteLikedCallback {

        void onNoteLikedAdded();

        void onNoteLikedAddError(Exception e);

    }

    void getUserProfile(@NonNull String userId, @NonNull GetUserProfileCallback callback);

    void getCurrentUser(@NonNull GetUserProfileCallback callback);

    void saveUserProfile(@NonNull User user, @NonNull SaveUserProfileCallback callback);

    void deleteUserProfile(@NonNull String userId, @NonNull DeleteUserProfileCallback callback);

    void getSubscriptions(@NonNull String userId, @NonNull GetSubscriptionsCallback callback);

    void addSubscription(@NonNull String goalId, @NonNull String userId, @NonNull AddSubscriptionCallback callback);

    void deleteSubscription(@NonNull String userId, @NonNull String goalId);

    void getGoalsLiked(@NonNull String userId, @NonNull GetGoalsLikedCallback callback);

    void addGoalLiked(@NonNull String goalId, @NonNull String userId, @NonNull AddGoalLikedCallback callback);

    void deleteGoalLike(@NonNull String userId, @NonNull String goalId);

    void getNotesLiked(@NonNull String userId, @NonNull GetNotesLikedCallback callback);

    void addNoteLiked(@NonNull String noteId, @NonNull String userId, @NonNull AddNoteLikedCallback callback);

    void deleteNoteLike(@NonNull String userId, @NonNull String noteId);

}
