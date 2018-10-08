package com.techticz.app.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.techticz.app.model.MealPlateResponse
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.powerkit.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject
import com.techticz.app.repo.MealPlateRepository
/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class MealPlateViewModel @Inject
constructor(mealPlateRepository: MealPlateRepository) : BaseViewModel() {
    val triggerMealPlateID = MutableLiveData<String>()
    val mealPlateResponse: LiveData<Resource<MealPlateResponse>>

    init {
        Timber.d("Injecting:" + this)
        mealPlateResponse = Transformations.switchMap(triggerMealPlateID) { triggerLaunch ->
            Timber.d("MealPlate Trigger received.")
            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData.create<Resource<MealPlateResponse>>()
            } else {
                return@switchMap mealPlateRepository.fetchMealPlateResponse(triggerMealPlateID.value)
            }
        }
    }


}