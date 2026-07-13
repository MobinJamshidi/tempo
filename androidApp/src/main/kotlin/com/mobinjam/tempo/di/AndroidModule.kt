package com.mobinjam.tempo.di

import com.mobinjam.tempo.core.notification.Notifier
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { Notifier(androidContext()) }
}