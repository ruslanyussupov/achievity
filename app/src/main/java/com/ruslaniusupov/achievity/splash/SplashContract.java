package com.ruslaniusupov.achievity.splash;

import android.content.Intent;

public interface SplashContract {

    interface View {

        void initAuthorization(Intent authorizationIntent);

        void authorizationPassed();

    }

    interface Presenter {

        void checkAuthStatus();

        void authFailed(Intent data);

        void authSuccess();

    }


}
