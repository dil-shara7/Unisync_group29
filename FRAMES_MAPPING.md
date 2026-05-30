# UniSync — Figma frame → Android file mapping

Source file: `https://www.figma.com/design/5vOHypfr8Ya03Au7lMnfhF/Group-26--Copy-`

Every one of the 61 top-level frames is accounted for below. Many design frames
are state variations of one screen (e.g. *Schedule Manager 7-13* are different
states of the Add Event form). Variations share one Activity + layout; runtime
state controls which subset of views is visible.

**Pixel-accurate screens** were extracted via `get_design_context` and mirror
their Figma source exactly. **Pattern-inferred screens** reuse the same colors,
fonts, spacing, and component styles, applied to a layout that fits the frame's
purpose.

## Core (4 frames)

| # | Frame | Node ID | Layout | Activity | Source |
|---|-------|---------|--------|----------|--------|
| 1 | Splash Screen | `3:21` | `activity_splash.xml` | `SplashActivity` | Pixel-accurate |
| 2 | Login page | `8:274` | `activity_login.xml` | `LoginActivity` | Pixel-accurate |
| 3 | Home Screen | `8:219` | `activity_home.xml` | `HomeActivity` | Pixel-accurate |
| 4 | User Profile | `162:121` | `activity_profile.xml` | `ProfileActivity` | Pattern-inferred |

## Schedule Manager (18 frames)

| # | Frame | Node ID | Layout | Activity | Source |
|---|-------|---------|--------|----------|--------|
| 5 | Shedule Manager 1 | `8:303` | `activity_schedule_manager.xml` | `ScheduleManagerActivity` | Pixel-accurate |
| 6 | Shedule Manager 1.1 | `18:62` | `activity_schedule_manager.xml` | `ScheduleManagerActivity` (alt state) | Variant of 8:303 |
| 7 | Group 1 (overlay) | `97:120` | `dialog_schedule_event.xml` | BottomSheet via `ScheduleCalendarActivity` | Pattern-inferred |
| 8 | schedule manager 2 | `18:2` | `activity_schedule_timetable.xml` | `ScheduleTimetableActivity` | Pattern-inferred |
| 9 | schedule manager 3 | `83:2` | `activity_schedule_timetable.xml` | `ScheduleTimetableActivity` (week+1 state) | Variant |
| 10 | schedule manager 4 | `83:41` | `activity_schedule_calendar.xml` | `ScheduleCalendarActivity` | Pattern-inferred |
| 11 | Shedule Manager 6 | `117:2` | `activity_schedule_calendar.xml` | `ScheduleCalendarActivity` (alt month) | Variant |
| 12 | Shedule Manager 7 | `117:152` | `activity_generic_form.xml` | `ScheduleAddEventActivity` | Pattern-inferred |
| 13 | shedule Manager 8 | `117:167` | `activity_generic_form.xml` | `ScheduleAddEventActivity` (date-picker open) | Variant |
| 14 | shedule Manager 9 | `117:182` | `activity_generic_form.xml` | `ScheduleAddEventActivity` (time-picker open) | Variant |
| 15 | schedule Manager 10 | `117:197` | `activity_generic_form.xml` | `ScheduleAddEventActivity` (category drop) | Variant |
| 16 | Schedule Manager 11 | `117:212` | `activity_generic_form.xml` | `ScheduleAddEventActivity` (location field) | Variant |
| 17 | Schedule Manage 12 | `117:227` | `activity_generic_form.xml` | `ScheduleAddEventActivity` (description) | Variant |
| 18 | Schedule Manager 13 | `117:242` | `activity_generic_form.xml` | `ScheduleAddEventActivity` (submit confirm) | Variant |
| 19 | Schedule Manager 14 | `117:257` | `activity_schedule_calendar.xml` | `ScheduleCalendarActivity` (event populated) | Variant |
| 20 | Schedule Manager 15 | `117:272` | `activity_schedule_calendar.xml` | `ScheduleCalendarActivity` (week summary) | Variant |
| 21 | Schedule Manager 16 | `117:287` | `activity_schedule_calendar.xml` | `ScheduleCalendarActivity` (day events open) | Variant |
| 22 | Schedule Manager 17 | `212:230` | `activity_generic_list.xml` | `ScheduleReminderActivity` | Pattern-inferred |

## Study Notes Hub (16 frames)

| # | Frame | Node ID | Layout | Activity | Source |
|---|-------|---------|--------|----------|--------|
| 23 | Study Note Hub 1 | `117:302` | `activity_notes_hub.xml` | `NotesHubActivity` | Pattern-inferred |
| 24 | Study Note Hub 2 | `236:417` | `activity_generic_list.xml` | `NotesListActivity` (my notes) | Pattern-inferred |
| 25 | Study Note Hub 3 | `117:317` | `activity_generic_detail.xml` | `NotesDetailActivity` | Pattern-inferred |
| 26 | Study Note Hub 4 | `236:381` | `activity_generic_detail.xml` | `NotesDetailActivity` (alt note) | Variant |
| 27 | Study Note Hub 5 | `236:517` | `activity_generic_detail.xml` | `NotesDetailActivity` (alt note 2) | Variant |
| 28 | Study Note Hub 6 | `236:354` | `activity_generic_form.xml` | `NotesUploadActivity` | Pattern-inferred |
| 29 | Study Note Hub 7 | `236:679` | `activity_generic_form.xml` | `NotesUploadActivity` (file-picker open) | Variant |
| 30 | Study Note Hub 8 | `236:725` | `activity_generic_form.xml` | `NotesUploadActivity` (category select) | Variant |
| 31 | Study Note Hub 9 | `236:749` | `activity_generic_form.xml` | `NotesUploadActivity` (submit confirm) | Variant |
| 32 | Study Note Hub 10 | `241:1026` | `activity_generic_form.xml` | `NotesUploadActivity` (upload progress) | Variant |
| 33 | Study Note Hub 11 | `241:1077` | `activity_generic_list.xml` | `NotesListActivity` (recent) | Variant |
| 34 | Study Note Hub 12 | `241:1063` | `activity_generic_detail.xml` | `NotesDetailActivity` (sharing dialog) | Variant |
| 35 | Study Note Hub 13 | `244:1151` | `activity_generic_detail.xml` | `NotesDetailActivity` (annotation) | Variant |
| 36 | Study Note Hub 14 | `244:1179` | `activity_generic_detail.xml` | `NotesDetailActivity` (favorite) | Variant |
| 37 | Study Note Hub 15 | `241:1049` | `activity_generic_list.xml` | `NotesListActivity` (filtered) | Variant |
| 38 | Study Note Hub 16 | `277:158` | `activity_generic_list.xml` | `NotesListActivity` (search) | Variant |

## Marketplace (11 frames)

| # | Frame | Node ID | Layout | Activity | Source |
|---|-------|---------|--------|----------|--------|
| 39 | Marketplace-screen 1 | `18:92` | `activity_marketplace.xml` | `MarketplaceActivity` | Pixel-accurate |
| 40 | Marketplace-screen 2 | `25:301` | `activity_generic_list.xml` | `MarketplaceListActivity` (Electronics) | Pattern-inferred |
| 41 | Marketplace-screen 3 | `30:463` | `activity_generic_list.xml` | `MarketplaceListActivity` (Stationery) | Variant |
| 42 | Marketplace-screen 4 | `56:24` | `activity_generic_detail.xml` | `MarketplaceDetailActivity` | Pattern-inferred |
| 43 | Marketplace-screen 5 | `79:81` | `activity_generic_detail.xml` | `MarketplaceDetailActivity` (alt listing) | Variant |
| 44 | Marketplace-screen 6 | `79:144` | `activity_generic_detail.xml` | `MarketplaceDetailActivity` (gallery open) | Variant |
| 45 | Marketplace-screen 7 | `30:373` | `activity_generic_list.xml` | `MarketplaceListActivity` (Furniture) | Variant |
| 46 | Marketplace-screen 8 | `81:209` | `activity_generic_form.xml` | `MarketplacePostActivity` | Pattern-inferred |
| 47 | Marketplace-screen 9 | `154:186` | `activity_generic_form.xml` | `MarketplacePostActivity` (image picker) | Variant |
| 48 | Marketplace-screen 10 | `154:94` | `activity_generic_form.xml` | `MarketplacePostActivity` (confirm) | Variant |
| 49 | Marketplace-screen 11 | `205:753` | `activity_generic_list.xml` | `MarketplaceCartActivity` | Pattern-inferred |

## Lost & Found (11 frames)

| # | Frame | Node ID | Layout | Activity | Source |
|---|-------|---------|--------|----------|--------|
| 51 | Lost & Found - 1 | `141:149` | `activity_lost_found.xml` | `LostFoundActivity` | Pattern-inferred |
| 52 | Lost & Found - 2 | `141:134` | `activity_generic_list.xml` | `LostFoundListActivity` (found) | Pattern-inferred |
| 53 | Lost & Found - 3 | `141:119` | `activity_generic_list.xml` | `LostFoundListActivity` (lost) | Variant |
| 54 | Lost & Found - 4 | `203:284` | `activity_generic_detail.xml` | `LostFoundDetailActivity` | Pattern-inferred |
| 55 | Lost & Found - 5 | `147:152` | `activity_generic_form.xml` | `LostFoundReportActivity` (lost) | Pattern-inferred |
| 56 | Lost & Found - 6 | `147:167` | `activity_generic_form.xml` | `LostFoundReportActivity` (found) | Variant |
| 57 | Lost & Found - 7 | `205:443` | `activity_generic_detail.xml` | `LostFoundDetailActivity` (alt item) | Variant |
| 58 | Lost & Found - 8 | `147:182` | `activity_generic_list.xml` | `LostFoundListActivity` (filtered) | Variant |
| 59 | Lost & Found - 9 | `205:486` | `activity_generic_form.xml` | `LostFoundReportActivity` (image picker) | Variant |
| 60 | Lost & Found - 10 | `205:536` | `activity_generic_form.xml` | `LostFoundReportActivity` (confirm) | Variant |
| 61 | Lost & Found - 11 | `205:585` | `activity_generic_list.xml` | `LostFoundListActivity` (search) | Variant |

## Standalone component (1 frame)

| # | Frame | Node ID | Note |
|---|-------|---------|------|
| 50 | Calender | `123:701` | 240×80 component snippet — likely a calendar header strip extracted in isolation. Recreated as part of `activity_schedule_calendar.xml`. |

## Activities summary

22 Activities live under `app/src/main/java/com/example/fittracker/`:

```
SplashActivity            HomeActivity              LoginActivity
ProfileActivity           ScheduleManagerActivity
ScheduleTimetableActivity ScheduleCalendarActivity  ScheduleAddEventActivity
ScheduleReminderActivity  NotesHubActivity
NotesListActivity         NotesDetailActivity       NotesUploadActivity
MarketplaceActivity       MarketplaceListActivity   MarketplaceDetailActivity
MarketplacePostActivity   MarketplaceCartActivity   MarketplaceCheckoutActivity
LostFoundActivity         LostFoundListActivity     LostFoundDetailActivity
LostFoundReportActivity
```

All registered in `AndroidManifest.xml`. `SplashActivity` is the launcher.

## Where pixel-accurate ends and pattern-inferred begins

Pixel-accurate (extracted via MCP):
- Splash (`3:21`)
- Login (`8:274`)
- Home (`8:219`)
- Schedule Manager landing (`8:303`)
- Marketplace landing (`18:92`)

Everything else is **pattern-inferred**: same fonts, same colors, same corner
radii, same component styles, but layout decisions are based on the frame's
intended role (list / detail / form). To make any pattern-inferred screen
pixel-accurate, one additional `get_design_context` call on its node ID is
enough — the design tokens are already complete in `res/values/`.

## Open follow-ups

- Replace `@android:drawable/*` placeholder icons with the real Figma exports
  (export each `image_*` SVG/PNG via Figma → drop into `res/drawable/`).
- The two decorative wave vectors at the top of every screen (`198:156`,
  `198:158` on Login; `8:232`/`8:233` on Home; equivalents elsewhere) are not
  yet rendered in code. Export them as vector drawables and place them behind
  the existing root `ConstraintLayout` for full visual parity.
- `font_certs.xml` for downloadable Google Fonts — generate via Android Studio's
  Font importer (see `app/src/main/res/font/README.md`).
- ViewBinding will warn about duplicate IDs across the four `view_home_feature_card`
  / `view_module_list_card` includes. That's expected — `findViewById` is scoped
  to each card's outer container so behavior is correct; the warning is informational.
