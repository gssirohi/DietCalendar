package com.techticz.app.constants

import com.techticz.app.model.User
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.Food
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 6/10/18.
 */
enum class Meals(val id: String, val mealName:String) {
    EARLY_MORNING("EM","Early Morning"),
    BREAKFAST("BF","Breakfast"),
    LUNCH("LN","Lunch"),
    BRUNCH("BR","Brunch"),
    DINNER("DN","Dinner"),
    BED_TIME("BD","Bed Time")
}