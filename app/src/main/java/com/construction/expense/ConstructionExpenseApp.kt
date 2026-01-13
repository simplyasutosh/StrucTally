package com.construction.expense

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for StrucTally
 *
 * This class serves as the entry point for the entire application and handles:
 * - Dependency injection initialization (Hilt)
 * - Global app-wide configurations
 * - Logging setup
 * - Crash reporting initialization (future)
 */
@HiltAndroidApp
class ConstructionExpenseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("StrucTally App initialized in DEBUG mode")
        } else {
            // In production, you would plant a custom tree that logs to Crashlytics or similar
            Timber.plant(ReleaseTree())
            Timber.d("StrucTally App initialized in RELEASE mode")
        }

        Timber.d("App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
    }

    /**
     * Custom Timber tree for release builds
     * Only logs WARNING, ERROR, and WTF levels in production
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == android.util.Log.VERBOSE || priority == android.util.Log.DEBUG || priority == android.util.Log.INFO) {
                return
            }

            // Log to crash reporting service (e.g., Firebase Crashlytics)
            // TODO: Implement crash reporting in production
        }
    }
}
