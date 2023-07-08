package com.shnaseri.strenghmap.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shnaseri.strenghmap.model.PinPoint

@TypeConverters(DateConverter::class)
@Database(entities = [PinPoint::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pinPointDao(): PinPointDao
}
