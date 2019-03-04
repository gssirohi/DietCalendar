package com.techticz.app.db.dao

import androidx.room.*
import com.techticz.app.model.food.Food
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.LiveData
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
abstract class FoodDao:BaseDao<Food>(){
   /* @Query("SELECT * FROM Food WHERE english LIKE '%' || :name  || '%'")
    abstract fun getByName(name: String): Single<List<Food>>*/

}