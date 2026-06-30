# Tempo

A study companion for Android and iOS. Plan your tasks, lay them out on a calendar, and run a study timer — on your own or together with friends in a live study room. Track how much you study over time, keep your streak going, and compare progress with friends.

Built with Kotlin Multiplatform and Compose Multiplatform, sharing a single codebase across platforms.

[![CI](https://github.com/mobinjam/tempo/actions/workflows/ci.yml/badge.svg)](https://github.com/mobinjam/tempo/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)

> iOS targets are wired up at the code level (`expect`/`actual`, `iosMain`). Distribution currently focuses on Android.

## Features

**Core**
- Email/password and Google sign-in
- Personal tasks with due dates, priorities and categories
- Monthly / weekly calendar
- Study timer with per-subject tracking

**Social & study**
- Add friends and create groups
- Live study rooms — see who's studying right now
- Study statistics and charts
- Daily goals, streaks and leaderboards

The full plan is in [ROADMAP.md](ROADMAP.md).

## Screenshots

_Coming soon — screens are being built phase by phase._

## Tech stack

- Compose Multiplatform — UI
- Kotlin Multiplatform — shared logic
- Clean Architecture + MVVM
- Koin — dependency injection
- Coroutines & Flow — async
- Ktor — networking
- SQLDelight — local cache
- Supabase — backend

## Architecture

Code is split by platform first (source sets), then by responsibility inside the shared module:

```
shared/src/commonMain/      shared code — most of the app
  core/
    domain/                 entities, use cases, repository interfaces
    data/                   repository implementations, network, cache
    designsystem/           theme and reusable composables
    navigation/             route definitions
  feature/                  auth, tasks, calendar, study, friends, group
shared/src/androidMain/     Android-specific actuals
shared/src/iosMain/         iOS-specific actuals
androidApp/                 Android entry point
iosApp/                     iOS (Xcode) entry point
```

## Getting started

Requirements: Android Studio with the Kotlin Multiplatform plugin, JDK 17+.

```bash
git clone https://github.com/mobinjam/tempo.git
cd tempo
./gradlew :androidApp:assembleDebug
```

Then open the project in Android Studio and run the `androidApp` configuration.

## License

MIT — see [LICENSE](LICENSE).
