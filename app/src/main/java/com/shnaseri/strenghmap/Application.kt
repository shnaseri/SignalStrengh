package com.shnaseri.strenghmap

import android.content.Context
import androidx.room.Room
import com.shnaseri.strenghmap.db.AppDatabase
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackMapApplication : MultiDexApplication() {
    companion object {
        lateinit var database: AppDatabase
        lateinit var instance: TrackMapApplication

    }
    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "strengh-map-db",
        ).build()
    }
    fun getContext(): Context = instance.applicationContext

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
}
