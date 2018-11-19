package com.techticz.app.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class BrowseRecipeResponse : CommonResponse() {
    @SerializedName("recipes")
    @Expose
    var recipes: List<Recipe> = ArrayList()
        get() = field

}