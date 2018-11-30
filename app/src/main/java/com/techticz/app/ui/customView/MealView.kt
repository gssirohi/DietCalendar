package com.techticz.app.ui.customView

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.app.ui.activity.DietChartActivity
import kotlinx.android.synthetic.main.meal_layout.view.*

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
    }

    private fun addMealPlate() {
        (context as DietChartActivity).startBrowsePlateScreen(this)
    }



    private var plateView: PlateView? = null

    fun fillDetails(mealViewModel: MealPlateViewModel) {
        this.mealPlateViewModel = mealViewModel
        tv_meal_name.setText(mealViewModel.triggerMealPlateID.value?.mealType?.mealName)

        if(TextUtils.isEmpty(mealViewModel.triggerMealPlateID.value?.mealPlateId)){
            // meal plate is empty
            fab_add_plate.visibility = View.VISIBLE
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
 //           tv_meal_plate_name.setText(mealViewModel.triggerMealPlateID.value?.mealPlateId)
        }

    }


}