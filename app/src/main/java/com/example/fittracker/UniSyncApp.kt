package com.example.fittracker

import android.app.Application
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UniSyncApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Build DB and seed sample data on a background thread; never block onCreate.
        appScope.launch {
            DatabaseSeeder.seedIfEmpty(AppDatabase.get(this@UniSyncApp))
        }
    }

    companion object {
        lateinit var instance: UniSyncApp
            private set
    }
}
