package com.example.ananke

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.*
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class AnankeApplication : Application() {
    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
        super.onCreate()
    }
}