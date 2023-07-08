package com.shnaseri.strenghmap.di

import android.app.Application
import com.shnaseri.strenghmap.controller.PositioningManager
import com.shnaseri.strenghmap.controller.TrackingManager
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
}
