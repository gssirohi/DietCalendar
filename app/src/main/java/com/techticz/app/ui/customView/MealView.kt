package com.techticz.app.ui.customView

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.app.ui.adapter.MealFoodAdapter
import com.techticz.app.ui.adapter.MealRecipesAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.ui.activity.DietChartActivity
import kotlinx.android.synthetic.main.meal_layout.view.*
import kotlinx.android.synthetic.main.meal_plate_content_layout.view.*
import kotlinx.android.synthetic.main.plate_layout.view.*


import timber.log.Timber

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
        b_add_plate.setOnClickListener({addMealPlate()})
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
            b_add_plate.visibility = View.VISIBLE
            b_explore_plate.visibility = View.GONE
            plateView?.visibility = View.GONE
            //rl_plate_container.removeViewAt(1)

        } else {
            b_explore_plate.visibility = View.VISIBLE
            if(plateView == null) {
                plateView = PlateView(daySection, context, PlateView.MODE_COLLAPSED)
                rl_plate_container.addView(plateView)
            }

            plateView?.visibility = View.VISIBLE

            plateView?.fillDetails(mealViewModel)
            b_add_plate.visibility = View.GONE
 //           tv_meal_plate_name.setText(mealViewModel.triggerMealPlateID.value?.mealPlateId)
        }

    }


}