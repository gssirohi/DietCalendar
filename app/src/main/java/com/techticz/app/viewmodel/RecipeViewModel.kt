package com.techticz.app.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.model.FoodResponse
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.food.PrinciplesAndDietaryFibers
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class RecipeViewModel @Inject
constructor(mealPlateRepository: RecipeRepository) : BaseViewModel() {
    val triggerRecipeItem = MutableLiveData<RecipeItem>()
    val liveRecipeResponse: LiveData<Resource<RecipeResponse>>
    var liveFoodViewModelList: MediatorLiveData<Resource<List<FoodViewModel>>>? = MediatorLiveData<Resource<List<FoodViewModel>>>()
    init {
        Timber.d("Injecting:" + this)
        liveRecipeResponse = Transformations.switchMap(triggerRecipeItem) { triggerLaunch ->

            if (triggerLaunch == null || triggerLaunch.id == null) {
                return@switchMap AbsentLiveData.create<Resource<RecipeResponse>>()
            } else {
                Timber.d("Recipe Trigger detected for:"+triggerLaunch?.id)
                return@switchMap mealPlateRepository.fetchRecipeResponse(triggerRecipeItem.value)
            }
        }
    }

    fun getNutrients(): Nutrients? {
        var nutrients = Nutrients()

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var foodNutrients: Nutrients? = foodViewModel.getNutrients()
                    var factoredNutrients = foodNutrients?.applyFactor(foodViewModel.triggerFoodItem?.value?.qty!!)
                    nutrients.addUpNutrients(factoredNutrients)
                }
            }
        }
        return nutrients
    }


}