package com.shnaseri.strenghmap.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.Date

data class Track(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo("start_date")
    var startDate: Date? = null,
)
