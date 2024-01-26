package com.example.android.politicalpreparedness

import android.app.Application
import timber.log.Timber

class MyApp : Application() {

    lateinit var appContainer : AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(applicationContext)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // Release mode logging
//            Timber.plant(ReleaseTree())
        }
    }
}