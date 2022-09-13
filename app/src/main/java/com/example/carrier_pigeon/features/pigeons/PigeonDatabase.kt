package com.example.carrier_pigeon.features.pigeons

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

@Database(entities = [Pigeon::class], version = 1)
abstract class PigeonDatabase : RoomDatabase() {

    abstract fun pigeonDao(): PigeonDao

    companion object {
        private const val DATABASE_NAME = "pigeon_database"

        @Volatile
        private var INSTANCE: PigeonDatabase? = null

        fun getInstance(context: Context): PigeonDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PigeonDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}