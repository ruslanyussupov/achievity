package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.data.models.Note;

public class NotesRepository implements NotesDataSource {

    private final CollectionReference mNotesCollection;

    public NotesRepository(String goalId) {
        mNotesCollection =  FirebaseFirestore.getInstance().collection("goals")
                .document(goalId).collection("notes");
    }

    @Override
    public void getNotes(@NonNull final LoadNotesCallback callback) {
        mNotesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onNotesLoaded(task.getResult().toObjects(Note.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void getNote(@NonNull String noteId, @NonNull final GetNoteCallback callback) {
        mNotesCollection.document(noteId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onNoteLoaded(task.getResult().toObject(Note.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void saveNote(@NonNull Note note) {
        mNotesCollection.document().set(note);
    }

    @Override
    public void deleteNote(@NonNull String noteId) {
        mNotesCollection.document(noteId).delete();
    }
}
