package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.models.Note;

import java.util.List;

public interface NotesDataSource {

    interface LoadNotesCallback {

        void onNotesLoaded(List<Note> notes);

        void onDataNotAvailable(Exception e);

    }

    interface GetNoteCallback {

        void onNoteLoaded(Note note);

        void onDataNotAvailable(Exception e);

    }

    void getNotes(@NonNull LoadNotesCallback callback);

    void getNote(@NonNull String noteId, @NonNull GetNoteCallback callback);

    void saveNote (@NonNull Note note);

    void deleteNote(@NonNull String noteId);

}
