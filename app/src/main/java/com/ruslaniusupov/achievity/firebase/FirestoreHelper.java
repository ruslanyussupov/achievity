package com.ruslaniusupov.achievity.firebase;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.Count;
import com.ruslaniusupov.achievity.model.Counter;
import com.ruslaniusupov.achievity.model.Like;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    public static final String GOALS_COLLECTION = "goals";
    public static final String USERS_DATA_COLLECTION = "usersData";
    public static final String COUNTERS_COLLECTION = "counters";
    public static final String LIKES_COLLECTION = "likes";
    public static final String SHARDS_COLLECTION = "shards";
    public static final String SUBSCRIBERS_COLLECTION = "subscribers";
    public static final String SUBSCRIPTIONS_COLLECTION = "subscriptions";
    public static final String NOTES_COLLECTION = "notes";
    public static final String GOALS_LIKED = "goalsLiked";
    public static final String FIELD_BIO = "bio";
    public static final String FIELD_GOAL_ID = "goalId";
    public static final String FIELD_USER_ID = "userId";

    public static DocumentReference getGoalReference(String docId) {
        return FirebaseFirestore.getInstance().collection(GOALS_COLLECTION)
                .document(docId);
    }

    public static CollectionReference getGoalsReference() {
        return FirebaseFirestore.getInstance().collection(GOALS_COLLECTION);
    }

    public static DocumentReference getUserDataReference(FirebaseUser firebaseUser) {
        return FirebaseFirestore.getInstance().collection(USERS_DATA_COLLECTION)
                .document(firebaseUser.getUid());
    }

    public static CollectionReference getLikesReference(String docId) {
        return getGoalReference(docId).collection(LIKES_COLLECTION);
    }

    public static CollectionReference getGoalsLiked(FirebaseUser firebaseUser) {
        return getUserDataReference(firebaseUser).collection(GOALS_LIKED);
    }

    public static CollectionReference getNotesReference(String goalId) {
        return getGoalReference(goalId).collection(NOTES_COLLECTION);
    }

    public static CollectionReference getSubscribersReference(DocumentReference docId) {
        return docId.collection(SUBSCRIBERS_COLLECTION);
    }

    public static CollectionReference getSubscriptionsReference(FirebaseUser firebaseUser) {
        return getUserDataReference(firebaseUser).collection(SUBSCRIPTIONS_COLLECTION);
    }

    public static DocumentReference getLikeCounter(DocumentReference documentReference) {
        return documentReference.collection(COUNTERS_COLLECTION).document(Counter.DOC_LIKE_COUNTER);
    }

    public static DocumentReference getUnlikeCounter(DocumentReference documentReference) {
        return documentReference.collection(COUNTERS_COLLECTION).document(Counter.DOC_UNLIKE_COUNTER);
    }

    public static void incrementLikeCounter(DocumentReference documentReference, int numShards) {
        DocumentReference ref = documentReference
                .collection(Count.COUNTERS)
                .document(Counter.DOC_LIKE_COUNTER);
        Count.incrementCounter(ref, numShards);
    }

    public static void incrementUnlikeCounter(DocumentReference documentReference, int numShards) {
        DocumentReference ref = documentReference
                .collection(Count.COUNTERS)
                .document(Counter.DOC_UNLIKE_COUNTER);
        Count.incrementCounter(ref, numShards);
    }

    public static void deleteLike(CollectionReference collection, FirebaseUser user) {
        collection.whereEqualTo(Like.FIELD_USER_ID, user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    snapshot.getReference().delete();
                }
            }
        });
    }

    public static void deleteItemFromLikeList(CollectionReference collection, String docId,
                                             final OnSuccessListener<Void> onSuccessListener) {
        collection.whereEqualTo(FIELD_GOAL_ID, docId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    snapshot.getReference().delete().addOnSuccessListener(onSuccessListener);
                }
            }
        });
    }

    public static void addLike(CollectionReference collection, FirebaseUser user) {
        collection.document().set(new Like(user));
    }

    public static void addItemToLikeList(CollectionReference collection, String docId,
                                         final OnSuccessListener<Void> onSuccessListener) {
        Map<String, Object> likeData = new HashMap<>();
        likeData.put(FirestoreHelper.FIELD_GOAL_ID, docId);
        collection.document().set(likeData).addOnSuccessListener(onSuccessListener);
    }

}
