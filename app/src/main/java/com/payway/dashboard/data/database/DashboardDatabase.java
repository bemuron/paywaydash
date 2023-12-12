package com.payway.dashboard.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.payway.dashboard.models.Transaction;

@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
public abstract class DashboardDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "dashboard.db";

    /*
    Having more than one instance of a database running causes consistency problems,
    if, for example, you try to read the database with one instance while you're
    writing with another instance. To make sure that you're only creating one instance
    of the RoomDatabase, your database class should be a Singleton.
     */
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static volatile DashboardDatabase sInstance;

    public static DashboardDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            DashboardDatabase.class, DashboardDatabase.DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database

    public abstract TransactionsDao transactionsDao();
}
