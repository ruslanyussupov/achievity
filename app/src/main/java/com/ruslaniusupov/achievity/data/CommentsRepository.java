package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.data.models.Comment;

public class CommentsRepository implements CommentsDataSource {

    private final CollectionReference mCommentsCollection;

    public CommentsRepository(String goalId, String noteId) {
        mCommentsCollection = FirebaseFirestore.getInstance().collection("goals")
                .document(goalId).collection("notes").document(noteId)
                .collection("comments");
    }


    @Override
    public void getComments(@NonNull final LoadCommentsCallback callback) {
        mCommentsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onCommentsLoaded(task.getResult().toObjects(Comment.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void getComment(@NonNull String commentId, @NonNull final GetCommentCallback callback) {
        mCommentsCollection.document(commentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onCommentLoaded(task.getResult().toObject(Comment.class));
                } else {
                    callback.onDataNotAvailable(task.getException());
                }
            }
        });
    }

    @Override
    public void saveComment(@NonNull Comment comment) {
        mCommentsCollection.document().set(comment);
    }

    @Override
    public void deleteComment(@NonNull String commentId) {
        mCommentsCollection.document(commentId).delete();
    }
}
