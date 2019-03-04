package com.techticz.app.db.dao

import androidx.room.*
import com.techticz.app.model.food.Food
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.LiveData
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
abstract class PlateDao:BaseDao<MealPlate>(){

    @Query("SELECT * FROM MealPlate WHERE english LIKE '%' || :name  || '%'")
    abstract fun getByName(name: String): Single<List<MealPlate>>

    @Query("SELECT * FROM MealPlate WHERE category LIKE :category")
    abstract fun getByCategory(category: String): Single<List<MealPlate>>

    @Query("SELECT * FROM MealPlate")
    abstract fun getAll(): List<MealPlate>

    @Query("SELECT * FROM MealPlate WHERE id LIKE  :id")
    abstract fun getById(id: String): Single<MealPlate>


}