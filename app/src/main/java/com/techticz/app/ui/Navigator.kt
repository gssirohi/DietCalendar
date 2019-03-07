package com.techticz.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.supercilex.poiandroid.demo.ToolActivity
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.activity.*
import javax.inject.Inject
import org.parceler.Parcels
import com.techticz.auth.LoginActivity
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.ui.activity.DietPlanActivity
import javax.inject.Named


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 27/9/18.
 */
class Navigator @Inject constructor(val context: Context)  {
    init {
        Log.d("DI","providing navigator")
    }
fun startDashBoard(){
    var intent = Intent(context,DashboardActivity::class.java)
    context.startActivity(intent)
}
    fun startFoodDetails(){
        var intent = Intent(context,FoodDetailsActivity::class.java)
        context.startActivity(intent)
    }

    fun startDeveloperScreen() {
        var intent = Intent(context, DeveloperActivity::class.java)
        context.startActivity(intent)
    }

    fun startSettingsScreen() {
        var intent = Intent(context, SettingsActivity::class.java)
        context.startActivity(intent)
    }

    fun startBrowsePlanScreen() {
        var intent = Intent(context, BrowseDietPlansActivity::class.java)
        context.startActivity(intent)
    }

    fun startDietChartScreen(activity: Activity, plan: DietPlan?) {
        var intent = Intent(context, DietChartActivity::class.java)
        intent.putExtra("plan", Parcels.wrap<DietPlan>(plan))
        activity.startActivityForResult(intent,1)
    }

    fun startCreatePlanActivity() {
        var intent = Intent(context, DietPlanActivity::class.java)
        context.startActivity(intent)
    }

    fun startCopyPlanActivity(activity: Activity, plan: DietPlan?) {
        var intent = Intent(context, DietPlanActivity::class.java)
        intent.putExtra("plan", Parcels.wrap<DietPlan>(plan))
        intent.putExtra("mode", DietPlanActivity.MODE_COPY_FROM_PLAN)
        activity.startActivityForResult(intent,1)
    }

    fun navigateToLoginActivity(activity:BaseDIActivity) {
        activity.startActivityForResult(Intent(activity, LoginActivity::class.java), 11)
    }

    fun startUserProfileScreen() {
        var intent = Intent(context, UserProfileActivity::class.java)
        context.startActivity(intent)
    }

    fun startOnboarding() {
        var intent = Intent(context, OnboardingActivity::class.java)
        context.startActivity(intent)
    }

    fun startCreatePlateScreen() {
        var intent = Intent(context, MealPlateActivity::class.java)
        intent.putExtra("mode",MealPlateActivity.MODE_NEW)
        context.startActivity(intent)
    }

    fun startCreateRecipeScreen() {
        var intent = Intent(context, RecipeDetailsActivity::class.java)
        intent.putExtra("mode",RecipeDetailsActivity.MODE_NEW)
        context.startActivity(intent)
    }

    fun startBrowseRecipeScreen(activity: Activity,plateId:String?) {
        var intent = Intent(context, BrowseRecipeActivity::class.java)
        intent.putExtra("plateId",plateId)
        activity.startActivityForResult(intent,1)
    }

    fun startBrowseFoodScreen(activity: Activity,plateId:String?,recipeId:String?) {
        var intent = Intent(context, BrowseFoodActivity::class.java)
        intent.putExtra("plateId",plateId)
        intent.putExtra("recipeId",recipeId)
        activity.startActivityForResult(intent,2)
    }

    fun startBrowsePlateScreen(activity: Activity, planId: String,daySection:Int,mealType:String) {
        var intent = Intent(context, BrowsePlateActivity::class.java)
        intent.putExtra("planId",planId)
        intent.putExtra("daySection",daySection)
        intent.putExtra("mealType",mealType)
        activity.startActivityForResult(intent,1)
    }

    fun startExplorePlateScreen(mealPlateId: String?) {
        var intent = Intent(context, MealPlateActivity::class.java)
        intent.putExtra("plateId", mealPlateId)
        intent.putExtra("mode", MealPlateActivity.MODE_EXPLORE)
        context.startActivity(intent)
    }

    fun startExploreRecipeScreen(recipeItem: RecipeItem?) {
        var intent = Intent(context, RecipeDetailsActivity::class.java)
        intent.putExtra("recipeId", recipeItem?.id)
        intent.putExtra("mode", RecipeDetailsActivity.MODE_EXPLORE)
        context.startActivity(intent)
    }

    fun startCopyPlateScreen(activity: Activity,mealPlate: MealPlate?) {
        var intent = Intent(context, MealPlateActivity::class.java)
        intent.putExtra("plate", Parcels.wrap<MealPlate>(mealPlate))
        intent.putExtra("mode", MealPlateActivity.MODE_COPY_FROM_PLATE)
        activity.startActivityForResult(intent,2)
    }

    fun startCopyRecipeScreen(activity: Activity,recipe: Recipe?) {
        var intent = Intent(context, RecipeDetailsActivity::class.java)
        intent.putExtra("recipe", Parcels.wrap<Recipe>(recipe))
        intent.putExtra("mode", RecipeDetailsActivity.MODE_COPY_FROM_RECIPE)
        activity.startActivityForResult(intent,2)
    }

    fun navigateToExcelExplorer() {
        var intent = Intent(context, ToolActivity::class.java)
        context.startActivity(intent)
    }


}