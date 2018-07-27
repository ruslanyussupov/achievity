package com.ruslaniusupov.achievity.adapter;

public interface GoalsViewHolderContract {

    interface View {

        void setLikeBtnState(boolean isSelected);

        void setSubscribeBtnState(boolean isSelected);

    }

    interface Presenter {

        void initButtonsState(String goalId);

        void like(String goalId);

        void cancelLike(String goalId);

        void subscribe(String goalId);

        void unsubscribe(String goalId);

    }

}
