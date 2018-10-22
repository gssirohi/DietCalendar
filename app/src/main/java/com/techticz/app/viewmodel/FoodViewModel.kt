package com.techticz.app.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.constants.FoodCategories
import com.techticz.app.model.FoodResponse
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.repo.FoodRepository
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

class FoodViewModel @Inject
constructor(foodRepository: FoodRepository) : BaseViewModel() {
    val triggerFoodItem = MutableLiveData<FoodItem>()
    val liveFoodResponse: LiveData<Resource<FoodResponse>>
    init {
        Timber.d("Injecting:" + this)
        liveFoodResponse = Transformations.switchMap(triggerFoodItem) { triggerLaunch ->
            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData.create<Resource<FoodResponse>>()
            } else {
                Timber.d("Food Trigger detected for:"+triggerLaunch?.id)
                return@switchMap foodRepository.fetchFoodResponse(triggerFoodItem.value)
            }
        }
    }

    fun getNutrients(): Nutrients? {
        var nutrients = liveFoodResponse?.value?.data?.food?.nutrition?.nutrients
        return nutrients
    }

    fun isVeg(): Boolean {
        if(liveFoodResponse?.value?.status == Status.SUCCESS) {
            var cat = liveFoodResponse?.value?.data?.food?.basicInfo?.category
            if (cat.equals(FoodCategories.EGG.id, true)
                    || cat.equals(FoodCategories.MEAT.id, true)) {
                return false;
            }
            return true;
        }
        return true;
    }


}