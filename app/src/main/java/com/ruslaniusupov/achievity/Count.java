package com.ruslaniusupov.achievity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.ruslaniusupov.achievity.data.models.Counter;
import com.ruslaniusupov.achievity.data.models.Shard;

import java.util.ArrayList;
import java.util.List;

// solution from https://cloud.google.com/firestore/docs/solutions/counters
public class Count {

    private static final String LOG_TAG = Count.class.getSimpleName();
    private static final String SHARDS = "shards";
    public static final String COUNTERS = "counters";

    public static Task<Void> createCounter(final DocumentReference ref, final int numShards) {
        return ref.set(new Counter(numShards))
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        List<Task<Void>> tasks = new ArrayList<>();

                        for (int i = 0; i < numShards; i++) {
                            Task<Void> makeShard = ref.collection(SHARDS)
                                    .document(String.valueOf(i))
                                    .set(new Shard(0));

                            tasks.add(makeShard);
                        }

                        return Tasks.whenAll(tasks);

                    }
                });
    }

    public static Task<Void> incrementCounter(final DocumentReference ref, int numShards) {
        int shardId = (int) Math.floor(Math.random() * numShards);
        final DocumentReference shardRef = ref.collection(SHARDS).document(String.valueOf(shardId));
        return FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Shard shard = transaction.get(shardRef).toObject(Shard.class);
                shard.increment();
                transaction.set(shardRef, shard);
                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, "Transaction:error", e);
            }
        });
    }

    public static Task<Integer> getCount(final DocumentReference ref) {
        return ref.collection(SHARDS).get()
                .continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        int count = 0;
                        for (DocumentSnapshot snap : task.getResult()) {
                            Shard shard = snap.toObject(Shard.class);
                            count += shard.getCount();
                        }
                        return count;
                    }
                });
    }

}
