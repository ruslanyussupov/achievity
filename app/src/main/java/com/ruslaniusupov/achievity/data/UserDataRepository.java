package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.data.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserDataRepository implements UserDataDataSource {

    private static final CollectionReference USER_DATA_COLLECTION =
            FirebaseFirestore.getInstance().collection("user_data");

    private static final String SUBSCRIPTIONS_COLLECTION = "subscriptions";
    private static final String GOALS_LIKED_COLLECTION = "goals_liked";
    private static final String NOTES_LIKED_COLLECTION = "notes_liked";
    private static final String FIELD_GOAL_ID = "goalId";
    private static final String FIELD_NOTE_ID = "noteId";

    @Override
    public void getUserProfile(@NonNull String userId, @NonNull final GetUserProfileCallback callback) {
        USER_DATA_COLLECTION.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onUserProfileLoaded(task.getResult().toObject(User.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void getCurrentUser(@NonNull final GetUserProfileCallback callback) {
        USER_DATA_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onUserProfileLoaded(task.getResult().toObject(User.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void saveUserProfile(@NonNull User user, @NonNull final SaveUserProfileCallback callback) {
        USER_DATA_COLLECTION.document(user.getUserId()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onUserDataSaved();
                        } else {
                            callback.onUserProfileSaveError(task.getException());
                        }
                    }
                });
    }

    @Override
    public void deleteUserProfile(@NonNull String userId, @NonNull final DeleteUserProfileCallback callback) {
        USER_DATA_COLLECTION.document(userId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onUserProfileDeleted();
                } else {
                    callback.onUserProfileDeleteError(task.getException());
                }
            }
        });
    }

    @Override
    public void getSubscriptions(@NonNull String userId, @NonNull final GetSubscriptionsCallback callback) {
        USER_DATA_COLLECTION.document(userId).collection(SUBSCRIPTIONS_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> subscriptionsIds = new ArrayList<>();
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                subscriptionsIds.add(snapshot.getString(FIELD_GOAL_ID));
                            }
                            callback.onSubscriptionsLoaded(subscriptionsIds);
                        } else {
                            callback.onDataNotAvailable(task.getException());
                        }
                    }
                });
    }

    @Override
    public void addSubscription(@NonNull String goalId, @NonNull String userId, @NonNull final AddSubscriptionCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_GOAL_ID, goalId);
        USER_DATA_COLLECTION.document(userId).collection(SUBSCRIPTIONS_COLLECTION).document()
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSubscriptionAdded();
                } else {
                    callback.onSubscriptionAddError(task.getException());
                }
            }
        });
    }

    @Override
    public void deleteSubscription(@NonNull String userId, @NonNull String goalId) {
        USER_DATA_COLLECTION.document(userId).collection(SUBSCRIPTIONS_COLLECTION)
                .whereEqualTo(FIELD_GOAL_ID, goalId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                snapshot.getReference().delete();
                            }
                        }
                    }
                });
    }

    @Override
    public void getGoalsLiked(@NonNull String userId, @NonNull final GetGoalsLikedCallback callback) {
        USER_DATA_COLLECTION.document(userId).collection(GOALS_LIKED_COLLECTION)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> goalsLikedIds = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        goalsLikedIds.add(snapshot.getString(FIELD_GOAL_ID));
                    }
                    callback.onGoalsLikedLoaded(goalsLikedIds);
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void addGoalLiked(@NonNull String goalId, @NonNull String userId, @NonNull final AddGoalLikedCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_GOAL_ID, goalId);
        USER_DATA_COLLECTION.document(userId).collection(GOALS_LIKED_COLLECTION).document()
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onGoalLikedAdded();
                } else {
                    callback.onGoalLikedAddError(task.getException());
                }
            }
        });
    }

    @Override
    public void deleteGoalLike(@NonNull String userId, @NonNull String goalId) {
        USER_DATA_COLLECTION.document(userId).collection(GOALS_LIKED_COLLECTION)
                .whereEqualTo(FIELD_GOAL_ID, goalId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        snapshot.getReference().delete();
                    }
                }
            }
        });
    }

    @Override
    public void getNotesLiked(@NonNull String userId, @NonNull final GetNotesLikedCallback callback) {
        USER_DATA_COLLECTION.document(userId).collection(NOTES_LIKED_COLLECTION)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> notesLikedIds = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        notesLikedIds.add(snapshot.getString(FIELD_NOTE_ID));
                    }
                    callback.onNoteLikedCallback(notesLikedIds);
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void addNoteLiked(@NonNull String noteId, @NonNull String userId, @NonNull final AddNoteLikedCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_NOTE_ID, noteId);
        USER_DATA_COLLECTION.document(userId).collection(NOTES_LIKED_COLLECTION).document()
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onNoteLikedAdded();
                } else {
                    callback.onNoteLikedAddError(task.getException());
                }
            }
        });
    }

    @Override
    public void deleteNoteLike(@NonNull String userId, @NonNull String noteId) {
        USER_DATA_COLLECTION.document(userId).collection(NOTES_LIKED_COLLECTION)
                .whereEqualTo(FIELD_NOTE_ID, noteId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        snapshot.getReference().delete();
                    }
                }
            }
        });
    }
}
