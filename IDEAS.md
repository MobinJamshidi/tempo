# Tempo — Ideas & Roadmap

A focused study companion for Android and iOS (Compose Multiplatform).
This file tracks every planned feature. Nothing here is dropped — it's ordered by what must be built first.

---

## Core (the foundation — everything else builds on this)

- [ ] Auth — login, sign up, forgot password ✅ (done)
- [ ] Bottom navigation + app skeleton (Tasks / Study / Profile)
- [ ] Personal tasks — create, edit, complete, delete (with Supabase database)
- [ ] Calendar strip inside Tasks (week days, tap a day to see its tasks)
- [ ] Study timer — start/stop, log time with date & subject
- [ ] Basic study statistics

## Social & study

- [ ] Add friends
- [ ] See who is studying right now (friends)
- [ ] Global Study — see everyone (not just friends) currently studying
- [ ] Live study room (shared timer / "virtual library")
- [ ] Leaderboard — rank by weekly study time (inside Study tab)

## Motivation & insight

- [ ] Study Heatmap — GitHub-style green squares for daily study activity
- [ ] Streaks + Streak Freeze (a "rest day" that doesn't break the streak)
- [ ] Weekly goal + celebration animation & badge when reached
- [ ] Compare with last week ("+2h more than last week 📈")
- [ ] "Your best study hour" — detect peak focus times and suggest when to study
- [ ] Session goal timer — set an intention ("finish chapter 3"), asked at the end if achieved
- [ ] Pomodoro mode (25/5) with ambient sounds (rain, cafe, forest)
- [ ] Badges & achievements

## Focus Together (platform-specific — expect/actual)

Two user-selectable modes:
- [ ] Lock mode — user picks apps to block during a study session; notifications from them hidden
- [ ] Awareness mode — nothing is blocked, but if the user opens another app, it's shown (to themselves / the room) which app and for how long

## Study DNA / Study Personality (built last, once enough data exists)

A detailed profile generated from the user's history:
- [ ] Average session length (short bursts vs long marathons)
- [ ] Peak focus hours
- [ ] Consistency (daily vs irregular)
- [ ] Favorite subject / where most time goes
- [ ] A named "personality" (e.g. "Morning Sprinter", "Night Marathoner") with a chart and explanation

## Profile

- [ ] Public profile with overall stats, streak, badges
- [ ] Settings (theme, account)

## Deferred / future

- [ ] Google sign in (OAuth)
- [ ] Email confirmation (real verification flow)
- [ ] Immersive fullscreen on splash (hide status bar)
- [ ] `profiles` table for usernames (proper version)
- [ ] Teams / Projects (task assignment, time tracking, kanban) — separate big feature, only if it ever fits

---

## Build principle

Build the **core first**, then stack the exciting features on top one by one.
Most features here depend on study/task data existing — so the timer and tasks come before the heatmap, comparisons, and Study DNA.
