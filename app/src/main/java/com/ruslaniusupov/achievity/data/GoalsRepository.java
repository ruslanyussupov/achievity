package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.data.models.Goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GoalsRepository implements GoalsDataSource {

    private static final CollectionReference GOALS_COLLECTION =
            FirebaseFirestore.getInstance().collection("goals");
    private static final String LIKES_COLLECTION = "likes";
    private static final String SUBSCRIBERS_COLLECTION = "subscribers";
    private static final String FIELD_USER_ID = "userId";

    private ListenerRegistration mListenerRegistration;

    @Override
    public void getGoals(@NonNull final LoadGoalsCallback callback, @NonNull final GoalChangedListener listener) {
        mListenerRegistration = GOALS_COLLECTION.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    callback.onDataNotAvailable(e);
                    return;
                }
                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    callback.onGoalsLoaded(documentSnapshots.toObjects(Goal.class));
                    for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                listener.onAdded(dc.getOldIndex(),
                                        dc.getNewIndex(),
                                        dc.getDocument().toObject(Goal.class));
                                break;
                            case MODIFIED:
                                listener.onModified(dc.getOldIndex(),
                                        dc.getNewIndex(),
                                        dc.getDocument().toObject(Goal.class));
                                break;
                            case REMOVED:
                                listener.onRemoved(dc.getOldIndex(),
                                        dc.getNewIndex(),
                                        dc.getDocument().toObject(Goal.class));
                                break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void getGoals(@NonNull String userId, @NonNull final LoadGoalsCallback callback, @NonNull final GoalChangedListener listener) {
        mListenerRegistration = GOALS_COLLECTION.whereEqualTo(Goal.FIELD_USER_ID, userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            callback.onDataNotAvailable(e);
                            return;
                        }
                        if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                            callback.onGoalsLoaded(documentSnapshots.toObjects(Goal.class));
                            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        listener.onAdded(dc.getOldIndex(),
                                                dc.getNewIndex(),
                                                dc.getDocument().toObject(Goal.class));
                                        break;
                                    case MODIFIED:
                                        listener.onModified(dc.getOldIndex(),
                                                dc.getNewIndex(),
                                                dc.getDocument().toObject(Goal.class));
                                        break;
                                    case REMOVED:
                                        listener.onRemoved(dc.getOldIndex(),
                                                dc.getNewIndex(),
                                                dc.getDocument().toObject(Goal.class));
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void getGoal(@NonNull String goalId, @NonNull final GetGoalCallback callback) {
        GOALS_COLLECTION.document(goalId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onGoalLoaded(task.getResult().toObject(Goal.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void saveGoal(@NonNull Goal goal, @NonNull final GoalSavedCallback callback) {
        DocumentReference docRef = GOALS_COLLECTION.document();
        goal.setId(docRef.getId());
        docRef.set(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onGoalSaved();
                } else {
                    callback.onGoalSavedError(task.getException());
                }
            }
        });
    }


    @Override
    public void deleteGoal(@NonNull String goalId, @NonNull final GoalDeletedCallback callback) {
        GOALS_COLLECTION.document(goalId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onGoalDeleted();
                } else {
                    callback.onGoalDeletedError(task.getException());
                }
            }
        });
    }

    @Override
    public void getUsersLiked(@NonNull String goalId, @NonNull final GetUsersLikedCallback callback) {
        GOALS_COLLECTION.document(goalId).collection(LIKES_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> userIds = new ArrayList<>();
                            for (DocumentSnapshot snap : task.getResult()) {
                                userIds.add(snap.getString(FIELD_USER_ID));
                            }
                            callback.onUsersLikedLoaded(userIds);
                        } else {
                            callback.onDataNotAvailable(task.getException());
                        }
                    }
                });
    }

    @Override
    public void addUserLiked(@NonNull String goalId, @NonNull String userId, @NonNull final StatusCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_USER_ID, userId);
        GOALS_COLLECTION.document(goalId).collection(LIKES_COLLECTION).document()
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(task.getException());
                }
            }
        });
    }

    @Override
    public void deleteUserLiked(@NonNull String goalId, @NonNull String userId) {
        GOALS_COLLECTION.document(goalId).collection(LIKES_COLLECTION)
                .whereEqualTo(FIELD_USER_ID, userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snap : task.getResult()) {
                                snap.getReference().delete();
                            }
                        }
                    }
                });
    }

    @Override
    public void getSubscribers(@NonNull String goalId, @NonNull final GetSubscribersCallback callback) {
        GOALS_COLLECTION.document(goalId).collection(SUBSCRIBERS_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> userIds = new ArrayList<>();
                            for (DocumentSnapshot snap : task.getResult()) {
                                userIds.add(snap.getString(FIELD_USER_ID));
                            }
                            callback.onSubscribersLoaded(userIds);
                        } else {
                            callback.onDataNotAvailable(task.getException());
                        }
                    }
                });
    }

    @Override
    public void addSubscriber(@NonNull String goalId, @NonNull String userId, @NonNull final StatusCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_USER_ID, userId);
        GOALS_COLLECTION.document(goalId).collection(SUBSCRIBERS_COLLECTION).document()
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(task.getException());
                }
            }
        });
    }

    @Override
    public void deleteSubscriber(@NonNull String goalId, @NonNull String userId, @NonNull final StatusCallback callback) {
        GOALS_COLLECTION.document(goalId).collection(SUBSCRIBERS_COLLECTION)
                .whereEqualTo(FIELD_USER_ID, userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snap : task.getResult()) {
                        snap.getReference().delete();
                    }
                    callback.onSuccess();
                } else {
                    callback.onError(task.getException());
                }
            }
        });
    }

    @Override
    public void stopListeningGoals() {
        if (mListenerRegistration == null) {
            return;
        }
        mListenerRegistration.remove();
    }

}
