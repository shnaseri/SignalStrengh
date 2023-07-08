package com.shnaseri.strenghmap.loaders

import android.content.Context
import com.shnaseri.strenghmap.db.AppDatabase

class PinpointLoader(
    context: Context?,
    databaseManager: AppDatabase,
    trackId: Long,
    latSw: Double,
    lonSw: Double,
    latNe: Double,
    lonNe: Double,
) :
    DatabaseLoader(context!!) {
    private val mDatabaseManager: AppDatabase
    private val mTrackId: Long
    private val mLatSw: Double
    private val mLonSw: Double
    private val mLatNe: Double
    private val mLonNe: Double

    init {
        mDatabaseManager = databaseManager
        mTrackId = trackId
        mLatSw = latSw
        mLonSw = lonSw
        mLatNe = latNe
        mLonNe = lonNe
    }

    override fun loadList(): List<*> {
        return mDatabaseManager.pinPointDao().getAll()
    }
}
