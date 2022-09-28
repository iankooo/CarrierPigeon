package com.example.carrier_pigeon.app.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.room.Room
import com.example.carrier_pigeon.app.Config.PIGEON_DATABASE
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.features.pigeons.database.PigeonDao
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesApplication(app: Application): Context = app

    @Provides
    fun providesResources(app: Application): Resources = app.resources

    @Provides
    fun providesSharedPreferences(app: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(app)

    @Provides
    fun providesSharedPrefsWrapper(sharedPreferences: SharedPreferences): SharedPrefsWrapper {
        return SharedPrefsWrapper(sharedPreferences)
    }

    @Singleton
    @Provides
    fun providePigeonDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, PigeonDatabase::class.java, PIGEON_DATABASE)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesPigeonDao(pigeonDatabase: PigeonDatabase): PigeonDao = pigeonDatabase.pigeonDao()
}
