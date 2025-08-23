package com.example.parteincidenciacompose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ParteEntity::class], version = 2)
abstract class ParteDatabase : RoomDatabase() {
    abstract fun parteDao(): ParteDao

    companion object {
        @Volatile
        private var INSTANCE: ParteDatabase? = null

        fun getDatabase(context: Context): ParteDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
                    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE partes ADD COLUMN estado TEXT NOT NULL DEFAULT 'EN_CURSO'")
                    }
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParteDatabase::class.java,
                    "parte_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
