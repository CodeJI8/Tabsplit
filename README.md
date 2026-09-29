# Tabsplit

A premium offline group expense splitter for Android, built with Kotlin and Jetpack Compose (Material 3).

Tabsplit helps you split expenses with friends and family — calculating who owes whom without ever processing payments.

## Features

- **Group Management**: Create groups with custom icons, themes, and currencies
- **Smart Splitting**: Equal, exact amount, percentage, and share-based splits
- **Fair Rounding**: Distributes leftover cents fairly — no lost pennies
- **Minimal Settlements**: Greedy algorithm minimizes the number of payments needed
- **Beautiful UI**: Premium fintech-inspired design with gradient cards, animated numbers, and smooth transitions
- **Dark Mode**: Full light, dark, and system theme support
- **Backup & Restore**: Export/import data as JSON via system file picker
- **Share Summaries**: Share group expense summaries as plain text
- **Demo Data**: Ships with realistic sample data to showcase all features
- **Zero Permissions**: No internet, contacts, storage, notification, or any other permissions

## Build & Run

### Requirements
- Android Studio Ladybug or newer
- JDK 17+
- Android SDK with API 37 (or compileSdk target)
- Min SDK: 24 (Android 7.0)

### Steps
```bash
git clone <repo-url>
cd Tabsplit
./gradlew assembleDebug
```

Install on device/emulator:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Design System

| Token | Light | Dark |
|-------|-------|------|
| Background | #F6F4FF | #0F0D1E |
| Surface | #FFFFFF | #1B1830 |
| Primary | #6C4DFF → #B44DFF gradient | Same |
| Accent Mint (owed to you) | #2DD4A7 | #3EE6B8 |
| Accent Coral (you owe) | #FF6B81 | #FF8598 |
| Text | #1A1533 | #F6F4FF |
| Secondary Text | #7A7594 | #A19DB3 |

**Typography**: Plus Jakarta Sans (bundled) — Bold for amounts (32-44sp tabular figures), Medium for titles, Light for secondary text.

**Shapes**: Cards 24dp radius, buttons fully rounded, bottom sheets 32dp top radius.

**Spacing**: 4/8/16/24/32dp grid, 20dp screen side padding.

## Architecture

- **MVVM** with Kotlin Coroutines and StateFlow
- **Room** database for local persistence
- **Navigation Compose** for single-activity navigation
- **Manual DI** via Application class (no Hilt/Dagger)
- **Money stored as Long** (minor units / cents) to avoid floating point errors

## License

MIT
