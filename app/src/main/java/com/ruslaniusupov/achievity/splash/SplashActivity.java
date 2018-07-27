package com.ruslaniusupov.achievity.splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ruslaniusupov.achievity.MainActivity;
import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.DefPrefsRepository;
import com.ruslaniusupov.achievity.data.GoalsLikedPrefsRepository;
import com.ruslaniusupov.achievity.data.SubscriptionsPrefsRepository;


public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    private static final String TAG = "SplashActivity";
    private static final int RC_SIGN_IN = 100;

    private SplashContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mPresenter = new SplashPresenter(this,
                new DefPrefsRepository(this),
                new GoalsLikedPrefsRepository(this),
                new SubscriptionsPrefsRepository(this));

        mPresenter.checkAuthStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mPresenter.authSuccess();

            } else {
                mPresenter.authFailed(data);
            }
        }

    }

    @Override
    public void initAuthorization(Intent authorizationIntent) {
        startActivityForResult(authorizationIntent, RC_SIGN_IN);
    }

    @Override
    public void authorizationPassed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
