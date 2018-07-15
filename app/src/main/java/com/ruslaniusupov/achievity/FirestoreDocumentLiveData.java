package com.ruslaniusupov.achievity;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

public class FirestoreDocumentLiveData extends LiveData<DocumentSnapshot> {

    private static final String TAG = "DocumentLiveData";

    private final DocumentReference mDocumentReference;
    private final DocumentEventListener mListener = new DocumentEventListener();
    private ListenerRegistration mListenerRegistration;

    public FirestoreDocumentLiveData(DocumentReference documentReference) {
        mDocumentReference = documentReference;
    }

    @Override
    protected void onActive() {
        mListenerRegistration = mDocumentReference.addSnapshotListener(mListener);
    }

    @Override
    protected void onInactive() {
        mListenerRegistration.remove();
    }

    private class DocumentEventListener implements EventListener<DocumentSnapshot> {

        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            setValue(documentSnapshot);
            if (e != null) {
                Log.e(TAG, "DocumentEventListener:onEvent:error", e);
            }
        }

    }

}
