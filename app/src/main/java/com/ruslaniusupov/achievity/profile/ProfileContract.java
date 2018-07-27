package com.ruslaniusupov.achievity.profile;

import com.ruslaniusupov.achievity.data.models.Goal;
import com.ruslaniusupov.achievity.data.models.User;

import java.util.List;

public interface ProfileContract {

    interface View {

        void setLoadingIndicator(boolean active);

        void showGoals(List<Goal> goals);

        void showUserData(User user);

        void showLoadingGoalsError();

        void showNoGoals();

        void setAddGoalBtn(boolean active);

    }

    interface Presenter {

        void loadGoals();

        void loadUserData();

        void signOut();

    }

}
