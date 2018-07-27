package com.ruslaniusupov.achievity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.GoalsLikedPrefsRepository;
import com.ruslaniusupov.achievity.data.GoalsRepository;
import com.ruslaniusupov.achievity.data.SubscriptionsPrefsRepository;
import com.ruslaniusupov.achievity.data.UserDataRepository;
import com.ruslaniusupov.achievity.data.models.Goal;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

class GoalsViewHolder extends RecyclerView.ViewHolder implements GoalsViewHolderContract.View {

    private final GoalsViewHolderContract.Presenter mPresenter;

    @BindView(R.id.author)TextView mAuthorTv;
    @BindView(R.id.publish_date)TextView mPubDateTv;
    @BindView(R.id.body)TextView mBodyTv;
    @BindView(R.id.fav_btn)ImageButton mFavBtn;
    @BindView(R.id.notifications_btn)ImageButton mNotificationsBtn;

    GoalsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mPresenter = new GoalsViewHolderPresenter(this, new UserDataRepository(),
                new GoalsLikedPrefsRepository(itemView.getContext()),
                new SubscriptionsPrefsRepository(itemView.getContext()),
                new GoalsRepository());
    }

    void bind(final Goal goal, final OnGoalSelectedListener listener) {

        mAuthorTv.setText(goal.getAuthor());
        Date pubDate = goal.getTimestamp();
        mPubDateTv.setText(pubDate.toString());
        mBodyTv.setText(goal.getText());

        mPresenter.initButtonsState(goal.getId());

        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFavBtn.isSelected()) {

                    mFavBtn.setSelected(false);

                    mPresenter.cancelLike(goal.getId());

                } else {

                    mFavBtn.setSelected(true);

                    mPresenter.like(goal.getId());

                }

            }
        });

        mNotificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mNotificationsBtn.isSelected()) {

                    mNotificationsBtn.setSelected(false);

                    mPresenter.unsubscribe(goal.getId());

                } else {

                    mNotificationsBtn.setSelected(true);

                    mPresenter.subscribe(goal.getId());
                }

            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGoalSelected(goal);
            }
        });

    }

    @Override
    public void setLikeBtnState(boolean isSelected) {
        mFavBtn.setSelected(isSelected);
    }

    @Override
    public void setSubscribeBtnState(boolean isSelected) {
        mNotificationsBtn.setSelected(isSelected);
    }
}
