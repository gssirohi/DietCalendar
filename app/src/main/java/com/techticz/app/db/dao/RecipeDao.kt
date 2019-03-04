package com.techticz.app.db.dao

import androidx.room.*
import com.techticz.app.model.food.Food
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.LiveData
import com.techticz.app.model.recipe.Recipe
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
abstract class RecipeDao:BaseDao<Recipe>(){

    @Query("SELECT * FROM Recipe WHERE english LIKE '%' || :name  || '%'")
    abstract fun getByName(name: String): Single<List<Recipe>>

    @Query("SELECT * FROM Recipe WHERE category LIKE :category")
    abstract fun getByCategory(category: String): Single<List<Recipe>>

    @Query("SELECT * FROM Recipe")
    abstract fun getAll(): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE id LIKE  :id")
    abstract fun getById(id: String): Single<Recipe>


}