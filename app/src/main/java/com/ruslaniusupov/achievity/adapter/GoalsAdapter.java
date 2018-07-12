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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ruslaniusupov.achievity.Count;
import com.ruslaniusupov.achievity.FirestoreHelper;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.model.Goal;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsAdapter extends FirestoreRecyclerAdapter<Goal, GoalsAdapter.ViewHolder> {

    private static final String TAG = GoalsAdapter.class.getSimpleName();

    public interface OnGoalSelectedListener {
        void onGoalSelected(DocumentSnapshot goal);
    }

    private final GoalsAdapter.OnGoalSelectedListener mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GoalsAdapter(@NonNull FirestoreRecyclerOptions<Goal> options, OnGoalSelectedListener listener) {
        super(options);
        mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Goal model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(rootView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseAuth mAuth;
        private Context mContext;
        private SharedPreferences mPref;
        private boolean mIsLiked;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPubDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;
        @BindView(R.id.likes_counter)TextView mLikesCounterTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            mAuth = FirebaseAuth.getInstance();
            mPref = mContext.getSharedPreferences(mContext.getString(R.string.pref_goals_liked),
                    Context.MODE_PRIVATE);
        }

        void bind(final DocumentSnapshot documentSnapshot,
                  final GoalsAdapter.OnGoalSelectedListener listener) {

            Goal goal = documentSnapshot.toObject(Goal.class);
            final DocumentReference docRef = documentSnapshot.getReference();
            final String docId = docRef.getId();

            mAuthorTv.setText(goal.getAuthor());
            Date pubDate = goal.getTimestamp();
            mPubDateTv.setText(pubDate.toString());
            mBodyTv.setText(goal.getText());

            // Update likes counter TextView
            Count.getCount(FirestoreHelper.getLikeCounter(docRef)).addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(final Integer likes) {
                    Count.getCount(FirestoreHelper.getUnlikeCounter(docRef)).addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer unlikes) {
                            mLikesCounterTv.setText(String.valueOf(likes - unlikes));
                        }
                    });
                }
            });

            // Update fav button state
            if (mPref.contains(docId)) {
                mIsLiked = true;
                mFavBtn.setSelected(true);
            } else {
                mIsLiked = false;
                mFavBtn.setSelected(false);
            }

            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mIsLiked) {

                        mFavBtn.setSelected(false);

                        // Delete Like from goals/{goalId}/likes/
                        FirestoreHelper.deleteLike(FirestoreHelper.getLikesReference(docId),
                                mAuth.getCurrentUser());

                        // Delete Goal's ID from userData/{userId}/goalsLiked/
                        FirestoreHelper.deleteItemFromLikeList(FirestoreHelper.getGoalsLiked(mAuth.getCurrentUser()),
                                docId, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Delete liked goal from local user data
                                        mPref.edit().remove(docId).apply();
                                    }
                                });

                        // Update likes counter TextView
                        mLikesCounterTv.setText(String.valueOf(
                                Integer.valueOf(mLikesCounterTv.getText().toString()) - 1));

                        FirestoreHelper.incrementUnlikeCounter(docRef, 10);

                    } else {

                        mFavBtn.setSelected(true);

                        // Add Like to goals/{goalId}/likes/
                        FirestoreHelper.addLike(FirestoreHelper.getLikesReference(docId),
                                mAuth.getCurrentUser());

                        // Add Goal's ID to userData/{userId}/goalsLiked/
                        FirestoreHelper.addItemToLikeList(FirestoreHelper.getGoalsLiked(mAuth.getCurrentUser()),
                                docId, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mPref.edit().putBoolean(docId, true).apply();
                                    }
                                });


                        // Update likes counter TextView
                        mLikesCounterTv.setText(String.valueOf(
                                Integer.valueOf(mLikesCounterTv.getText().toString()) + 1));

                        FirestoreHelper.incrementLikeCounter(docRef, 10);

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

    }

}
