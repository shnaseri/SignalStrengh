package com.shnaseri.strenghmap.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class PinPoint(
    @SerializedName("signStr")
    @ColumnInfo(name = "signal_strengths")
    var signalStrengths: Double,
    @SerializedName("networkType")
    @ColumnInfo(name = "network_type")
    var networkType: String,
    @SerializedName("lac")
    @ColumnInfo(name = "lac")
    var lac: String,
    @SerializedName("ci")
    @ColumnInfo(name = "ci")
    var ci: String,
    @SerializedName("terminal")
    @ColumnInfo(name = "terminal")
    var terminal: String,
    @SerializedName("latitude")
    @ColumnInfo(name = "lat")
    var lat: Double,
    @SerializedName("longitude")
    @ColumnInfo(name = "longi")
    var longi: Double,
    @SerializedName("eventTime")
    @ColumnInfo(name = "event_time")
    var eventTime: Int,
    @SerializedName("operator")
    @ColumnInfo(name = "operator")
    var operator: String,
    @SerializedName("location")
    @ColumnInfo(name = "country")
    var country: String,
    @ColumnInfo(name = "upload")
    var upload: Boolean,
    @Embedded
    var track: Track,
    @ColumnInfo(name = "track_id")
    var trackId: Int,
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var zeroId: Int,
    @SerializedName("uploadTime")
    var uploadTime: Int,
)
