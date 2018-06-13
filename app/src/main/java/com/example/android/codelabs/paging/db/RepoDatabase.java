package com.example.android.codelabs.paging.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.codelabs.paging.model.Repo;

/**
 * Database schema that holds the list of repos.
 */
@Database(entities = {Repo.class}, version = 1, exportSchema = false)
public abstract class RepoDatabase extends RoomDatabase {

    private static volatile RepoDatabase INSTANCE;

    public static RepoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RepoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static RepoDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                RepoDatabase.class,
                "Github.db"
        ).build();
    }

    //RepoDao is a DAO class annotated with @Dao
    public abstract RepoDao reposDao();
}
