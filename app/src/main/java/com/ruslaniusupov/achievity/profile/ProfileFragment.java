package com.ruslaniusupov.achievity.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ruslaniusupov.achievity.AddGoalActivity;
import com.ruslaniusupov.achievity.EditProfileActivity;
import com.ruslaniusupov.achievity.NotesActivity;
import com.ruslaniusupov.achievity.ProfileViewModel;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.GoalsRepository;
import com.ruslaniusupov.achievity.data.UserDataRepository;
import com.ruslaniusupov.achievity.data.models.User;
import com.ruslaniusupov.achievity.splash.SplashActivity;
import com.ruslaniusupov.achievity.adapter.FirestoreGoalsAdapter;
import com.ruslaniusupov.achievity.adapter.OnGoalSelectedListener;
import com.ruslaniusupov.achievity.firebase.FirestoreHelper;
import com.ruslaniusupov.achievity.data.models.Goal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements OnGoalSelectedListener, ProfileContract.View {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER_ID = "user_id";

    private Context mContext;
    private String mUserId;
    private ProfileContract.Presenter mPresenter;

    @BindView(R.id.name)TextView mNameTv;
    @BindView(R.id.bio)TextView mBioTv;
    @BindView(R.id.goals_rv)RecyclerView mGoalsRv;
    @BindView(R.id.add_goal_fab)FloatingActionButton mAddGoalFab;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        mUserId = getArguments().getString(ARG_USER_ID);
        mPresenter = new ProfilePresenter(this, mUserId, new GoalsRepository(), new UserDataRepository());

        mAddGoalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AddGoalActivity.class));
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_sign_out:
                mPresenter.signOut();
                return true;
            case R.id.action_edit_profile:
                editProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void editProfile() {
        startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    @Override
    public void onGoalSelected(DocumentSnapshot goal) {
        Intent startNotesActivity = new Intent(mContext, NotesActivity.class);
        startNotesActivity.putExtra(NotesActivity.EXTRA_GOAL_ID, goal.getId());
        startActivity(startNotesActivity);
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showGoals(List<Goal> goals) {

    }

    @Override
    public void showUserData(User user) {
        mNameTv.setText(user.getFullName());
        mBioTv.setText(user.getBio());
    }

    @Override
    public void showLoadingGoalsError() {

    }

    @Override
    public void showNoGoals() {

    }

    @Override
    public void setAddGoalBtn(boolean active) {
        if (active) {
            mAddGoalFab.setVisibility(View.VISIBLE);
        } else {
            mAddGoalFab.setVisibility(View.GONE);
        }
    }
}
