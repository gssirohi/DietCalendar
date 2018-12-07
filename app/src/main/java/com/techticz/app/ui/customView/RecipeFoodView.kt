package com.techticz.app.ui.customView

import androidx.lifecycle.Observer
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.model.FoodResponse
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import kotlinx.android.synthetic.main.recipe_food_layout.view.*
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class RecipeFoodView(parent: ViewGroup?, val recipeView: MealRecipeView) : FrameLayout(recipeView?.context) {

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

    private var foodViewModel: FoodViewModel? = null

    fun fillDetails(foodViewModel: FoodViewModel?){
        this.foodViewModel = foodViewModel
        tv_food_name.setText(foodViewModel?.triggerFoodItem?.value?.id)
        tv_food_qty.setText(""+ foodViewModel?.triggerFoodItem?.value?.qty)
        foodViewModel?.liveFoodResponse?.observe(context as BaseDIActivity, Observer {
            resource ->
            onViewModelDataLoaded(resource)

        })
       // foodViewModel?.triggerFoodItem?.value = foodViewModel?.triggerFoodItem?.value
    }

    private fun onViewModelDataLoaded(resource: Resource<FoodResponse>?) {
        Timber.d("foodViewModel?.liveFoodResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        /*if(resource?.status == Status.SUCCESS && resource?.isFresh!!) {
            var resOld = recipeView.recipeViewModel?.liveFoodViewModelList?.value
            var resNew = resOld?.createCopy(resource?.status)
            recipeView.recipeViewModel?.liveFoodViewModelList?.value = resNew
        }*/
        onFoodLoaded(resource)
    }

    private fun onFoodLoaded(resource: Resource<FoodResponse>?) {
        //launcherBinding?.viewModel1 = launcherViewModel
        resource?.isFresh = false
        when(resource?.status){
            Status.LOADING ->{
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS ->
            {
                spin_kit.visibility = View.INVISIBLE
                tv_food_name.text = resource.data?.food?.basicInfo?.name?.english
                tv_food_qty.setText(""+
                        foodViewModel?.triggerFoodItem?.value?.qty+"\n"+
                        resource.data?.food?.standardServing?.servingType)
                tv_food_calory.text = ""+foodViewModel?.perServingCal()+
                        "\nKcal/"+resource.data?.food?.standardServing?.servingType
                tv_food_name.visibility = View.VISIBLE

                when(foodViewModel?.isVeg()){
                    true->tv_food_type.setTextColor(Color.parseColor("#ff669900"))
                    else->tv_food_type.setTextColor(Color.parseColor("#ffcc0000"))
                }
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