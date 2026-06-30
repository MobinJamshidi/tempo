# Roadmap

The project is built in three phases. Each phase maps to a GitHub Milestone, and each item below maps to one or more Issues.

## Phase 1 — MVP (personal core)

A fully working single-user app, no social features yet.

- [ ] Project bootstrap — Compose Multiplatform skeleton, builds on Android
- [ ] Authentication — sign up & sign in (email/password + Google), session handling, sign out
- [ ] Task management — create, edit, delete, complete tasks
- [ ] Task details — title, description, due date, priority, category
- [ ] Calendar — monthly/weekly view, tasks shown on their dates
- [ ] Study timer (basic) — start/stop, pick a subject, log time with date
- [ ] Settings — light/dark theme, basic profile
- [ ] Offline-first persistence — local storage + backend sync

## Phase 2 — social & study

- [ ] Add friend — search users, send/accept requests, friends list
- [ ] Groups — create group, invite members, manage roles
- [ ] Live study room — see friends who are currently studying
- [ ] Study statistics — daily/weekly/monthly charts, breakdown by subject
- [ ] Streaks & daily goals — set a goal, count consecutive days
- [ ] Leaderboard — rank friends/group members by study time

## Phase 3 — engagement & polish

- [ ] Notifications — task reminders, calendar reminders, friend requests
- [ ] Badges & achievements
- [ ] Group challenges — shared goals with collective progress
- [ ] Pomodoro mode — 25/5 focus timer
- [ ] Public profile — overall stats, badges, streak
- [ ] Activity feed

## Engineering quality (ongoing)

- [ ] Clean Architecture + MVVM
- [ ] Shared code in commonMain, expect/actual only where needed
- [ ] Dependency injection with Koin
- [ ] Cross-platform navigation
- [ ] State management with Coroutines & Flow
- [ ] Unit tests for the domain layer
- [ ] Continuous integration with GitHub Actions
