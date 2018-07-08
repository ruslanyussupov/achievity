package com.ruslaniusupov.achievity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashMap;
import java.util.Map;

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
        private Context mContext;
        private SharedPreferences mPref;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPubDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            mDb = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mPref = mContext.getSharedPreferences(mContext.getString(R.string.pref_goals_liked),
                    Context.MODE_PRIVATE);
        }

        void bind(final DocumentSnapshot documentSnapshot,
                  final OnGoalSelectedListener listener) {

            Goal goal = documentSnapshot.toObject(Goal.class);
            final String docId = documentSnapshot.getId();
            final DocumentReference docRef = documentSnapshot.getReference();

            mAuthorTv.setText(goal.getAuthor());
            Date pubDate = goal.getTimestamp();
            if (pubDate != null) {
                mPubDateTv.setText(pubDate.toString());
            }
            mBodyTv.setText(goal.getText());

            if (mPref.contains(docId)) {
                mFavBtn.setSelected(true);
            } else {
                mFavBtn.setSelected(false);
            }

            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mPref.contains(docId)) {

                        mFavBtn.setSelected(false);

                        // Delete user from likes list
                        queryLike(docRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (DocumentSnapshot snap : task.getResult().getDocuments()) {
                                    snap.getReference().delete();
                                }
                                mPref.edit().remove(docId).apply();
                            }
                        });

                        mDb.collection("users_data")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("goals_likes")
                                .whereEqualTo("goal_id", docId)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (DocumentSnapshot snap : task.getResult().getDocuments()) {
                                    snap.getReference().delete();
                                }
                            }
                        });

                        incrementUnlikeCounter(docRef);

                    } else {

                        mFavBtn.setSelected(true);

                        // Add user to likes list
                        docRef.collection(Like.LIKES).document()
                                .set(new Like(mAuth.getCurrentUser()))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mPref.edit().putBoolean(docId, true).apply();
                            }
                        });

                        Map<String, Object> likeData = new HashMap<>();
                        likeData.put("goal_id", docId);

                        mDb.collection("users_data")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("goals_likes")
                                .document().set(likeData);

                        incrementLikeCounter(docRef);

                    }

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGoalSelected(documentSnapshot);
                }
            });

        }

        private Query queryLike(DocumentReference documentReference) {
            return documentReference
                    .collection(Like.LIKES)
                    .whereEqualTo(Like.USER_ID,
                            mAuth.getCurrentUser().getUid());
        }

        private void incrementLikeCounter(DocumentReference documentReference) {
            DocumentReference ref = documentReference
                    .collection(Count.COUNTERS)
                    .document("like_counter");
            Count.incrementCounter(ref, 10);
        }

        private void incrementUnlikeCounter(DocumentReference documentReference) {
            DocumentReference ref = documentReference
                    .collection(Count.COUNTERS)
                    .document("unlike_counter");
            Count.incrementCounter(ref, 10);
        }

    }

}
