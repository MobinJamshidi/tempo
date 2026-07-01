package com.mobinjam.tempo.di

import com.mobinjam.tempo.feature.auth.data.SupabaseAuthRepository
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import com.mobinjam.tempo.feature.auth.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<AuthRepository> { SupabaseAuthRepository() }

    viewModel { LoginViewModel(get()) }
}