package com.mobinjam.tempo

import android.app.Application
import com.mobinjam.tempo.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TempoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@TempoApplication)
        }
    }
}