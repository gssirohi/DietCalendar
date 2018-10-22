package com.techticz.app.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.meal.Meal
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.powerkit.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject
import com.techticz.app.repo.MealPlateRepository
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Status

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class MealPlateViewModel @Inject
constructor(mealPlateRepository: MealPlateRepository) : BaseViewModel() {
    val triggerMealPlateID = MutableLiveData<Meal>()
    val liveMealPlateResponse: LiveData<Resource<MealPlateResponse>>
    var liveRecipeViewModelList:MediatorLiveData<Resource<List<RecipeViewModel>>>? = MediatorLiveData<Resource<List<RecipeViewModel>>>()
    var liveFoodViewModelList: MediatorLiveData<Resource<List<FoodViewModel>>>? = MediatorLiveData<Resource<List<FoodViewModel>>>()
    var liveImage: MediatorLiveData<Resource<ImageViewModel>>? = MediatorLiveData<Resource<ImageViewModel>>()
    init {
        Timber.d("Injecting:" + this)
        liveMealPlateResponse = Transformations.switchMap(triggerMealPlateID) { triggerLaunch ->

            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData.create<Resource<MealPlateResponse>>()
            } else {
                Timber.d("MealPlate Trigger detected for:"+triggerLaunch?.mealPlateId)
                var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.EMPTY, null, "Empty Meal recipes..", DataSource.LOCAL)
                liveRecipeViewModelList?.value = recipeViewModelListResource

                var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.EMPTY, null, "Empty Meal foods..", DataSource.LOCAL)
                liveFoodViewModelList?.value = foodViewModelListResource

                var liveImageResource = Resource<ImageViewModel>(Status.EMPTY, null, "Empty image meal..", DataSource.LOCAL)
                liveImage?.value = liveImageResource

                return@switchMap mealPlateRepository.fetchMealPlateResponse(triggerMealPlateID.value)
            }
        }
    }

    fun getNutrients(): Nutrients? {
        var nutrientsFood = Nutrients()

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var foodNutrients: Nutrients? = foodViewModel.getNutrients()
                    var factoredNutrients = foodNutrients?.applyFactor(foodViewModel.triggerFoodItem?.value?.qty!!)
                    nutrientsFood.addUpNutrients(factoredNutrients)
                }
            }
        }

        var nutrientsRecipe = Nutrients()

        var recipeViewModelList = liveRecipeViewModelList?.value?.data
        if(recipeViewModelList != null) {
            for (recipeViewModel in recipeViewModelList!!) {
                if(recipeViewModel.liveRecipeResponse.value?.data != null) {
                    var recipeNutrients: Nutrients? = recipeViewModel.getNutrients()
                    var factoredNutrients = recipeNutrients?.applyFactor(recipeViewModel.triggerRecipeItem?.value?.qty!!)
                    nutrientsRecipe.addUpNutrients(factoredNutrients)
                }
            }
        }
        nutrientsRecipe.addUpNutrients(nutrientsFood)
        return nutrientsRecipe
    }

    fun isVeg(): Boolean {
        var isVeg = true

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var isVeg = foodViewModel.isVeg()
                    if(!isVeg) return false;
                }
            }
        }

        var recipeViewModelList = liveRecipeViewModelList?.value?.data
        if(recipeViewModelList != null) {
            for (recipeViewModel in recipeViewModelList!!) {
                if(recipeViewModel.liveRecipeResponse.value?.data != null) {
                    var isVeg = recipeViewModel.isVeg()
                    if(!isVeg) return false;
                }
            }
        }
        return isVeg
    }
}