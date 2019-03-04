package com.techticz.app.constants


import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.Food
import com.techticz.app.model.launch.Launching
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.model.user.User
import java.util.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 6/10/18.
 */
enum class AppCollections(val collectionName: String,val jsonName:String,val className: Class<*>) {
    SYNC_PREFERENCES("syncPreferences","syncPreferences.json", Food::class.java),
    USERS("users","user.json", User::class.java),
    FOODS("foods","food.json",Food::class.java),
    RECIPES("recipes","recipe.json",Recipe::class.java),
    PLATES("plates","plate.json", MealPlate::class.java),
    PLANS("plans","dietplan.json",DietPlan::class.java),
    FOOD_CATEGORIES("foodCategories","foodCategories.json",Food::class.java),
    RECIPE_CATEGORIES("recipeCategories","recipeCategories.json",Recipe::class.java),
    SERVING_TYPES("servingTypes","servingTypes.json",Food::class.java),
    NUTRIENT_UNITS("nutrientUnits","nutrientUnits.json",Food::class.java),
    UNITS("units","units.json",Food::class.java),
    LAUNCHINGS("launchings","launchings.json", Launching::class.java)
}