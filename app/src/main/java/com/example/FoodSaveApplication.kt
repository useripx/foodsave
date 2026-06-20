package com.example

import android.app.Application
import com.example.data.FoodDatabase
import com.example.data.FoodRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FoodSaveApplication : Application() {
    private val database by lazy { FoodDatabase.getDatabase(this) }
    val repository by lazy { FoodRepository(database.foodDao()) }

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // Pre-populate data on start if database is empty
        applicationScope.launch {
            repository.checkAndSeedDatabase()
        }
    }
}
