package com.shnaseri.strenghmap.loaders

import android.content.Context
import com.shnaseri.strenghmap.db.AppDatabase

class TrackLoader(context: Context, databaseManager: AppDatabase) : DatabaseLoader(context) {
    private val mDatabaseManager: AppDatabase

    init {
        mDatabaseManager = databaseManager
    }

    override fun loadList(): List<*> {
        return mDatabaseManager.pinPointDao().getAll()
    }
}
