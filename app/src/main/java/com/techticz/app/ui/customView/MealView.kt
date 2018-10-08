package com.techticz.app.ui.customView

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.meal.Meal
import com.techticz.app.repo.MealPlateRepository
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.ui.adapter.MealPlanPagerAdapter
import com.techticz.app.viewmodel.BrowseDietPlanViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIActivity
import kotlinx.android.synthetic.main.content_browse_diet_plan.*
import kotlinx.android.synthetic.main.meal_layout.view.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class MealView(daySection:Int?, parent:ViewGroup?) : FrameLayout(parent?.context) {

    init {
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: ViewGroup?) {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.meal_layout, parent, false) as ViewGroup
        addView(itemView)
    }

    fun fillDetails(meal: Meal){
        tv_meal_name.setText(meal.mealType.mealName)
        tv_meal_plate_id.setText(meal.mealPlateId)
        var liveData = (context as DietChartActivity).mealPlateRepo?.fetchMealPlateResponse(meal.mealPlateId)
        liveData?.observe(context as BaseDIActivity, Observer {
            resource ->
            Timber.d("Data Changed : Source=" + resource?.dataSource)
            onDataLoaded(resource)

        })

    }

    private fun onDataLoaded(resource: Resource<MealPlateResponse>?) {
        //launcherBinding?.viewModel1 = launcherViewModel
        when(resource?.status){
            Status.LOADING->{
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS->
            {
                spin_kit.visibility = View.GONE
                tv_meal_plate_name.text = resource.data?.mealPlate?.basicInfo?.name?.english
                tv_meal_plate_name.visibility = View.VISIBLE
            }
            Status.ERROR->
            {
                spin_kit.visibility = View.GONE
                tv_meal_plate_name.text = "error occured"
                tv_meal_plate_name.visibility = View.VISIBLE
            }
        }

    }
}