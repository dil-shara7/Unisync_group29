# UniSync — Campus Life Management Mobile Application

> **Bringing every corner of campus life into one app.**

UniSync is a native Android mobile application developed as part of the Mobile Application Development module (BIT Hons in Networking & Mobile Computing). It consolidates academic scheduling, study notes, a campus marketplace, and lost & found reporting into a single, student-focused platform.

---

## 👥 Team

| Student ID | Name |
|---|---|
| ITBNM-2313-0015 | A.A.M Dilshara Dias |
| ITBNM-2313-0058 | R.G Malsha Prabodinee |

**Group:** 29 | **Program:** BIT(Hons) in Networking & Mobile Computing | **Faculty:** Faculty of IT - NMC

---

## 🎯 SDG Alignment

**SDG 4 – Quality Education**
UniSync supports quality education by helping students stay organized with their academic schedule, study notes, reminders, and shared learning resources — all from one mobile interface.

---

## ✨ Features

| Module | Description |
|---|---|
| 🔐 Authentication | Register & login with email/password. Passwords hashed using PBKDF2WithHmacSHA256 with 16-byte salt |
| 🏠 Home Dashboard | Central hub with cards for Schedule, Notes, Marketplace, and Lost & Found |
| 📅 Schedule Manager | Create, view, edit and delete class/exam/study events with calendar and timetable views |
| 🔔 Reminders | Add and manage personal academic reminders |
| 📝 Study Notes | Upload, browse, bookmark, share and delete study notes with image attachments |
| 🛒 Campus Marketplace | Browse categories, post listings, search items, add to cart and checkout |
| 🔍 Lost & Found | Report lost/found items by category and location, search and filter reports |
| 👤 Profile | View student details and logout |

---

## 🛠️ Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | XML layouts, ConstraintLayout, Material Components |
| Database | Room Persistence Library (`unisync.db`) |
| Async | Kotlin Coroutines (`Dispatchers.IO / Default`) |
| Session | SharedPreferences via `SessionManager` |
| Security | PBKDF2WithHmacSHA256 — 100,000 iterations, 16-byte salt |
| Build | Gradle Kotlin DSL, KSP, Android Gradle Plugin |
| SDK | minSdk 24 · targetSdk 36 · compileSdk 36 |
| Package | `com.example.fittracker` |

---

## 📱 Screens

Splash · Login · Sign Up · Home · Profile · Schedule Manager · Schedule Form · Calendar View · Timetable List · Study Notes Hub · Upload/Edit Note · Note Detail · Marketplace · Category Products · Marketplace Detail · Add/Edit Item · Cart · Checkout · Lost & Found Landing · Lost/Found Category List · Lost/Found Items · Lost/Found Report

---

## 🎨 Design

- **Design Tool:** Figma → [View Prototype](https://www.figma.com)
- **Primary Color:** `#B073AA` (Purple)
- **Fonts:** Joti One · Josefin Slab · Jomolhari
- **Theme:** Soft mauve/purple academic palette

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android device or emulator running API 24+

### Installation
```bash
# Clone the repository
git clone https://github.com/dil-shara7/Unisync_group29.git

# Open in Android Studio
File → Open → Select the cloned folder

# Build & Run
Click ▶ Run or press Shift + F10
```

The app seeds demo data on first launch via `DatabaseSeeder`.

---

## 📂 Project Structure

```
app/src/main/
├── kotlin+java/com/example/fittracker/
│   ├── data/               # Room entities, DAOs, AppDatabase
│   ├── AddItemActivity.kt
│   ├── CalendarAdapter.kt
│   ├── CategoryProductsActivity.kt
│   └── ...                 # All feature activities & adapters
├── res/
│   ├── layout/             # XML screen layouts
│   ├── drawable/           # Logo, icons, shapes
│   ├── values/             # colors.xml, strings.xml, styles.xml
│   └── mipmap-*/           # Launcher icons
└── AndroidManifest.xml
```

---

## 🧪 Testing

16 test cases covering Registration, Login, Navigation, Schedule, Notes, Marketplace, Cart, Checkout, Lost & Found, and Logout — all passing. See the project report for the full test case table.

---

## 📦 Downloads

| Resource | Link |
|---|---|
| 📥 APK | [Download APK](https://drive.google.com/file/d/1Qg-Dr97OgTvseUTH3rdXMQDlfU3uRLr1/view?usp=sharing) |
| 🎥 Video Demo | [Watch Demo](https://drive.google.com/file/d/1wCw7G-WnLPYAprKv9xLdGWipoSMIGj7z/view?usp=sharing) |
| 🎨 Figma Design | [View Prototype](https://www.figma.com) |

---

## ⚠️ Limitations

- No cloud/backend storage (local Room DB only)
- No real push notifications
- No payment gateway integration
- No admin moderation for Lost & Found
- No email verification

---

## 📄 License

This project was developed for academic purposes as part of a university module assessment.
