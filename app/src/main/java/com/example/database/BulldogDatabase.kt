package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        KeywordEntity::class,
        BlockedFileEntity::class,
        CommitmentLockEntity::class,
        ProtectedAppEntity::class,
        ProtectionSettingsEntity::class,
        UnlockRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BulldogDatabase : RoomDatabase() {
    abstract fun keywordDao(): KeywordDao
    abstract fun blockedFileDao(): BlockedFileDao
    abstract fun commitmentDao(): CommitmentDao
    abstract fun settingsDao(): SettingsDao
    abstract fun protectedAppDao(): ProtectedAppDao
    abstract fun unlockRequestDao(): UnlockRequestDao

    companion object {
        @Volatile
        private var INSTANCE: BulldogDatabase? = null

        fun getInstance(context: Context): BulldogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BulldogDatabase::class.java,
                    "bulldog_protection.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
