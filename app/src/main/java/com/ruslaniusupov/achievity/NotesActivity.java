package com.ruslaniusupov.achievity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.ruslaniusupov.achievity.adapter.FirestoreNotesAdapter;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.model.Note;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends AppCompatActivity {

    private static final String TAG = NotesActivity.class.getSimpleName();
    public static final String EXTRA_GOAL_ID = "goal_id";
    public static final String EXTRA_USER_ID = "user_id";

    private String mGoalId;
    private FirestoreNotesAdapter mAdapter;

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.add_note_fab)FloatingActionButton mAddNoteFab;
    @BindView(R.id.notes_rv)RecyclerView mNotesRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoalId = getIntent().getStringExtra(EXTRA_GOAL_ID);

        checkOwner();

        Query query = FirestoreHelper.getNotesReference(mGoalId)
                .orderBy(Note.FIELD_TIMESTAMP, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        mAdapter = new FirestoreNotesAdapter(options);
        mNotesRv.setAdapter(mAdapter);
        mNotesRv.setLayoutManager(new LinearLayoutManager(this));

        mAddNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startAddNoteActivity = new Intent(NotesActivity.this,
                        AddNoteActivity.class);
                startAddNoteActivity.putExtra(AddNoteActivity.EXTRA_GOAL_ID, mGoalId);
                startActivity(startAddNoteActivity);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    // Show add fab if the user is the goal's author
    private void checkOwner() {
        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if (TextUtils.equals(userId, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            mAddNoteFab.setVisibility(View.VISIBLE);
        } else {
            mAddNoteFab.setVisibility(View.GONE);
        }
    }
}
