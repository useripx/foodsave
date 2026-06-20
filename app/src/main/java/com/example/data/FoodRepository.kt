package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class FoodRepository(private val foodDao: FoodDao) {

    val allFoodItems: Flow<List<FoodItem>> = foodDao.getAllFoodItems()

    suspend fun insert(foodItem: FoodItem): Long {
        return foodDao.insertFoodItem(foodItem)
    }

    suspend fun update(foodItem: FoodItem) {
        foodDao.updateFoodItem(foodItem)
    }

    suspend fun delete(foodItem: FoodItem) {
        foodDao.deleteFoodItem(foodItem)
    }

    suspend fun deleteById(id: Int) {
        foodDao.deleteFoodItemById(id)
    }

    // Prepopulate some sample items if database is empty
    suspend fun checkAndSeedDatabase() {
        try {
            val currentItems = allFoodItems.first()
            if (currentItems.isEmpty()) {
                val cal = Calendar.getInstance()
                
                // 1. Susu Cair Segar, Category: Susu & Olahan (Dairy in mockup, we can use Susu & Olahan)
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, 1) // 1 day survival
                foodDao.insertFoodItem(
                    FoodItem(
                        name = "Susu Cair Segar",
                        category = "Susu & Olahan",
                        quantity = 1,
                        quantityUnit = "Liter",
                        expiryDate = cal.timeInMillis
                    )
                )

                // 2. Bayam Organik, Category: Sayuran
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, 3) // 3 days survival
                foodDao.insertFoodItem(
                    FoodItem(
                        name = "Bayam Organik",
                        category = "Sayuran",
                        quantity = 2,
                        quantityUnit = "Bundles",
                        expiryDate = cal.timeInMillis
                    )
                )

                // 3. Apel Merah, Category: Buah
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, 12) // 12 days
                foodDao.insertFoodItem(
                    FoodItem(
                        name = "Apel Merah",
                        category = "Buah",
                        quantity = 5,
                        quantityUnit = "Units",
                        expiryDate = cal.timeInMillis
                    )
                )

                // 4. Telur Ayam, Category: Protein
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, 15) // 15 days
                foodDao.insertFoodItem(
                    FoodItem(
                        name = "Telur Ayam",
                        category = "Protein",
                        quantity = 10,
                        quantityUnit = "pcs",
                        expiryDate = cal.timeInMillis
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
