package com.techticz.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.activity.*
import javax.inject.Inject
import org.parceler.Parcels
import com.techticz.auth.LoginActivity
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.ui.activity.DietPlanActivity


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 27/9/18.
 */
class Navigator @Inject constructor(val context: Context)  {
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

    fun startCreatePlateScreen() {
        var intent = Intent(context, MealPlateActivity::class.java)
        intent.putExtra("mode",MealPlateActivity.MODE_NEW)
        context.startActivity(intent)
    }

    fun startBrowseRecipeScreen(activity: Activity) {
        var intent = Intent(context, BrowseRecipeActivity::class.java)
        activity.startActivityForResult(intent,1)
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

    fun startCopyPlateScreen(activity: Activity,mealPlate: MealPlate?) {
        var intent = Intent(context, MealPlateActivity::class.java)
        intent.putExtra("plate", Parcels.wrap<MealPlate>(mealPlate))
        intent.putExtra("mode", MealPlateActivity.MODE_COPY_FROM_PLATE)
        activity.startActivityForResult(intent,2)
    }


}