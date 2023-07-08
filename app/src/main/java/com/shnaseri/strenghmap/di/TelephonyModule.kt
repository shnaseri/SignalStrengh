package com.shnaseri.strenghmap.di

import android.app.Application
import com.shnaseri.strenghmap.telephony.CustomPhoneStateListener
import com.shnaseri.strenghmap.telephony.TelephonyInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TelephonyModule {

    @Provides
    @Singleton
    fun providesTelephonyInfo(
        application: Application,
        customPhoneStateListener: CustomPhoneStateListener,
    ) = TelephonyInfo(application, customPhoneStateListener)

    @Provides
    @Singleton
    fun providesCustomPhoneStateListener() = CustomPhoneStateListener()
}
