package com.js.photocompress;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;


@androidx.room.Database(entities = {PhotoEntity.class}, version = 1)
public abstract class Database extends RoomDatabase {
    private static volatile Database INSTANCE;

    public static Database getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class,
                            "Database").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract PhotoEntityDAO pathDao();
}

