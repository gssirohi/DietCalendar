package com.techticz.app.ui.customView

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.Meals
import com.techticz.app.model.dietplan.CaloryDistribution
import com.techticz.app.ui.activity.DashboardActivity
import com.techticz.app.ui.activity.DashboardActivity_MembersInjector
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.util.Utils
import com.techticz.auth.utils.LoginUtils
import kotlinx.android.synthetic.main.fragment_user_profile_goal_details.view.*
import kotlinx.android.synthetic.main.meal_layout.view.*
import kotlinx.android.synthetic.main.meal_plate_content_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class MealView(val daySection:Int?, parent:Context?) : FrameLayout(parent) {
    var mealPlateViewModel: MealPlateViewModel? = null

    init {
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: Context?) {
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.meal_layout, null, false) as ViewGroup
        addView(itemView)
        fab_add_plate.setOnClickListener({addMealPlate()})
        if(context is DietChartActivity) {
            b_remove_plate.setOnClickListener({ removePlate() })
        }
        fab_expand_collapse.setOnClickListener({toggleExpandableView()})
    }

    private fun removePlate() {
        if(context is DietChartActivity) {
            var mealType = mealPlateViewModel?.triggerMealPlateID?.value?.mealType?.id
            (context as DietChartActivity).removePlate(this,daySection,mealType)
        }
    }

    private fun toggleExpandableView() {
        plateView?.let{
            when(it.ll_meal_content?.visibility){
                View.VISIBLE -> {
                    it.ll_meal_content?.visibility = View.GONE
                    fab_expand_collapse.setImageResource(R.drawable.ic_keyboard_arrow_down)
                }
                View.GONE -> {
                    it.ll_meal_content?.visibility = View.VISIBLE
                    fab_expand_collapse.setImageResource(R.drawable.ic_keyboard_arrow_up)
                }
            }
        }
    }

    private fun addMealPlate() {
        (context as DietChartActivity).startBrowsePlateScreen(this)
    }



    private var plateView: PlateView? = null

    fun fillDetails(mealViewModel: MealPlateViewModel) {
        this.mealPlateViewModel = mealViewModel
        tv_meal_name.setText(mealViewModel.triggerMealPlateID.value?.mealType?.mealName)
        tv_recommonded_calory.text = getRequiredMealCalory()
        if(TextUtils.isEmpty(mealViewModel.triggerMealPlateID.value?.mealPlateId)){
            // meal plate is empty
            fab_add_plate.visibility = View.VISIBLE
            b_remove_plate.visibility = View.GONE
            fab_expand_collapse.visibility = View.GONE
            plateView?.visibility = View.GONE
            //rl_plate_container.removeViewAt(1)

        } else {
            if(plateView == null) {
                plateView = PlateView(daySection, context, PlateView.MODE_EXPLORE,rl_plate_container)
                rl_plate_container.addView(plateView)
            }

            plateView?.visibility = View.VISIBLE

            plateView?.fillDetails(mealViewModel)
            fab_add_plate.visibility = View.GONE
            if(context is DietChartActivity) {
                b_remove_plate.visibility = View.VISIBLE
            }
            fab_expand_collapse.visibility = View.VISIBLE
 //           tv_meal_plate_name.setText(mealViewModel.triggerMealPlateID.value?.mealPlateId)
        }

    }

    private fun getRequiredMealCalory(): String {
        var dailyCalories = (context as BaseDIActivity).baseuserViewModel?.liveUserResponse?.value?.data?.user?.dailyRequiredCaloriesAsPerGoal
        var distribution:CaloryDistribution? = null
        if(context is DietChartActivity) {
            distribution = (context as DietChartActivity).dietChartViewModel?.liveDietPlanResponse?.value?.data?.dietPlan?.caloryDistribution
        } else if(context is DashboardActivity){
            distribution = (context as DashboardActivity).dietChartViewModel?.liveDietPlanResponse?.value?.data?.dietPlan?.caloryDistribution
        }
        if(distribution == null){
            distribution = CaloryDistribution().apply {
                earlyMorning = 10
                breakfast = 20
                lunch = 30
                eveningSnacks = 10
                dinner = 20
                bedTime = 10
            }
        }
        if(dailyCalories == null){
            dailyCalories = 2000f
        }
        var meal = mealPlateViewModel?.triggerMealPlateID?.value?.mealType
        when(meal){
            Meals.EARLY_MORNING->{return "Recommonded - "+ Utils.roundUpFloatToOneDecimal(dailyCalories!! * (distribution!!.earlyMorning * 0.01f))+" KCAL"}
            Meals.BREAKFAST->{return "Recommonded - "+ Utils.roundUpFloatToOneDecimal(dailyCalories!! * (distribution!!.breakfast* 0.01f))+" KCAL"}
            Meals.LUNCH->{return "Recommonded - "+ Utils.roundUpFloatToOneDecimal(dailyCalories!! * (distribution!!.lunch* 0.01f))+" KCAL"}
            Meals.BRUNCH->{return "Recommonded - "+ Utils.roundUpFloatToOneDecimal(dailyCalories!! * (distribution!!.eveningSnacks* 0.01f))+" KCAL"}
            Meals.DINNER->{return "Recommonded - "+ Utils.roundUpFloatToOneDecimal(dailyCalories!! * (distribution!!.dinner* 0.01f))+" KCAL"}
            Meals.BED_TIME->{return "Recommonded - "+ Utils.roundUpFloatToOneDecimal(dailyCalories!! * (distribution!!.bedTime* 0.01f))+" KCAL"}
        }
        return ":-)";
    }


}