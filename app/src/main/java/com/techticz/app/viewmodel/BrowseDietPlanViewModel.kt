package com.techticz.app.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.app.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class BrowseDietPlanViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: DietPlanRepository

    val triggerFetchingMealPlans = MutableLiveData<Boolean>()
    val dietPlansResponse: LiveData<Resource<BrowseDietPlanResponse>>

    init {
         dietPlansResponse = Transformations.switchMap(triggerFetchingMealPlans) { triggerBrowse ->
            Timber.d("BrowseMealPlan Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseDietPlanResponse>>()
            } else {
                return@switchMap injectedRepo?.browseDietPlansResponseData
            }
        }
    }


}