package com.mr3y.poodle

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@HiltAndroidApp
class PoodleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            setUpNapierLogger()
            setUpStrictMode()
        }
    }

    private fun setUpNapierLogger() {
        Napier.base(DebugAntilog())
    }

    private fun setUpStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().build())
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectCleartextNetwork()
                .penaltyDeath()
                .build()
        )
    }
}
