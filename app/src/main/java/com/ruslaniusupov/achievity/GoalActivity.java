package com.ruslaniusupov.achievity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ruslaniusupov.achievity.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalActivity extends AppCompatActivity {

    private static final String LOG_TAG = GoalActivity.class.getSimpleName();

    private Menu mMenu;
    private TextWatcher mTextWatcher;

    @BindView(R.id.goal_et)EditText mGoalEt;
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mMenu.findItem(R.id.action_post).setVisible(true);
                } else {
                    mMenu.findItem(R.id.action_post).setVisible(false);
                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoalEt.addTextChangedListener(mTextWatcher);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoalEt.removeTextChangedListener(mTextWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.goal, menu);
        menu.findItem(R.id.action_post).setVisible(false);
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
                postGoal();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void postGoal() {

        final Editable goalText = mGoalEt.getText();
        goalText.clearSpans();

        final DocumentReference goalRef = FirebaseFirestore.getInstance().collection(Goal.GOALS)
                .document();

        goalRef.set(new Goal(FirebaseAuth.getInstance().getCurrentUser(), goalText.toString()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Count.createCounter(goalRef.collection(Count.COUNTERS)
                                .document("like_counter"), 10);

                        Count.createCounter(goalRef.collection(Count.COUNTERS)
                                .document("unlike_counter"), 10);

                        GoalActivity.this.finish();
                    }
                });

    }

    private void closeActivity() {

        if (mGoalEt.getText().length() > 0) {

            new DiscardDialog().show(getFragmentManager(), DiscardDialog.class.getSimpleName());

        } else {

            finish();

        }

    }

}
