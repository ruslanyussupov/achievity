package com.ruslaniusupov.achievity.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {

    private static final String LOG_TAG = FirestoreAdapter.class.getSimpleName();

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();

    public FirestoreAdapter(Query query) {
        mQuery = query;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(LOG_TAG, "onEvent:error", e);
            return;
        }

        for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
            switch (documentChange.getType()) {
                case ADDED:
                    onDocumentAdded(documentChange);
                    break;
                case MODIFIED:
                    onDocumentModified(documentChange);
                    break;
                case REMOVED:
                    onDocumentRemoved(documentChange);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
    }

    public void setQuery(Query query) {
        stopListening();

        mSnapshots.clear();
        notifyDataSetChanged();

        mQuery = query;
        startListening();
    }

    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    protected void onDocumentAdded(DocumentChange documentChange) {
        mSnapshots.add(documentChange.getNewIndex(), documentChange.getDocument());
        notifyItemInserted(documentChange.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange documentChange) {
        if (documentChange.getOldIndex() == documentChange.getNewIndex()) {
            mSnapshots.set(documentChange.getOldIndex(), documentChange.getDocument());
            notifyItemChanged(documentChange.getOldIndex());
        } else {
            mSnapshots.remove(documentChange.getOldIndex());
            mSnapshots.add(documentChange.getNewIndex(), documentChange.getDocument());
            notifyItemMoved(documentChange.getOldIndex(), documentChange.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange documentChange) {
        mSnapshots.remove(documentChange.getOldIndex());
        notifyItemRemoved(documentChange.getOldIndex());
    }

}
