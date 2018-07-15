package com.ruslaniusupov.achievity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.model.Note;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirestoreNotesAdapter extends FirestoreRecyclerAdapter<Note, FirestoreNotesAdapter.ViewHolder> {

    private static final String TAG = FirestoreNotesAdapter.class.getSimpleName();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreNotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Note model) {
        Log.d(TAG, "onBindViewHolder");
        holder.bind(getSnapshots().getSnapshot(position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(rootView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseUser mUser;

        @BindView(R.id.author)TextView mAuthorTv;
        @BindView(R.id.publish_date)TextView mPubDateTv;
        @BindView(R.id.body)TextView mBodyTv;
        @BindView(R.id.fav_btn)ImageButton mFavBtn;
        @BindView(R.id.likes_counter)TextView mLikesCounterTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        void bind(DocumentSnapshot snapshot) {

            Note note = snapshot.toObject(Note.class);
            String docId = snapshot.getId();
            Log.d(TAG, "Note ID: " + docId);

            mAuthorTv.setText(mUser.getDisplayName());
            mBodyTv.setText(note.getText());
            mPubDateTv.setText(note.getTimestamp().toString());

        }

    }

}
