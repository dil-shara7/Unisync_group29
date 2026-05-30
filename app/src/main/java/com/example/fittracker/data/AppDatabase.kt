package com.example.fittracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ScheduleEventEntity::class,
        ReminderEntity::class,
        NoteEntity::class,
        MarketplaceListingEntity::class,
        CartItemEntity::class,
        LostFoundItemEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun scheduleEventDao(): ScheduleEventDao
    abstract fun reminderDao(): ReminderDao
    abstract fun noteDao(): NoteDao
    abstract fun marketplaceListingDao(): MarketplaceListingDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun lostFoundDao(): LostFoundDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "unisync.db",
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}
