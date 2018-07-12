package com.ruslaniusupov.achievity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private String mOldName;
    private String mOldBio;
    private FirebaseUser mUser;

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.name_et)EditText mNameEt;
    @BindView(R.id.bio_et)EditText mBioEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        updateUi();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_done:
                updateProfile();
                finish();
                return true;
            case android.R.id.home:
                closeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeActivity() {

        if (getChangedName() != null || getChangedBio() != null) {
            new DiscardDialog().show(getFragmentManager(), TAG);
        } else {
            finish();
        }

    }

    private void updateUi() {

        mOldName = mUser.getDisplayName();
        mNameEt.setText(mOldName);
        FirestoreHelper.getUserDataReference(mUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mOldBio = documentSnapshot.getString(FirestoreHelper.FIELD_BIO);
                mBioEt.setText(mOldBio);
            }
        });

    }

    private void updateProfile() {
        if (getChangedName() != null) {
            updateName(getChangedName());
        }
        if (getChangedBio() != null) {
            updateBio(getChangedBio());
        }
    }

    private void updateName(String newName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        mUser.updateProfile(profileUpdates);
    }

    private void updateBio(String newBio) {
        Map<String, Object> data = new HashMap<>();
        data.put(FirestoreHelper.FIELD_BIO, newBio);
        FirestoreHelper.getUserDataReference(mUser).set(data);
    }

    private String getChangedName() {
        String name = mNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.equals(mOldName, name)) {
            return null;
        } else {
            return name;
        }
    }

    private String getChangedBio() {
        String bio = mBioEt.getText().toString().trim();
        if (TextUtils.isEmpty(bio) || TextUtils.equals(mOldBio, bio)) {
            return null;
        } else {
            return bio;
        }
    }

}
