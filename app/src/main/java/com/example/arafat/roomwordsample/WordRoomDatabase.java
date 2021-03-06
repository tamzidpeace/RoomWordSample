package com.example.arafat.roomwordsample;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


@Database(entities = {Word.class}, version = 1)
public abstract class WordRoomDatabase extends RoomDatabase {

    public abstract WordDao wordDao();


    private static volatile WordRoomDatabase INSTANCE;

    static WordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    //create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // AsyncTask that deletes the contents of the database

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final WordDao mDao;

        String[] words = {"dolphin", "crocodile", "cobra"};

        PopulateDbAsync(WordRoomDatabase db) {

            mDao = db.wordDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDao.deleteAll();

            // inserting data
            for (int i = 0; i < words.length; i++) {
                Word word = new Word(words[i]);
                mDao.insert(word);
            }

            return null;
        }
    }
}
