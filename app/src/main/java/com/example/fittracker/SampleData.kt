package com.example.fittracker

/**
 * Hard-coded sample data so demo screens render with content instead of blanks.
 * Replace with Room-backed data once the schema is in place.
 */
data class ListEntry(
    val title: String,
    val subtitle: String,
    val iconRes: Int = android.R.drawable.ic_menu_gallery,
    val imageUri: String? = null,
    val id: Long = -1L,
)

data class Product(
    val name: String,
    val price: String,
    val imageUri: String? = null,
)

data class CartLine(
    val name: String,
    val price: Int,
    var quantity: Int,
)

object SampleData {

    val marketplaceListings = listOf(
        ListEntry("MacBook Air M1 (2020)", "$650 · Excellent condition"),
        ListEntry("Engineering Drawing Set", "$15 · Like new"),
        ListEntry("HP DeskJet Printer", "$45 · Includes 2 cartridges"),
        ListEntry("Reading Lamp", "$10 · Adjustable arm"),
        ListEntry("Calculus textbook (8th ed)", "$20 · Highlighted notes inside"),
        ListEntry("IKEA Desk Chair", "$50 · Pickup only"),
        ListEntry("USB-C Hub 7-in-1", "$22 · Used 3 months"),
        ListEntry("Casio FX-991ES Plus", "$18 · Working"),
    )

    val lostItems = listOf(
        ListEntry("Black backpack", "Lost · Library 2F · Wed 5 Mar"),
        ListEntry("Silver hoop earring", "Lost · Cafeteria · Thu 6 Mar"),
        ListEntry("Blue water bottle", "Lost · Lecture Hall A · Fri 7 Mar"),
        ListEntry("AirPods Pro (case)", "Lost · Gym · Mon 10 Mar"),
    )

    val foundItems = listOf(
        ListEntry("Red colored pencil case", "Found · Block 3 corridor · Tue"),
        ListEntry("Violet mist colored tumbler", "Found · Library entrance"),
        ListEntry("Brown leather wallet", "Found · Bus stop · Wed"),
        ListEntry("Set of keys (3)", "Found · Hostel block · Fri"),
        ListEntry("Calculator (Casio)", "Found · Hall A row 5"),
    )

    val notes = listOf(
        ListEntry("Networking Lecture 4", "Routing protocols · 12 pages"),
        ListEntry("DBMS · Indexing", "Compiled from 3 lectures · 8 pages"),
        ListEntry("Calculus 2 · Series", "Worked examples · 14 pages"),
        ListEntry("OS · Process scheduling", "Tutorial answers · 6 pages"),
        ListEntry("Linear Algebra · Eigenvalues", "Slides + summary · 10 pages"),
        ListEntry("Software Engineering", "Group project plan · 4 pages"),
    )

    val reminders = listOf(
        ListEntry("Submit DBMS assignment", "Tomorrow · 5:00 PM"),
        ListEntry("Networking quiz", "Wed · 9:00 AM"),
        ListEntry("Meet supervisor", "Thu · 2:00 PM · Block 3"),
        ListEntry("Pay hostel fees", "Fri · before 4:00 PM"),
    )

    /** A 7×6 weekly timetable grid (Mon-Sun across, 6 time slots down). */
    val timetableCells: List<TimetableCell> = buildList {
        // Mon
        add(TimetableCell(slot = 0, day = 0, label = "Calculus", color = ColorTag.CLASS))
        add(TimetableCell(slot = 2, day = 0, label = "Networking", color = ColorTag.CLASS))
        // Tue
        add(TimetableCell(slot = 1, day = 1, label = "DBMS Lab", color = ColorTag.STUDY))
        add(TimetableCell(slot = 3, day = 1, label = "OS Lecture", color = ColorTag.CLASS))
        // Wed
        add(TimetableCell(slot = 0, day = 2, label = "Networking Quiz", color = ColorTag.EXAM))
        add(TimetableCell(slot = 4, day = 2, label = "Software Eng", color = ColorTag.CLASS))
        // Thu
        add(TimetableCell(slot = 1, day = 3, label = "Linear Algebra", color = ColorTag.CLASS))
        // Fri
        add(TimetableCell(slot = 2, day = 4, label = "Group Project", color = ColorTag.STUDY))
        add(TimetableCell(slot = 5, day = 4, label = "DBMS Exam", color = ColorTag.EXAM))
    }

    /** Days of the current month that have any event, mapped to event count. */
    val calendarEvents: Map<Int, Int> = mapOf(
        3 to 1, 5 to 2, 8 to 1, 12 to 3, 15 to 1, 18 to 2, 22 to 1, 27 to 2, 29 to 1
    )

    val marketplaceByCategory: Map<String, List<Product>> = mapOf(
        "Lifestyle on Campus" to listOf(
            Product("University Hoodie", "\$50"),
            Product("Sports Shoes", "\$170"),
            Product("leather Backpack", "\$90"),
            Product("Laptop Sticker Pack", "\$30"),
        ),
        "Electronics" to listOf(
            Product("MacBook Air M1 (2020)", "\$650"),
            Product("Scientific Calculator", "\$25"),
            Product("JBL Tune 510BT", "\$50"),
            Product("Wireless Mouse", "\$15"),
            Product("Wireless Charger", "\$150"),
            Product("USB Flash Drive", "\$10"),
        ),
        "Stationary & Accessories" to listOf(
            Product("Notebook Set", "\$20"),
            Product("Highlighter Set", "\$10"),
            Product("Water Bottle", "\$50"),
            Product("Sticky Notes Pad", "\$15"),
            Product("Ballpoint Pen Set", "\$5"),
            Product("Laptop Covers", "\$20"),
        ),
        "Furniture & Hostel Item" to listOf(
            Product("Tub Chair", "\$10"),
            Product("Table Lamp", "\$10"),
            Product("Storage Box", "\$50"),
            Product("Mini Book Shelf", "\$15"),
            Product("Laundry Basket", "\$15"),
            Product("Adjustable Table", "\$20"),
        ),
    )

    /** Shared in-memory cart so Cart → Checkout share the same lines. */
    val cart: MutableList<CartLine> = mutableListOf(
        CartLine("Pencil Case", 10, 1),
        CartLine("Glass Water Bottle", 15, 2),
        CartLine("Anime Key Chain", 5, 1),
        CartLine("Ladies wallet with long strap", 25, 1),
    )

    val lostFoundCategories: List<String> = listOf(
        "Stationary & Accessories",
        "Electronics",
        "Wallets",
        "Keys",
        "Others",
    )

    /** kind="lost" or "found"; category → items. */
    val lostFoundByCategory: Map<Pair<String, String>, MutableList<LostFoundItem>> = mapOf(
        ("lost" to "Stationary & Accessories") to mutableListOf(
            LostFoundItem("Red colored pencil case", "Lost 1 hour ago at Art Room"),
            LostFoundItem("Black colored journal book", "Lost 2 days ago at Auditorium"),
            LostFoundItem("Glass water bottle", "Lost 2 days ago at Cafeteria"),
            LostFoundItem("Anime key-chain", "Lost a 5 days ago at Gate"),
            LostFoundItem("Purple colored pencil case", "Lost 6 days ago at First Floor"),
            LostFoundItem("Green colored steel bottle", "Lost 6 days ago at Auditorium"),
            LostFoundItem("Leather backpack", "Lost a week ago at Cafeteria"),
            LostFoundItem("Violet mist colored tumbler", "Lost a week ago at Gate"),
        ),
        ("lost" to "Electronics") to mutableListOf(
            LostFoundItem("Samsung S24 mobile phone", "Lost 1 day ago at IoT Lab"),
            LostFoundItem("Laptop charger", "Lost 3 days ago at Auditorium"),
            LostFoundItem("Blue stripped smart watch", "Lost 5 days ago at Printing Room"),
            LostFoundItem("JBL headphone set", "Lost 5 days ago at Lecture Room"),
        ),
        ("lost" to "Wallets") to mutableListOf(
            LostFoundItem("Mini card holder - Gents", "Lost 5 hours ago at Cafeteria"),
            LostFoundItem("Ladies brown colored wallet", "Lost 1 day ago at Swimming Pool"),
            LostFoundItem("Gents leather wallet", "Lost 1 day ago at IoT Lab"),
            LostFoundItem("Ladies wallet with long strap", "Lost 2 days ago at Rest Room"),
        ),
        ("lost" to "Keys") to mutableListOf(
            LostFoundItem("House door key", "Lost 8 hours ago at Cafeteria"),
            LostFoundItem("Door key card", "Lost 1 day ago at Playground"),
            LostFoundItem("Bicycle key", "Lost 1 day ago at Gate"),
            LostFoundItem("House door key", "Lost 2 days ago at Lecture Room"),
        ),
        ("lost" to "Others") to mutableListOf(),
        ("found" to "All") to mutableListOf(
            LostFoundItem("Blue stripped smart watch", "Found 4 hours ago at Cafeteria"),
            LostFoundItem("Door key card", "Found 7 hours ago at Playground"),
            LostFoundItem("Red colored pencil case", "Found 30 minutes ago at Art Room"),
            LostFoundItem("Violet mist colored tumbler", "Found 5 days ago at Gate"),
        ),
    )
}

data class LostFoundItem(
    val name: String,
    val meta: String,
    val foundBy: String = "Sakuni Anupama",
    val date: String = "02/01/2026",
    val contact: String = "071-1589645",
    val imageUri: String? = null,
)

data class TimetableCell(
    val slot: Int,     // row 0..5 (time slots: 8am, 10, 12, 2pm, 4, 6)
    val day: Int,      // col 0..6 (Mon..Sun)
    val label: String,
    val color: ColorTag,
)

enum class ColorTag { CLASS, EXAM, STUDY }
