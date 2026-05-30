package com.example.fittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface ScheduleEventDao {
    @Query("SELECT * FROM schedule_events WHERE ownerId = :ownerId ORDER BY id DESC")
    suspend fun byOwner(ownerId: Long): List<ScheduleEventEntity>

    @Query("SELECT * FROM schedule_events WHERE ownerId = :ownerId AND title LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun search(ownerId: Long, query: String): List<ScheduleEventEntity>

    @Query("SELECT * FROM schedule_events WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): ScheduleEventEntity?

    @Insert
    suspend fun insert(event: ScheduleEventEntity): Long

    @Update
    suspend fun update(event: ScheduleEventEntity)

    @Delete
    suspend fun delete(event: ScheduleEventEntity)

    @Query("SELECT COUNT(*) FROM schedule_events")
    suspend fun count(): Int
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE ownerId = :ownerId ORDER BY id DESC")
    suspend fun byOwner(ownerId: Long): List<ReminderEntity>

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT COUNT(*) FROM reminders")
    suspend fun count(): Int
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE ownerId = :ownerId ORDER BY id DESC")
    suspend fun byOwner(ownerId: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE ownerId = :ownerId AND isFavorite = 1 ORDER BY id DESC")
    suspend fun favoritesByOwner(ownerId: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE ownerId = :ownerId AND isSaved = 1 ORDER BY id DESC")
    suspend fun savedByOwner(ownerId: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): NoteEntity?

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun count(): Int
}

@Dao
interface MarketplaceListingDao {
    @Query("SELECT * FROM marketplace_listings ORDER BY id DESC")
    suspend fun all(): List<MarketplaceListingEntity>

    @Query("SELECT * FROM marketplace_listings WHERE category = :category ORDER BY id DESC")
    suspend fun byCategory(category: String): List<MarketplaceListingEntity>

    @Query("SELECT * FROM marketplace_listings WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun search(query: String): List<MarketplaceListingEntity>

    @Query("SELECT * FROM marketplace_listings WHERE ownerId = :ownerId ORDER BY id DESC")
    suspend fun byOwner(ownerId: Long): List<MarketplaceListingEntity>

    @Query("SELECT * FROM marketplace_listings WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): MarketplaceListingEntity?

    @Insert
    suspend fun insert(listing: MarketplaceListingEntity): Long

    @Update
    suspend fun update(listing: MarketplaceListingEntity)

    @Delete
    suspend fun delete(listing: MarketplaceListingEntity)

    @Query("SELECT COUNT(*) FROM marketplace_listings")
    suspend fun count(): Int
}

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items WHERE ownerId = :ownerId ORDER BY id ASC")
    suspend fun byOwner(ownerId: Long): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE ownerId = :ownerId AND listingId = :listingId LIMIT 1")
    suspend fun findLine(ownerId: Long, listingId: Long): CartItemEntity?

    @Insert
    suspend fun insert(line: CartItemEntity): Long

    @Update
    suspend fun update(line: CartItemEntity)

    @Delete
    suspend fun delete(line: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE ownerId = :ownerId")
    suspend fun clearCart(ownerId: Long)
}

@Dao
interface LostFoundDao {
    @Query("SELECT * FROM lost_found_items WHERE status = :status ORDER BY id DESC")
    suspend fun byStatus(status: String): List<LostFoundItemEntity>

    @Query("SELECT * FROM lost_found_items WHERE status = :status AND category = :category ORDER BY id DESC")
    suspend fun byStatusAndCategory(status: String, category: String): List<LostFoundItemEntity>

    @Query("SELECT * FROM lost_found_items WHERE reporterId = :reporterId ORDER BY id DESC")
    suspend fun byReporter(reporterId: Long): List<LostFoundItemEntity>

    @Insert
    suspend fun insert(item: LostFoundItemEntity): Long

    @Delete
    suspend fun delete(item: LostFoundItemEntity)

    @Query("SELECT COUNT(*) FROM lost_found_items")
    suspend fun count(): Int
}
