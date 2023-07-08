package com.shnaseri.strenghmap.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shnaseri.strenghmap.model.PinPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface PinPointDao {
    @Query("SELECT * FROM pinpoint")
    fun getAll(): List<PinPoint>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pinPoints: List<PinPoint>)

    @Insert
    fun insert(pinPoint: PinPoint)

    @Query("SELECT * FROM pinpoint WHERE `zeroId` = :key LIMIT 1")
    fun findByKey(key: String): PinPoint

    @Delete
    fun delete(pinPoint: PinPoint)
}
