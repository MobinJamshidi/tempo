package com.mobinjam.tempo.di

import com.mobinjam.tempo.feature.auth.data.SupabaseAuthRepository
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import com.mobinjam.tempo.feature.auth.presentation.ForgotPasswordViewModel
import com.mobinjam.tempo.feature.auth.presentation.LoginViewModel
import com.mobinjam.tempo.feature.auth.presentation.SignUpViewModel
import com.mobinjam.tempo.feature.main.StudyLauncher
import com.mobinjam.tempo.feature.settings.data.SupabaseSettingsRepository
import com.mobinjam.tempo.feature.settings.domain.SettingsRepository
import com.mobinjam.tempo.feature.tasks.data.SupabaseTaskRepository
import com.mobinjam.tempo.feature.tasks.domain.TaskRepository
import com.mobinjam.tempo.feature.tasks.presentation.TasksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.mobinjam.tempo.feature.splash.SplashViewModel
import com.mobinjam.tempo.feature.study.data.SupabaseStudyRepository
import com.mobinjam.tempo.feature.study.domain.StudyRepository
import com.mobinjam.tempo.feature.study.presentation.StudyViewModel

val appModule = module {

    // Auth
    single<AuthRepository> { SupabaseAuthRepository() }
    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }

    // Tasks
    single<TaskRepository> { SupabaseTaskRepository() }
    viewModel { TasksViewModel(get()) }

    viewModel { SplashViewModel(get()) }
    single<StudyRepository> { SupabaseStudyRepository() }
    viewModel { StudyViewModel(get(), get(), get()) }
    single<SettingsRepository> { SupabaseSettingsRepository() }
    viewModel { StudyLauncher() }
}