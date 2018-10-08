package com.techticz.app.ui

import android.content.Context
import android.content.Intent
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.activity.*
import javax.inject.Inject
import android.support.v4.content.ContextCompat.startActivity
import org.parceler.Parcels



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

    fun startDietChartScreen(plan: DietPlan?) {
        var intent = Intent(context, DietChartActivity::class.java)
        intent.putExtra("plan", Parcels.wrap<DietPlan>(plan))
        context.startActivity(intent)
    }
}