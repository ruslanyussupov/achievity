package com.ruslaniusupov.achievity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.model.Goal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

class GoalsViewHolder extends RecyclerView.ViewHolder {

    private FirebaseUser mUser;
    private Context mContext;
    private SharedPreferences mGoalsLikedPref;
    private SharedPreferences mSubscriptionsPref;
    private boolean mIsLiked;
    private boolean mIsSubscribed;

    @BindView(R.id.author)TextView mAuthorTv;
    @BindView(R.id.publish_date)TextView mPubDateTv;
    @BindView(R.id.body)TextView mBodyTv;
    @BindView(R.id.fav_btn)ImageButton mFavBtn;
    @BindView(R.id.notifications_btn)ImageButton mNotificationsBtn;

    public GoalsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mGoalsLikedPref = mContext.getSharedPreferences(mContext.getString(R.string.pref_goals_liked),
                Context.MODE_PRIVATE);
        mSubscriptionsPref = mContext.getSharedPreferences(mContext.getString(R.string.pref_subscriptions),
                Context.MODE_PRIVATE);
    }

    void bind(final DocumentSnapshot documentSnapshot,
              final OnGoalSelectedListener listener) {

        Goal goal = documentSnapshot.toObject(Goal.class);
        final DocumentReference docRef = documentSnapshot.getReference();
        final String docId = docRef.getId();

        mAuthorTv.setText(goal.getAuthor());
        Date pubDate = goal.getTimestamp();
        mPubDateTv.setText(pubDate.toString());
        mBodyTv.setText(goal.getText());

        // Update fav button state
        if (mGoalsLikedPref.contains(docId)) {
            mIsLiked = true;
            mFavBtn.setSelected(true);
        } else {
            mIsLiked = false;
            mFavBtn.setSelected(false);
        }

        // Update notifications button state
        if (mSubscriptionsPref.contains(docId)) {
            mIsSubscribed = true;
            mNotificationsBtn.setSelected(true);
        } else {
            mIsSubscribed = false;
            mNotificationsBtn.setSelected(false);
        }

        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsLiked) {

                    mFavBtn.setSelected(false);

                    // Delete Like from goals/{goalId}/likes/
                    FirestoreHelper.deleteLike(FirestoreHelper.getLikesReference(docId),
                            mUser);

                    // Delete Goal's ID from userData/{userId}/goalsLiked/
                    FirestoreHelper.deleteItemFromLikeList(FirestoreHelper.getGoalsLiked(mUser),
                            docId, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Delete liked goal from local user data
                                    mGoalsLikedPref.edit().remove(docId).apply();
                                }
                            });

                    FirestoreHelper.incrementUnlikeCounter(docRef, 10);

                } else {

                    mFavBtn.setSelected(true);

                    // Add Like to goals/{goalId}/likes/
                    FirestoreHelper.addLike(FirestoreHelper.getLikesReference(docId), mUser);

                    // Add Goal's ID to userData/{userId}/goalsLiked/
                    FirestoreHelper.addItemToLikeList(FirestoreHelper.getGoalsLiked(mUser),
                            docId, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mGoalsLikedPref.edit().putBoolean(docId, true).apply();
                                }
                            });


                    FirestoreHelper.incrementLikeCounter(docRef, 10);

                }

            }
        });

        mNotificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsSubscribed) {

                    mNotificationsBtn.setSelected(false);

                    // Delete user from subscribers collection
                    FirestoreHelper.getSubscribersReference(docRef)
                            .whereEqualTo(FirestoreHelper.FIELD_USER_ID, mUser.getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                snap.getReference().delete();
                            }
                        }
                    });

                    // Delete goal from subscriptions collection
                    FirestoreHelper.getSubscriptionsReference(mUser)
                            .whereEqualTo(FirestoreHelper.FIELD_GOAL_ID, docId)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            SharedPreferences.Editor prefEditor = mSubscriptionsPref.edit();
                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                snap.getReference().delete();
                                // Remove goal from subscriptions prefs
                                prefEditor.remove(docId);
                            }
                            prefEditor.apply();
                        }
                    });

                } else {

                    mNotificationsBtn.setSelected(true);

                    // Add user to subscribers collection
                    Map<String, Object> subscriber = new HashMap<>();
                    subscriber.put(FirestoreHelper.FIELD_USER_ID, mUser.getUid());
                    FirestoreHelper.getSubscribersReference(docRef)
                            .document().set(subscriber);

                    // Add goal to subscriptions collection
                    Map<String, Object> subscription = new HashMap<>();
                    subscription.put(FirestoreHelper.FIELD_GOAL_ID, docId);
                    FirestoreHelper.getSubscriptionsReference(mUser)
                            .document()
                            .set(subscription)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Add goal to subscriptions prefs
                                    mSubscriptionsPref.edit().putBoolean(docId, true).apply();
                                }
                            });
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
