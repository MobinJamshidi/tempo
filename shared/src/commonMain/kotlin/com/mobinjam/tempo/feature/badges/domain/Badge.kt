package com.mobinjam.tempo.feature.badges.domain

enum class BadgeCategory {
    START, HOURS, STREAK, TIME_OF_DAY, GOAL, DAILY, VARIETY
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val category: BadgeCategory,
)

// badges
val allBadges = listOf(
    // start
    Badge("first_steps", "First Steps", "Complete your first study session", BadgeCategory.START),
    Badge("getting_started", "Getting Started", "Complete 5 study sessions", BadgeCategory.START),

    // total hours
    Badge("rookie", "Rookie", "Study for 1 hour total", BadgeCategory.HOURS),
    Badge("focused", "Focused", "Study for 10 hours total", BadgeCategory.HOURS),
    Badge("dedicated", "Dedicated", "Study for 50 hours total", BadgeCategory.HOURS),
    Badge("scholar", "Scholar", "Study for 100 hours total", BadgeCategory.HOURS),
    Badge("master", "Master", "Study for 250 hours total", BadgeCategory.HOURS),
    Badge("legend", "Legend", "Study for 500 hours total", BadgeCategory.HOURS),

    // streak
    Badge("on_fire", "On Fire", "Reach a 3 day streak", BadgeCategory.STREAK),
    Badge("unstoppable", "Unstoppable", "Reach a 7 day streak", BadgeCategory.STREAK),
    Badge("committed", "Committed", "Reach a 14 day streak", BadgeCategory.STREAK),
    Badge("iron_will", "Iron Will", "Reach a 30 day streak", BadgeCategory.STREAK),
    Badge("centurion", "Centurion", "Reach a 100 day streak", BadgeCategory.STREAK),

    // time of day
    Badge("early_bird", "Early Bird", "Study before 7 AM", BadgeCategory.TIME_OF_DAY),
    Badge("night_owl", "Night Owl", "Study after midnight", BadgeCategory.TIME_OF_DAY),
    Badge("lunch_break", "Lunch Break", "Study around noon", BadgeCategory.TIME_OF_DAY),

    // goal
    Badge("goal_getter", "Goal Getter", "Reach your daily goal for the first time", BadgeCategory.GOAL),
    Badge("overachiever", "Overachiever", "Double your daily goal in one day", BadgeCategory.GOAL),

    // daily
    Badge("marathon", "Marathon", "Study 3 hours in a single day", BadgeCategory.DAILY),
    Badge("deep_focus", "Deep Focus", "Study 2 hours in one session", BadgeCategory.DAILY),

    // variety
    Badge("explorer", "Explorer", "Study in 5 different categories", BadgeCategory.VARIETY),
    Badge("well_rounded", "Well Rounded", "Study in 10 different categories", BadgeCategory.VARIETY),
)