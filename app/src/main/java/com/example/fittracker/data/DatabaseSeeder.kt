package com.example.fittracker.data

/**
 * First-run seeding. Inserts sample marketplace listings, notes, reminders,
 * and lost & found items so demo screens render with content out of the box.
 * Listings are owned by a system seed user (id = 1) created here too if absent.
 */
object DatabaseSeeder {

    private const val SEED_USER_EMAIL = "demo@unisync.local"

    /** Copies starter notes + reminders to a freshly-signed-up user. */
    suspend fun seedNewUser(db: AppDatabase, ownerId: Long) {
        seedNotes(db.noteDao(), ownerId)
        seedReminders(db.reminderDao(), ownerId)
    }

    suspend fun seedIfEmpty(db: AppDatabase) {
        val users = db.userDao()
        val listings = db.marketplaceListingDao()
        val notes = db.noteDao()
        val reminders = db.reminderDao()
        val lostFound = db.lostFoundDao()

        // Ensure a seed owner exists for sample content
        val seedOwnerId: Long = users.findByEmail(SEED_USER_EMAIL)?.id
            ?: users.insert(
                UserEntity(
                    name = "UniSync Demo",
                    email = SEED_USER_EMAIL,
                    passwordHash = "",  // unusable, seed user can't log in
                    passwordSalt = "",
                )
            )

        if (listings.count() == 0) seedListings(listings, seedOwnerId)
        if (notes.count() == 0) seedNotes(notes, seedOwnerId)
        if (reminders.count() == 0) seedReminders(reminders, seedOwnerId)
        if (lostFound.count() == 0) seedLostFound(lostFound, seedOwnerId)
    }

    private suspend fun seedListings(dao: MarketplaceListingDao, owner: Long) {
        val data = listOf(
            // Lifestyle on Campus
            Triple("University Hoodie", "Lifestyle on Campus", 50),
            Triple("Sports Shoes", "Lifestyle on Campus", 170),
            Triple("leather Backpack", "Lifestyle on Campus", 90),
            Triple("Laptop Sticker Pack", "Lifestyle on Campus", 30),
            // Electronics
            Triple("MacBook Air M1 (2020)", "Electronics", 650),
            Triple("Scientific Calculator", "Electronics", 25),
            Triple("JBL Tune 510BT", "Electronics", 50),
            Triple("Wireless Mouse", "Electronics", 15),
            Triple("Wireless Charger", "Electronics", 150),
            Triple("USB Flash Drive", "Electronics", 10),
            // Stationary & Accessories
            Triple("Notebook Set", "Stationary & Accessories", 20),
            Triple("Highlighter Set", "Stationary & Accessories", 10),
            Triple("Water Bottle", "Stationary & Accessories", 50),
            Triple("Sticky Notes Pad", "Stationary & Accessories", 15),
            Triple("Ballpoint Pen Set", "Stationary & Accessories", 5),
            Triple("Laptop Covers", "Stationary & Accessories", 20),
            // Furniture & Hostel Item
            Triple("Tub Chair", "Furniture & Hostel Item", 10),
            Triple("Table Lamp", "Furniture & Hostel Item", 10),
            Triple("Storage Box", "Furniture & Hostel Item", 50),
            Triple("Mini Book Shelf", "Furniture & Hostel Item", 15),
            Triple("Laundry Basket", "Furniture & Hostel Item", 15),
            Triple("Adjustable Table", "Furniture & Hostel Item", 20),
        )
        data.forEach { (name, category, price) ->
            dao.insert(
                MarketplaceListingEntity(
                    ownerId = owner,
                    name = name,
                    category = category,
                    price = price,
                    description = "Lightly used. In good condition.",
                    condition = "Used – Like New",
                    sellerName = "John Axy",
                    contact = "+94 77 123 4567",
                )
            )
        }
    }

    suspend fun seedNotes(dao: NoteDao, owner: Long) {
        listOf(
            "Networking Lecture 4" to "Routing protocols · 12 pages",
            "DBMS · Indexing" to "Compiled from 3 lectures · 8 pages",
            "Calculus 2 · Series" to "Worked examples · 14 pages",
            "OS · Process scheduling" to "Tutorial answers · 6 pages",
            "Linear Algebra · Eigenvalues" to "Slides + summary · 10 pages",
            "Software Engineering" to "Group project plan · 4 pages",
        ).forEachIndexed { index, (title, subtitle) ->
            dao.insert(
                NoteEntity(
                    ownerId = owner,
                    title = title,
                    subtitle = subtitle,
                    body = "Sample note body for $title.",
                    category = "Lecture",
                    isFavorite = index < 2,
                )
            )
        }
    }

    suspend fun seedReminders(dao: ReminderDao, owner: Long) {
        listOf(
            "Submit DBMS assignment" to "Tomorrow · 5:00 PM",
            "Networking quiz" to "Wed · 9:00 AM",
            "Meet supervisor" to "Thu · 2:00 PM · Block 3",
            "Pay hostel fees" to "Fri · before 4:00 PM",
        ).forEach { (title, due) ->
            dao.insert(ReminderEntity(ownerId = owner, title = title, dueAt = due))
        }
    }

    private suspend fun seedLostFound(dao: LostFoundDao, reporter: Long) {
        // Lost items grouped by category
        val lostByCategory = mapOf(
            "Stationary & Accessories" to listOf(
                "Red colored pencil case" to "Lost 1 hour ago at Art Room",
                "Black colored journal book" to "Lost 2 days ago at Auditorium",
                "Glass water bottle" to "Lost 2 days ago at Cafeteria",
                "Anime key-chain" to "Lost a 5 days ago at Gate",
                "Purple colored pencil case" to "Lost 6 days ago at First Floor",
                "Green colored steel bottle" to "Lost 6 days ago at Auditorium",
                "Leather backpack" to "Lost a week ago at Cafeteria",
                "Violet mist colored tumbler" to "Lost a week ago at Gate",
            ),
            "Electronics" to listOf(
                "Samsung S24 mobile phone" to "Lost 1 day ago at IoT Lab",
                "Laptop charger" to "Lost 3 days ago at Auditorium",
                "Blue stripped smart watch" to "Lost 5 days ago at Printing Room",
                "JBL headphone set" to "Lost 5 days ago at Lecture Room",
            ),
            "Wallets" to listOf(
                "Mini card holder - Gents" to "Lost 5 hours ago at Cafeteria",
                "Ladies brown colored wallet" to "Lost 1 day ago at Swimming Pool",
                "Gents leather wallet" to "Lost 1 day ago at IoT Lab",
                "Ladies wallet with long strap" to "Lost 2 days ago at Rest Room",
            ),
            "Keys" to listOf(
                "House door key" to "Lost 8 hours ago at Cafeteria",
                "Door key card" to "Lost 1 day ago at Playground",
                "Bicycle key" to "Lost 1 day ago at Gate",
                "House door key" to "Lost 2 days ago at Lecture Room",
            ),
        )
        lostByCategory.forEach { (category, items) ->
            items.forEach { (name, meta) ->
                dao.insert(
                    LostFoundItemEntity(
                        reporterId = reporter,
                        name = name,
                        category = category,
                        location = meta.substringAfter("at ", ""),
                        timePeriod = meta.substringBefore(" at"),
                        status = "lost",
                    )
                )
            }
        }

        // Found items, distributed across proper categories
        val foundItems = listOf(
            Triple("Red colored pencil case", "Found 30 minutes ago at Art Room", "Stationary & Accessories"),
            Triple("Violet mist colored tumbler", "Found 5 days ago at Gate", "Stationary & Accessories"),
            Triple("Black notebook", "Found 2 hours ago at Library", "Stationary & Accessories"),
            Triple("Blue stripped smart watch", "Found 4 hours ago at Cafeteria", "Electronics"),
            Triple("Wireless earbuds", "Found 1 day ago at IoT Lab", "Electronics"),
            Triple("Door key card", "Found 7 hours ago at Playground", "Keys"),
            Triple("Bicycle key", "Found 3 hours ago at Gate", "Keys"),
            Triple("Brown leather wallet", "Found 1 day ago at Auditorium", "Wallets"),
        )
        foundItems.forEach { (name, meta, category) ->
            dao.insert(
                LostFoundItemEntity(
                    reporterId = reporter,
                    name = name,
                    category = category,
                    location = meta.substringAfter("at ", ""),
                    timePeriod = meta.substringBefore(" at"),
                    status = "found",
                    foundBy = "Sakuni Anupama",
                    contact = "071-1589645",
                )
            )
        }
    }
}
