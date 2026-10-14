package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.WordDao
import com.example.data.local.entity.WordEntity

@Database(
  entities = [WordEntity::class],
  version = 5,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun wordDao(): WordDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    val MIGRATION_4_5 = object : Migration(4, 5) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE words ADD COLUMN useCount INTEGER NOT NULL DEFAULT 0")
      }
    }

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "english_database"
        )
        .addMigrations(MIGRATION_4_5)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
