package com.example.parteincidenciacompose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ParteEntity::class], version = 1)
abstract class ParteDatabase : RoomDatabase() {
    abstract fun parteDao(): ParteDao

    companion object {
        @Volatile
        private var INSTANCE: ParteDatabase? = null

        fun getDatabase(context: Context): ParteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParteDatabase::class.java,
                    "parte_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
