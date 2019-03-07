package com.techticz.app.ui.customView

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.techticz.app.model.FoodResponse
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.ui.activity.RecipeDetailsActivity
import com.techticz.app.viewmodel.ImageViewModel
import kotlinx.android.synthetic.main.meal_food_layout.view.*
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class RecipeDetailsFoodView(parent: ViewGroup?, val recipeActivity: RecipeDetailsActivity) : FrameLayout(recipeActivity) {

    init {
        val params = LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: ViewGroup?) {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.meal_food_layout, parent, false) as ViewGroup
        addView(itemView)
    }

    private var foodViewModel: FoodViewModel? = null

    fun fillDetails(foodViewModel: FoodViewModel?){
        this.foodViewModel = foodViewModel
        tv_food_name.setText(foodViewModel?.triggerFoodItem?.value?.id)

        tv_food_qty.setText("" + foodViewModel?.triggerFoodItem?.value?.qty)
        tv_food_qty_unit.setText(" serving")

        fab_plus.setOnClickListener({onFabPlusClicked()})
        fab_minus.setOnClickListener({onFabMinusClicked()})
        fab_remove.setOnClickListener({onFabRemoveClicked()})
        tv_pivot.setOnClickListener { MaterialDialog(context).show {
            listItemsSingleChoice(items =listOf("1","10","50","100")) { dialog, index, text ->
                pivotPortion = text.toInt()
                tv_pivot.text = ""+pivotPortion
            }
        }}
        foodViewModel?.autoLoadChildren(context as BaseDIActivity)
        foodViewModel?.liveFoodResponse?.observe(context as BaseDIActivity, Observer {
            resource ->
            onViewModelDataLoaded(resource)

        })
        if(recipeActivity.mode == RecipeDetailsActivity.MODE_EXPLORE){
            configureUIinEditMode(false)

        } else {
            configureUIinEditMode(true)
        }

        //foodViewModel?.triggerFoodItem?.value = foodViewModel?.triggerFoodItem?.value
    }

    private fun onFabRemoveClicked(){
        recipeActivity?.removeFood(context as BaseDIActivity,foodViewModel?.triggerFoodItem?.value)
    }

    private fun onFabMinusClicked() {

        if(foodViewModel?.triggerFoodItem?.value?.qty!! > pivotPortion) {
            foodViewModel?.triggerFoodItem?.value?.qty = foodViewModel?.triggerFoodItem?.value?.qty!! - pivotPortion
            tv_food_qty.setText("" + foodViewModel?.triggerFoodItem?.value?.qty)
            recipeActivity?.recipeViewModel?.registerFoodChildCompletion()
//            var newRes = foodViewModel?.liveFoodResponse?.value?.createCopy(Status.SUCCESS)
//            foodViewModel?.liveFoodResponse?.value = newRes
            if( foodViewModel?.triggerFoodItem?.value?.qty!! <= pivotPortion){
                fab_remove.visibility = View.VISIBLE
                fab_minus.visibility = View.GONE
            } else {
                fab_remove.visibility = View.GONE
                fab_minus.visibility = View.VISIBLE
            }
        }
    }

    private fun onFabPlusClicked() {

        if(foodViewModel?.triggerFoodItem?.value?.qty!! < 2000) {
            foodViewModel?.triggerFoodItem?.value?.qty = foodViewModel?.triggerFoodItem?.value?.qty!! + pivotPortion
            tv_food_qty.setText("" + foodViewModel?.triggerFoodItem?.value?.qty)
            recipeActivity?.recipeViewModel?.registerFoodChildCompletion()
//            var newRes = foodViewModel?.liveFoodViewModelList?.value?.createCopy(Status.COMPLETE)
//            foodViewModel?.liveFoodViewModelList?.value = newRes
            if( foodViewModel?.triggerFoodItem?.value?.qty!! > pivotPortion){
                fab_remove.visibility = View.GONE
                fab_minus.visibility = View.VISIBLE
            } else {
                fab_remove.visibility = View.VISIBLE
                fab_minus.visibility = View.GONE
            }
        }

    }

    private fun configureUIinEditMode(yes: Boolean) {
        if(yes){
            fab_plus.visibility = View.VISIBLE

            if( foodViewModel?.triggerFoodItem?.value?.qty!! > 1){
                fab_remove.visibility = View.GONE
                fab_minus.visibility = View.VISIBLE
            } else {
                fab_remove.visibility = View.VISIBLE
                fab_minus.visibility = View.GONE
            }

        } else {
            fab_minus.visibility = View.GONE
            fab_plus.visibility = View.GONE
            fab_remove.visibility = View.GONE
        }
    }

    private fun onViewModelDataLoaded(resource: Resource<FoodResponse>?) {
        Timber.d("foodViewModel?.liveFoodResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        onFoodLoaded(resource)
    }

    private var pivotPortion: Int = 10

    private fun onFoodLoaded(resource: Resource<FoodResponse>?) {
        resource?.isFresh = false
        //launcherBinding?.viewModel1 = launcherViewModel
        when(resource?.status){
            Status.LOADING ->{
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS ->
            {
                spin_kit.visibility = View.INVISIBLE
                tv_food_name.text = resource.data?.food?.basicInfo?.name?.english
                tv_food_calory.text = foodViewModel?.perServingCalText()
                tv_food_calory_per.text = foodViewModel?.perServingCalPerUnitText()
                tv_food_qty.text = ""+ foodViewModel?.triggerFoodItem?.value?.qty
                tv_food_qty_unit.text = ""+foodViewModel?.liveFoodResponse?.value?.data?.food?.standardServing?.servingUnit
                foodViewModel?.liveFoodResponse?.value?.data?.food?.standardServing?.portion?.let{
                    pivotPortion = it
                }
                tv_pivot.text = ""+pivotPortion
                observeChildViewModels(resource)

                when(foodViewModel?.isVeg()){
                    true->tv_food_type.setTextColor(Color.parseColor("#ff669900"))
                    else->tv_food_type.setTextColor(Color.parseColor("#ffcc0000"))
                }

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

    private fun observeChildViewModels(resource: Resource<FoodResponse>) {
        this.foodViewModel?.liveImage?.observe(context as BaseDIActivity, Observer {
            resource->
            onImageModelLoaded(resource)
        })
    }
    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.foodViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
                aiv_food.setImageViewModel(resource?.data, context as LifecycleOwner)

            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                aiv_food.setImageViewModel(resource?.data, context as LifecycleOwner)
            }
            Status.EMPTY -> {
                /* var imageViewModel = ImageViewModel(context)
                 imageViewModel.triggerImageUrl.value = recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.basicInfo?.image
                 var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading recipe image model success..",DataSource.REMOTE)
                 this.recipeViewModel?.liveImage?.value = imageRes*/
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_food_calory.text = resource?.message
                tv_food_calory.visibility = View.VISIBLE
            }
        }
    }

}