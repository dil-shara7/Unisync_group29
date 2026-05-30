package com.example.fittracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val studentId: String = "",
    val faculty: String = "",
    val program: String = "",
    val year: String = "",
    val avatarUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "schedule_events")
data class ScheduleEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val title: String,
    val type: String,          // Class / Exam / Study Session
    val date: String,          // free-form date string (matches the form input)
    val time: String,          // free-form time string
    val location: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val title: String,
    val dueAt: String,         // human-readable due description ("Tomorrow · 5:00 PM")
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val title: String,
    val subtitle: String = "",
    val body: String = "",
    val category: String = "",
    val imageUri: String? = null,
    val isFavorite: Boolean = false,
    val isSaved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "marketplace_listings")
data class MarketplaceListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val name: String,
    val category: String,
    val price: Int,
    val description: String = "",
    val condition: String = "",
    val sellerName: String = "",
    val contact: String = "",
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = MarketplaceListingEntity::class,
            parentColumns = ["id"],
            childColumns = ["listingId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("listingId"), Index("ownerId")]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val listingId: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUri: String? = null,
)

@Entity(tableName = "lost_found_items")
data class LostFoundItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reporterId: Long,
    val name: String,
    val category: String,
    val location: String,
    val timePeriod: String,
    val status: String,        // "lost" or "found"
    val foundBy: String = "",
    val contact: String = "",
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
