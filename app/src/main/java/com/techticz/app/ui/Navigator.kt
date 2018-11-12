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

    fun navigateToLoginActivity(activity:BaseDIActivity) {
        activity.startActivityForResult(Intent(activity, LoginActivity::class.java), 11)
    }

    fun startUserProfileScreen() {
        var intent = Intent(context, UserProfileActivity::class.java)
        context.startActivity(intent)
    }
}