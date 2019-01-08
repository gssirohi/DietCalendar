package com.techticz.app.ui.customView

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.app.ui.adapter.PlateRecipeFoodAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import kotlinx.android.synthetic.main.meal_recipe_layout.view.*
import kotlinx.android.synthetic.main.meal_recipe_layout.view.spin_kit
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class MealRecipeView(val plateView:PlateView, parent: ViewGroup?) : FrameLayout(plateView?.context) {
    var liveRecipeNutrition: MediatorLiveData<Resource<Nutrition>> = MediatorLiveData<Resource<Nutrition>>()

    init {
        val params = LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: ViewGroup?) {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.meal_recipe_layout, parent, false) as ViewGroup
        addView(itemView)
    }


    var recipeViewModel: RecipeViewModel? = null

    fun fillDetails(recipeViewModel: RecipeViewModel?) {
        this.recipeViewModel = recipeViewModel
        tv_recipe_name.setText(recipeViewModel?.triggerRecipeItem?.value?.id)
        tv_recipe_qty.setText("" + recipeViewModel?.triggerRecipeItem?.value?.qty)
        tv_recipe_qty_unit.setText(" serving")

        fab_plus.setOnClickListener({onFabPlusClicked()})
        fab_minus.setOnClickListener({onFabMinusClicked()})
        fab_remove.setOnClickListener({onFabRemoveClicked()})
        fab_explore_recipe.setOnClickListener({exploreRecipe()})
        recipeViewModel?.liveRecipeResponse?.observe(context as BaseDIActivity, Observer { resource ->
            onViewModelDataLoaded(resource)

        })

        if(plateView.mode == PlateView.MODE_EXPLORE){
            configureUIinEditMode(false)

        } else {
            configureUIinEditMode(true)
        }

       /* Timber.i("sending signal for recipe:"+recipeViewModel?.triggerRecipeItem?.value?.id)
        var trigger = recipeViewModel?.triggerRecipeItem?.value
        var newTrigger: RecipeItem = RecipeItem()
        newTrigger.id = trigger?.id
        newTrigger.qty = trigger?.qty
        recipeViewModel?.triggerRecipeItem?.value = newTrigger*/

    }

    private fun exploreRecipe() {
        if(context is BaseDIActivity){
            (context as BaseDIActivity).navigator.startExploreRecipeScreen(recipeViewModel?.triggerRecipeItem?.value)
        }
    }

    private fun onFabRemoveClicked(){
        plateView?.removeRecipe(context as BaseDIActivity,recipeViewModel?.triggerRecipeItem?.value)
    }

    private fun onFabMinusClicked() {
        if(recipeViewModel?.triggerRecipeItem?.value?.qty!! > 1) {
            recipeViewModel?.triggerRecipeItem?.value?.qty = recipeViewModel?.triggerRecipeItem?.value?.qty!! - 1
            tv_recipe_qty.setText("" + recipeViewModel?.triggerRecipeItem?.value?.qty)
            var newRes = recipeViewModel?.liveFoodViewModelList?.value?.createCopy(Status.COMPLETE)
            recipeViewModel?.liveFoodViewModelList?.value = newRes
            if( recipeViewModel?.triggerRecipeItem?.value?.qty!! <= 1){
                fab_remove.visibility = View.VISIBLE
                fab_minus.visibility = View.GONE
            } else {
                fab_remove.visibility = View.GONE
                fab_minus.visibility = View.VISIBLE
            }
        }
    }

    private fun onFabPlusClicked() {
        if(recipeViewModel?.triggerRecipeItem?.value?.qty!! < 15) {
            recipeViewModel?.triggerRecipeItem?.value?.qty = recipeViewModel?.triggerRecipeItem?.value?.qty!! + 1
            tv_recipe_qty.setText("" + recipeViewModel?.triggerRecipeItem?.value?.qty)
            var newRes = recipeViewModel?.liveFoodViewModelList?.value?.createCopy(Status.COMPLETE)
            recipeViewModel?.liveFoodViewModelList?.value = newRes
            if( recipeViewModel?.triggerRecipeItem?.value?.qty!! > 1){
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

            if( recipeViewModel?.triggerRecipeItem?.value?.qty!! > 1){
                fab_remove.visibility = View.GONE
                fab_minus.visibility = View.VISIBLE
            } else {
                fab_remove.visibility = View.VISIBLE
                fab_minus.visibility = View.GONE
            }

            tv_show_more_less.visibility = View.GONE
        } else {
            fab_minus.visibility = View.GONE
            fab_plus.visibility = View.GONE
            fab_remove.visibility = View.GONE

            tv_show_more_less.visibility = View.VISIBLE
        }
    }

    private fun onViewModelDataLoaded(resource: Resource<RecipeResponse>?) {

        onRecipeLoaded(resource)
    }

    private fun onRecipeLoaded(resource: Resource<RecipeResponse>?) {
        resource?.isFresh = false
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_name.text = resource.data?.recipe?.basicInfo?.name?.english
                tv_recipe_name.visibility = View.VISIBLE

                tv_recipe_qty.setText("" + recipeViewModel?.triggerRecipeItem?.value?.qty)
                tv_recipe_qty_unit.setText(" "+resource.data?.recipe?.standardServing?.servingType)

                observeChildViewModels(resource)
                recipeFoodRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                recipeFoodRecyclerView.adapter = PlateRecipeFoodAdapter(this, null)
                recipeFoodRecyclerView.visibility = View.GONE
                tv_show_more_less.text = "show more"

                tv_show_more_less.setOnClickListener({
                    if(recipeFoodRecyclerView.visibility == View.GONE){
                        recipeFoodRecyclerView.visibility = View.VISIBLE
                        tv_show_more_less.text = "show less"
                    } else {
                        recipeFoodRecyclerView.visibility = View.GONE
                        tv_show_more_less.text = "show more"
                    }
                })
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_name.text = resource?.message
                tv_recipe_name.visibility = View.VISIBLE
            }
        }

    }

    private fun observeChildViewModels(resource: Resource<RecipeResponse>) {
        this.recipeViewModel?.liveFoodViewModelList?.observe(context as BaseDIActivity, Observer { resource ->

            onChildViewModelsDataChanged(resource)
        })

        this.recipeViewModel?.liveImage?.observe(context as BaseDIActivity, Observer {
            resource->
            onImageModelLoaded(resource)
        })
    }

    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.recipeViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.INVISIBLE
                aiv_recipe.setImageViewModel(resource?.data, context as LifecycleOwner)

            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                aiv_recipe.setImageViewModel(resource?.data, context as LifecycleOwner)
            }
            Status.EMPTY -> {
               /* var imageViewModel = ImageViewModel(context)
                imageViewModel.triggerImageUrl.value = recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.basicInfo?.image
                var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading recipe image model success..",DataSource.REMOTE)
                this.recipeViewModel?.liveImage?.value = imageRes*/
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_calory.text = resource?.message
                tv_recipe_calory.visibility = View.VISIBLE
            }
        }
    }

    private fun onChildViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {
        resource?.isFresh = false
        Timber.d("this.recipeViewModel?.liveFoodViewModelList? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)

        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_calory.text = recipeViewModel?.perServingCalText()
                tv_recipe_calory_per.text = recipeViewModel?.perServingCalPerUnitText()
                when(recipeViewModel?.isVeg()){
                    true->tv_recipe_type.setTextColor(Color.parseColor("#ff669900"))
                    else->tv_recipe_type.setTextColor(Color.parseColor("#ffcc0000"))
                }

            }
            Status.EMPTY->{

               /* var foods = recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.formula?.ingredients

                var foodViewModelList = ArrayList<FoodViewModel>()

                if (foods != null) {
                    for (food in foods) {
                        var foodViewModel = FoodViewModel(FoodRepository(FirebaseFirestore.getInstance()))
                        foodViewModel.triggerFoodItem.value = food
                        foodViewModelList.add(foodViewModel)
                    }
                }
                var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, foodViewModelList, "Loading Recipe foods..", DataSource.LOCAL)
                this.recipeViewModel?.liveFoodViewModelList?.value = foodViewModelListResource*/
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_calory.text = resource?.message
                tv_recipe_calory.visibility = View.VISIBLE
            }
        }

        //  private val foodList: ArrayList<LiveData<Resource<FoodResponse>>> = ArrayList()
/*

    private fun onRecipeFoodLoaded(ingredient: FoodItem, resource: Resource<FoodResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                addUpRecipeNutrients(ingredient, resource.data?.food)
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_name.text = resource?.message
                tv_recipe_name.visibility = View.VISIBLE
            }
        }


    }*/

        /*  private fun addUpRecipeNutrients(ingredient: FoodItem, food: Food?) {
        var qty: Int? = ingredient?.qty
        var nutritions = food?.nutrition
        var serving = food?.standardServing
        //apply serving logic
        //factor should be calculated by finding the ratio of servingType capacity and nutritions.perQty if units are same
        var factor = 1
        var finalQtyFactor = qty!! * factor
        var factorAppliedNutrients: Nutrients? = nutritions?.nutrients?.applyFactor(finalQtyFactor)


        liveRecipeNutrition.value?.data?.nutrients?.addUpNutrients(factorAppliedNutrients)
        var res = Resource<Nutrition>(Status.SUCCESS, liveRecipeNutrition.value?.data, "Partialy Loaded..", DataSource.REMOTE)
        liveRecipeNutrition.value = res
    }
*/
/*    private fun onRecipeNutritionLoaded(resource: Resource<Nutrition>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
                tv_recipe_calory.visibility = View.INVISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                var calories = resource?.data?.nutrients?.principlesAndDietaryFibers?.energy
                tv_recipe_calory.text = ""+calories
                tv_recipe_calory.visibility = View.VISIBLE
                addUpMealNutrients()
            }

            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_calory.text = resource?.message
                tv_recipe_calory.visibility = View.VISIBLE
            }
        }

    }

    private fun addUpMealNutrients() {
        var qty:Int? = recipeItem?.qty
        var nutritions = liveRecipeNutrition?.value?.data
        //var serving = food?.standardServing
        //apply serving logic
        //factor should be calculated by finding the ratio of servingType capacity and nutritions.perQty if units are same
        var factor = 1
        var finalQtyFactor = qty!!*factor
        var factorAppliedNutrients:Nutrients? = nutritions?.nutrients?.applyFactor(finalQtyFactor)


        plateView.liveMealNutrition.value?.data?.nutrients?.addUpNutrients(factorAppliedNutrients)
        var res = Resource<Nutrition>(Status.SUCCESS, plateView.liveMealNutrition.value?.data, "Partialy Loaded..", DataSource.REMOTE)
        plateView.liveMealNutrition.value = res
    }*/

    }
}