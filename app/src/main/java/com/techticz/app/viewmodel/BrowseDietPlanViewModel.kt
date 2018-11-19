package com.techticz.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    val triggerFeaturedMealPlans = MutableLiveData<Boolean>()
    val triggerMyMealPlans = MutableLiveData<Boolean>()
    val featuredDietPlansResponse: LiveData<Resource<BrowseDietPlanResponse>>
    val myDietPlansResponse: LiveData<Resource<BrowseDietPlanResponse>>
    init {
         featuredDietPlansResponse = Transformations.switchMap(triggerFeaturedMealPlans) { triggerBrowse ->
            Timber.d("BrowseMealPlan Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseDietPlanResponse>>()
            } else {
                return@switchMap injectedRepo?.browsePublishedDietPlansResponseData
            }
        }

        myDietPlansResponse = Transformations.switchMap(triggerMyMealPlans) { triggerBrowse ->
            Timber.d("Browse MY MealPlan Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseDietPlanResponse>>()
            } else {
                return@switchMap injectedRepo?.browseMyDietPlansResponseData
            }
        }
    }


}