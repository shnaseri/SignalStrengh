package com.shnaseri.strenghmap.di

import android.app.Application
import com.shnaseri.strenghmap.TrackMapApplication
import com.shnaseri.strenghmap.controller.PositioningManager
import com.shnaseri.strenghmap.controller.TrackingManager
import com.shnaseri.strenghmap.db.AppDatabase
import com.shnaseri.strenghmap.presentation.DataFragment
import com.shnaseri.strenghmap.presentation.TrackListFragment
import com.shnaseri.strenghmap.presentation.TrackMapFragment
import com.shnaseri.strenghmap.telephony.CustomPhoneStateListener
import com.shnaseri.strenghmap.telephony.TelephonyInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ControllerModule {
    @Provides
    @Singleton
    fun providesTrackManager(application: Application) = TrackingManager(application)

    @Provides
    @Singleton
    fun providesPositioningManager(application: Application) = PositioningManager(application)

    @Provides
    @Singleton
    fun providesTelephonyInfo(
        application: Application,
        customPhoneStateListener: CustomPhoneStateListener,
    ): TelephonyInfo = TelephonyInfo(application, customPhoneStateListener)

    @Provides
    @Singleton
    fun providesCustomPhoneStateListener(): CustomPhoneStateListener = CustomPhoneStateListener()

    @Provides
    @Singleton
    fun provideDataBaseManager(application: Application): AppDatabase = TrackMapApplication.database

    @Provides
    @Singleton
    fun providesDataFragment(
        trackingManager: TrackingManager,
        telephonyInfo: TelephonyInfo,
        customPhoneStateListener: CustomPhoneStateListener,
        dataBase: AppDatabase,
    ): DataFragment = DataFragment(
        trackingManager,
        telephonyInfo,
        customPhoneStateListener,
        dataBase,
    )

    @Provides
    @Singleton
    fun providesTrackListFragment(
        trackingManager: TrackingManager,
        dataBase: AppDatabase
    ): TrackListFragment = TrackListFragment(
        trackingManager,
        dataBase
    )

    @Provides
    @Singleton
    fun providesTrackMapFragment(
        telephonyInfo: TelephonyInfo,
        trackingManager: TrackingManager,
        positioningManager: PositioningManager,
        dataBase: AppDatabase,
    ): TrackMapFragment =
        TrackMapFragment(telephonyInfo, trackingManager, positioningManager, dataBase)
}
