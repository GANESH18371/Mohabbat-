package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ProjectDao
import com.example.data.model.ProjectEntity

@Database(entities = [ProjectEntity::class], version = 1, exportSchema = false)
abstract class MohabbatDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: MohabbatDatabase? = null

        fun getDatabase(context: Context): MohabbatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MohabbatDatabase::class.java,
                    "mohabbat_ai_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
