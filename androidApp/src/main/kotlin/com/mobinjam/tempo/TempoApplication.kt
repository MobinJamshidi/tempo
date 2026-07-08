package com.mobinjam.tempo

import android.app.Application
import com.mobinjam.tempo.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import com.mobinjam.tempo.di.androidModule
import org.koin.core.context.loadKoinModules

class TempoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@TempoApplication)
        }
        loadKoinModules(androidModule)
    }
}