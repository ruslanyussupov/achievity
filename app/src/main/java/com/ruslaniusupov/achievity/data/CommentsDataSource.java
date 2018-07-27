package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.models.Comment;

import java.util.List;

public interface CommentsDataSource {

    interface LoadCommentsCallback {

        void onCommentsLoaded(List<Comment> comments);

        void onDataNotAvailable(Exception e);

    }

    interface GetCommentCallback {

        void onCommentLoaded(Comment comment);

        void onDataNotAvailable(Exception e);

    }

    void getComments(@NonNull LoadCommentsCallback callback);

    void getComment(@NonNull String commentId, @NonNull GetCommentCallback callback);

    void saveComment(@NonNull Comment comment);

    void deleteComment(@NonNull String commentId);

}
