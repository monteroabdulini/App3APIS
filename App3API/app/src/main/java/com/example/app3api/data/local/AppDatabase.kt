package com.example.app3api.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoriteEntity::class, CharacterCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun characterCacheDao(): CharacterCacheDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Recrear la tabla favorites con el esquema actualizado para evitar incompatibilidad con v1
                db.execSQL("DROP TABLE IF EXISTS `favorites`")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `favorites` (" +
                        "`id` TEXT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`imageUrl` TEXT NOT NULL, " +
                        "`gender` TEXT NOT NULL, " +
                        "`statusOrRace` TEXT NOT NULL, " +
                        "`description` TEXT NOT NULL, " +
                        "`sourceApi` TEXT NOT NULL, " +
                        "`addedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )

                // Crear la tabla de caché
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `character_cache` (" +
                        "`id` TEXT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`imageUrl` TEXT NOT NULL, " +
                        "`gender` TEXT NOT NULL, " +
                        "`statusOrRace` TEXT NOT NULL, " +
                        "`description` TEXT NOT NULL, " +
                        "`sourceApi` TEXT NOT NULL, " +
                        "`addedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app3api.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instance = it }
            }
    }
}
