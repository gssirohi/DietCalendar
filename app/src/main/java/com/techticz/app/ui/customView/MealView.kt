package com.techticz.app.ui.customView

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.ui.adapter.MealFoodAdapter
import com.techticz.app.ui.adapter.MealRecipesAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIActivity
import kotlinx.android.synthetic.main.meal_layout.view.*
import kotlinx.android.synthetic.main.meal_plate_desc_layout.view.*
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class MealView(daySection:Int?, parent:ViewGroup?) : FrameLayout(parent?.context) {
    var mealPlateViewModel: MealPlateViewModel? = null

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

    private fun onViewModelDataLoaded(resource: Resource<MealPlateResponse>?) {
        Timber.d("mealPlateViewModel?.liveMealPlateResponse? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)
        onMealPlateLoaded(resource)
    }

    fun fillDetails(mealViewModel: MealPlateViewModel) {
        this.mealPlateViewModel = mealViewModel
        tv_meal_name.setText(mealViewModel.triggerMealPlateID.value?.mealType?.mealName)
        tv_meal_plate_id.setText(mealViewModel.triggerMealPlateID.value?.mealPlateId)

        mealViewModel?.liveMealPlateResponse?.observe(context as BaseDIActivity, Observer { resource ->
            onViewModelDataLoaded(resource)

        })



    }

    private fun onMealPlateLoaded(resource: Resource<MealPlateResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {

                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_name.text = resource.data?.mealPlate?.basicInfo?.name?.english
                tv_meal_plate_name.visibility = View.VISIBLE


                loadChildViewModels(resource)
                recipeRecyclerView.layoutManager = (LinearLayoutManager(context))
                recipeRecyclerView.adapter = MealRecipesAdapter(this, null)

                foodRecyclerView.layoutManager = LinearLayoutManager(context)
                foodRecyclerView.adapter = MealFoodAdapter(this, null)

            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_name.text = resource?.message
                tv_meal_plate_name.visibility = View.VISIBLE
            }
        }

    }

    private fun loadChildViewModels(resource: Resource<MealPlateResponse>) {

        var recipes = resource?.data?.mealPlate?.items?.recipies

        var recipeViewModelList = ArrayList<RecipeViewModel>()

        if (recipes != null) {
            for (recipe in recipes) {
                var recipeViewModel = RecipeViewModel(RecipeRepository(FirebaseFirestore.getInstance()))
                recipeViewModel.triggerRecipeItem.value = recipe
                recipeViewModelList.add(recipeViewModel)
            }
        }
        var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.LOADING, recipeViewModelList, "Loading Meal recipes..", DataSource.LOCAL)
        mealPlateViewModel?.liveRecipeViewModelList?.value = recipeViewModelListResource


        var foods = resource?.data?.mealPlate?.items?.foods

        var foodViewModelList = ArrayList<FoodViewModel>()

        if (foods != null) {
            for (food in foods) {
                var foodViewModel = FoodViewModel(FoodRepository(FirebaseFirestore.getInstance()))
                foodViewModel.triggerFoodItem.value = food
                foodViewModelList.add(foodViewModel)
            }
        }
        var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, foodViewModelList, "Loading Meal foods..", DataSource.LOCAL)
        mealPlateViewModel?.liveFoodViewModelList?.value = foodViewModelListResource


        mealPlateViewModel?.liveRecipeViewModelList?.observe(context as BaseDIActivity, Observer { resource ->
            Timber.d("mealPlateViewModel?.liveRecipeViewModelList? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)
            onChildRecipeViewModelsDataChanged(resource)
        })

        mealPlateViewModel?.liveFoodViewModelList?.observe(context as BaseDIActivity, Observer { resource ->
            Timber.d("mealPlateViewModel?.liveFoodViewModelList? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)
            onChildFoodViewModelsDataChanged(resource)
        })

    }

    private fun onChildFoodViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {

        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = "" + mealPlateViewModel?.getNutrients()?.principlesAndDietaryFibers?.energy

            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = resource?.message
                tv_meal_plate_calories.visibility = View.VISIBLE
            }
        }
    }
    private fun onChildRecipeViewModelsDataChanged(resource: Resource<List<RecipeViewModel>>?) {

        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = "" + mealPlateViewModel?.getNutrients()?.principlesAndDietaryFibers?.energy
                tv_meal_plate_calories.visibility = View.VISIBLE

            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = resource?.message
                tv_meal_plate_calories.visibility = View.VISIBLE
            }
        }
    }
}