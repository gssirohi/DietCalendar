package com.techticz.app.ui.customView

import android.arch.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.model.FoodResponse
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIActivity
import kotlinx.android.synthetic.main.recipe_food_layout.view.*
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class RecipeFoodView(parent: ViewGroup?, val recipeView: MealRecipeView) : FrameLayout(parent?.context) {

    init {
        val params = LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: ViewGroup?) {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.recipe_food_layout, parent, false) as ViewGroup
        addView(itemView)
    }

    fun fillDetails(foodViewModel: FoodViewModel?){
        tv_food_id.setText(foodViewModel?.triggerFoodItem?.value?.id)
        tv_food_qty.setText(""+ foodViewModel?.triggerFoodItem?.value?.qty)
        foodViewModel?.liveFoodResponse?.observe(context as BaseDIActivity, Observer {
            resource ->
            onViewModelDataLoaded(resource)

        })
       // foodViewModel?.triggerFoodItem?.value = foodViewModel?.triggerFoodItem?.value
    }

    private fun onViewModelDataLoaded(resource: Resource<FoodResponse>?) {
        Timber.d("foodViewModel?.liveFoodResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        onFoodLoaded(resource)
        var resOld = recipeView.recipeViewModel?.liveFoodViewModelList?.value
        var resNew = resOld?.createCopy(resource?.status)
        recipeView.recipeViewModel?.liveFoodViewModelList?.value = resNew
    }

    private fun onFoodLoaded(resource: Resource<FoodResponse>?) {
        //launcherBinding?.viewModel1 = launcherViewModel
        when(resource?.status){
            Status.LOADING ->{
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS ->
            {
                spin_kit.visibility = View.INVISIBLE
                tv_food_name.text = resource.data?.food?.basicInfo?.name?.english
                tv_food_calory.text = ""+resource.data?.food?.nutrition?.nutrients?.principlesAndDietaryFibers?.energy
                tv_food_name.visibility = View.VISIBLE
            }
            Status.ERROR ->
            {
                spin_kit.visibility = View.INVISIBLE
                tv_food_name.text = resource?.message
                tv_food_name.visibility = View.VISIBLE
            }
        }

    }



}