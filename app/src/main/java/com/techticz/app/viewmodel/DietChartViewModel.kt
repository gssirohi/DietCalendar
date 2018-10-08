package com.techticz.app.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.powerkit.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class DietChartViewModel @Inject
constructor(dietPlanRepository: DietPlanRepository) : BaseViewModel() {
    val triggerFetchingMealPlans = MutableLiveData<Boolean>()
    val dietPlansResponse: LiveData<Resource<BrowseMealPlanResponse>>

    init {
        Timber.d("Injecting:" + this)
        dietPlansResponse = Transformations.switchMap(triggerFetchingMealPlans) { triggerLaunch ->
            Timber.d("Launch Trigger received.")
            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseMealPlanResponse>>()
            } else {
                return@switchMap dietPlanRepository.dietPlanResponseData
            }
        }
    }


}