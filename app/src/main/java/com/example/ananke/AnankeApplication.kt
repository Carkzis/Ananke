package com.example.ananke

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnankeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}