package com.techticz.app.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.model.dietplan.DayPlan
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.model.mealplate.RecipeItem
import java.util.ArrayList

class FoodTypeConverters: BaseTypeConverters(){

    @TypeConverter
    fun listToString(model:List<String>?):String?{
        if(model == null) return null
        return model?.joinToString()
    }

    @TypeConverter
    fun stringToList(dbvalue:String?):List<String>?{
        var separater = ", "
        return dbvalue?.split(separater)
    }

    @TypeConverter
    fun foodItemlistToString(model:List<FoodItem>?):String?{
        return Gson().toJson(model)
    }

    @TypeConverter
    fun stringToFoodItemList(dbvalue:String?):List<FoodItem>?{
        val listType = object : TypeToken<List<FoodItem>>() {
        }.type
        return Gson().fromJson(dbvalue,listType)
    }

    @TypeConverter
    fun recipeItemlistToString(model:List<RecipeItem>?):String?{
        return Gson().toJson(model)
    }



    @TypeConverter
    fun stringToRecipeItemList(dbvalue:String?):List<RecipeItem>?{
        val listType = object : TypeToken<List<RecipeItem>>() {
        }.type
        return Gson().fromJson(dbvalue,listType)
    }

    @TypeConverter
    fun dayplanToString(model:DayPlan?):String?{
        return Gson().toJson(model)
    }

    @TypeConverter
    fun stringToDayPlan(dbvalue:String?): DayPlan?{
        val listType = object : TypeToken<DayPlan?>() {
        }.type
        return Gson().fromJson(dbvalue,listType)
    }
}