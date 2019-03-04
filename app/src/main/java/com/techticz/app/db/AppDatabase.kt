package com.techticz.app.db

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techticz.app.db.converters.FoodTypeConverters
import com.techticz.app.db.dao.FoodDao
import com.techticz.app.db.dao.PlanDao
import com.techticz.app.db.dao.PlateDao
import com.techticz.app.db.dao.RecipeDao
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.Food
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe


@Database(entities = [Food::class, Recipe::class, MealPlate::class,DietPlan::class], version = 1)
@TypeConverters(FoodTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun recipeDao(): RecipeDao
    abstract fun plateDao(): PlateDao
    abstract fun planDao(): PlanDao
    companion object {
        fun getAppDataBase(application: Application): AppDatabase {
            return Room.databaseBuilder(application, AppDatabase::class.java, "dietistDB").build()
        }
    }
}