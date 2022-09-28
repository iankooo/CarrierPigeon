package com.example.carrier_pigeon.features.pigeons.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.carrier_pigeon.app.Config.PIGEON_DATABASE
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import com.example.carrier_pigeon.features.pigeonsFlights.database.RecordDao

@Database(entities = [Pigeon::class, Record::class], version = 2)
abstract class PigeonDatabase : RoomDatabase() {

    abstract fun pigeonDao(): PigeonDao
    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: PigeonDatabase? = null

        fun getInstance(context: Context): PigeonDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PigeonDatabase::class.java,
                        PIGEON_DATABASE
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
