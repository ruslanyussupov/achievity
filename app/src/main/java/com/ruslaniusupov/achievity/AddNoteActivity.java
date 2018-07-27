package com.ruslaniusupov.achievity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.data.models.Note;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = AddNoteActivity.class.getSimpleName();
    public static final String EXTRA_GOAL_ID = "goal_id";

    private TextWatcher mNoteTextWatcher;
    private MenuItem mActionPost;
    private String mGoalId;

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.note_et)EditText mNoteEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        mGoalId = getIntent().getStringExtra(EXTRA_GOAL_ID);

        mNoteTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mActionPost.setVisible(true);
                } else {
                    mActionPost.setVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mNoteEt.addTextChangedListener(mNoteTextWatcher);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNoteEt.removeTextChangedListener(mNoteTextWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note, menu);
        mActionPost = menu.findItem(R.id.action_post);
        mActionPost.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                closeActivity();
                return true;
            case R.id.action_post:
                postNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void postNote() {
        String text = mNoteEt.getText().toString();
        FirestoreHelper.getNotesReference(mGoalId)
                .document()
                .set(new Note(FirebaseAuth.getInstance().getCurrentUser(), text.trim()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AddNoteActivity.this.finish();
                    }
                });
    }

    private void closeActivity() {

        if (mNoteEt.getText().length() > 0) {
            new DiscardDialog().show(getFragmentManager(), TAG);
        } else {
            finish();
        }

    }

}
