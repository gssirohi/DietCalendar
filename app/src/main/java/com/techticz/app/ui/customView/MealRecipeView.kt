package com.techticz.app.ui.customView

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.app.repo.FoodRepository
import com.techticz.app.ui.adapter.RecipeFoodAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIActivity
import kotlinx.android.synthetic.main.meal_recipe_layout.view.*
import kotlinx.android.synthetic.main.meal_recipe_layout.view.spin_kit
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class MealRecipeView(val mealView:MealView,parent: ViewGroup?) : FrameLayout(parent?.context) {
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
        tv_recipe_id.setText(recipeViewModel?.triggerRecipeItem?.value?.id)
        tv_recipe_qty.setText("" + recipeViewModel?.triggerRecipeItem?.value?.qty)

        recipeViewModel?.liveRecipeResponse?.observe(context as BaseDIActivity, Observer { resource ->
            onViewModelDataLoaded(resource)

        })

       /* Timber.i("sending signal for recipe:"+recipeViewModel?.triggerRecipeItem?.value?.id)
        var trigger = recipeViewModel?.triggerRecipeItem?.value
        var newTrigger: RecipeItem = RecipeItem()
        newTrigger.id = trigger?.id
        newTrigger.qty = trigger?.qty
        recipeViewModel?.triggerRecipeItem?.value = newTrigger*/

    }

    private fun onViewModelDataLoaded(resource: Resource<RecipeResponse>?) {

        onRecipeLoaded(resource)
    }

    private fun onRecipeLoaded(resource: Resource<RecipeResponse>?) {
        //launcherBinding?.viewModel1 = launcherViewModel
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_name.text = resource.data?.recipe?.basicInfo?.name?.english
                tv_recipe_name.visibility = View.VISIBLE

                loadChildViewModels(resource)
                recipeFoodRecyclerView.layoutManager = LinearLayoutManager(context)
                recipeFoodRecyclerView.adapter = RecipeFoodAdapter(this, null)
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_name.text = resource?.message
                tv_recipe_name.visibility = View.VISIBLE
            }
        }

    }

    private fun loadChildViewModels(resource: Resource<RecipeResponse>) {

        var foods = resource?.data?.recipe?.formula?.ingredients

        var foodViewModelList = ArrayList<FoodViewModel>()

        if (foods != null) {
            for (food in foods) {
                var foodViewModel = FoodViewModel(FoodRepository(FirebaseFirestore.getInstance()))
                foodViewModel.triggerFoodItem.value = food
                foodViewModelList.add(foodViewModel)
            }
        }
        var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, foodViewModelList, "Loading Recipe foods..", DataSource.LOCAL)
        this.recipeViewModel?.liveFoodViewModelList?.value = foodViewModelListResource
        this.recipeViewModel?.liveFoodViewModelList?.observe(context as BaseDIActivity, Observer { resource ->
            onChildViewModelsDataChanged(resource)
            //ring parent bell
            var resOld = mealView.mealPlateViewModel?.liveRecipeViewModelList?.value
            var resNew = resOld?.createCopy(resource?.status)
            mealView.mealPlateViewModel?.liveRecipeViewModelList?.value = resNew
        })
    }

    private fun onChildViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {
        Timber.d("this.recipeViewModel?.liveFoodViewModelList? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)

        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                tv_recipe_calory.text = "" + recipeViewModel?.getNutrients()?.principlesAndDietaryFibers?.energy

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


        mealView.liveMealNutrition.value?.data?.nutrients?.addUpNutrients(factorAppliedNutrients)
        var res = Resource<Nutrition>(Status.SUCCESS, mealView.liveMealNutrition.value?.data, "Partialy Loaded..", DataSource.REMOTE)
        mealView.liveMealNutrition.value = res
    }*/

    }
}