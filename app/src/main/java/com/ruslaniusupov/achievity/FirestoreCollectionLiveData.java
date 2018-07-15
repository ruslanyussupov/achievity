package com.ruslaniusupov.achievity;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class FirestoreCollectionLiveData extends LiveData<QuerySnapshot> {

    private static final String TAG = "CollectionLiveData";

    private final Query mQuery;
    private final DocumentsEventListener listener = new DocumentsEventListener();
    private ListenerRegistration mListenerRegistration;

    public FirestoreCollectionLiveData(Query query) {
        mQuery = query;
    }

    public FirestoreCollectionLiveData(CollectionReference collectionReference) {
        mQuery = collectionReference;
    }

    @Override
    protected void onActive() {
        mListenerRegistration = mQuery.addSnapshotListener(listener);
    }

    @Override
    protected void onInactive() {
        mListenerRegistration.remove();
    }

    private class DocumentsEventListener implements EventListener<QuerySnapshot> {

        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                setValue(queryDocumentSnapshots);
                if (e != null) {
                    Log.e(TAG, "DocumentsEventListener:onEvent:error", e);
                }
        }

    }

}
