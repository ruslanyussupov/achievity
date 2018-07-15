package com.ruslaniusupov.achievity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;

public class ProfileViewModel extends ViewModel {

    private final FirestoreDocumentLiveData mLiveData =
            new FirestoreDocumentLiveData(FirestoreHelper.getUserDataReference(FirebaseAuth.getInstance().getCurrentUser()));


    public LiveData<DocumentSnapshot> getLiveData() {
        return mLiveData;
    }

}
