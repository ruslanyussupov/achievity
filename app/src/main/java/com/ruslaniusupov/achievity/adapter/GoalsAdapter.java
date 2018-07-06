package com.ruslaniusupov.achievity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.Count;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.model.Goal;
import com.ruslaniusupov.achievity.model.Like;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsAdapter extends FirestoreAdapter<GoalsAdapter.ViewHolder> {

    private static final String LOG_TAG = GoalsAdapter.class.getSimpleName();

    public interface OnGoalSelectedListener {
        void onGoalSelected(DocumentSnapshot goal);
    }

    private final OnGoalSelectedListener mListener;

    public GoalsAdapter(Query query, OnGoalSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseFirestore mDb;
        private FirebaseAuth mAuth;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPubDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mDb = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
        }

        void bind(final DocumentSnapshot documentSnapshot,
                  final OnGoalSelectedListener listener) {

            Goal goal = documentSnapshot.toObject(Goal.class);

            mAuthorTv.setText(goal.getAuthor());
            Date pubDate = goal.getTimestamp();
            if (pubDate != null) {
                mPubDateTv.setText(pubDate.toString());
            }
            mBodyTv.setText(goal.getText());

            queryLike(documentSnapshot).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()) {
                        mFavBtn.setSelected(false);
                    } else {
                        mFavBtn.setSelected(true);
                    }
                }
            });

            queryLikeCounter(documentSnapshot).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()) {
                        DocumentReference ref = documentSnapshot.getReference()
                                .collection(Count.COUNTERS)
                                .document("like_counter");
                        Count.createCounter(ref, 10);
                    }
                }
            });

            queryUnlikeCounter(documentSnapshot).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    DocumentReference ref = documentSnapshot.getReference()
                            .collection(Count.COUNTERS)
                            .document("unlike_counter");
                    Count.createCounter(ref, 10);
                }
            });

            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Fav btn clicked");

                    queryLike(documentSnapshot).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                mFavBtn.setSelected(true);
                                documentSnapshot.getReference()
                                        .collection(Like.LIKES)
                                        .document().set(new Like(mAuth.getCurrentUser()));
                                incrementLikeCounter(documentSnapshot);
                                Log.d(LOG_TAG, "Document added");
                            } else {
                                mFavBtn.setSelected(false);
                                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                    snapshot.getReference().delete();
                                    incrementUnlikeCounter(documentSnapshot);
                                    Log.d(LOG_TAG, "Document deleted");
                                }
                            }
                        }
                    });

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGoalSelected(documentSnapshot);
                }
            });

        }

        private Query queryLike(DocumentSnapshot documentSnapshot) {
            return documentSnapshot.getReference()
                    .collection(Like.LIKES)
                    .whereEqualTo(Like.USER_ID,
                            mAuth.getCurrentUser().getUid());
        }

        private Query queryLikeCounter(DocumentSnapshot documentSnapshot) {
            return documentSnapshot.getReference()
                    .collection(Count.COUNTERS);
        }

        private Query queryUnlikeCounter(DocumentSnapshot documentSnapshot) {
            return documentSnapshot.getReference()
                    .collection(Count.COUNTERS);
        }

        private void incrementLikeCounter(DocumentSnapshot documentSnapshot) {
            DocumentReference ref = documentSnapshot.getReference()
                    .collection(Count.COUNTERS)
                    .document("like_counter");
            Count.incrementCounter(ref, 10);
        }

        private void incrementUnlikeCounter(DocumentSnapshot documentSnapshot) {
            DocumentReference ref = documentSnapshot.getReference()
                    .collection(Count.COUNTERS)
                    .document("unlike_counter");
            Count.incrementCounter(ref, 10);
        }

    }

}
