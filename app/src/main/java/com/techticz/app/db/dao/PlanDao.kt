package com.techticz.app.db.dao

import androidx.room.*
import com.techticz.app.model.food.Food
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.LiveData
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
abstract class PlanDao:BaseDao<DietPlan>(){

    /*@Query("SELECT * FROM DietPlan")
    abstract fun getAll(): List<DietPlan>

    @Query("SELECT * FROM DietPlan WHERE id LIKE  :id")
    abstract fun getById(id: String): Single<DietPlan>
*/

}