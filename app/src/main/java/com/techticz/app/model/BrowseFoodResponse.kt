package com.techticz.app.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.Food
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class BrowseFoodResponse : CommonResponse() {
    @SerializedName("foods")
    @Expose
    var foods: List<Food> = ArrayList()
        get() = field

}