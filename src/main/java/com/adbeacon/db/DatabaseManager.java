package com.adbeacon.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by mihne on 06.10.2015.
 */
public class DatabaseManager {
    private static volatile DatabaseManager mInstance;
    private DatabaseHelper mHelper;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (mInstance == null) {
            synchronized (DatabaseManager.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (mHelper == null) mHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public void release() {
        if (mHelper != null) OpenHelperManager.releaseHelper();
    }

    public DatabaseHelper getHelper() {
        return mHelper;
    }
}

