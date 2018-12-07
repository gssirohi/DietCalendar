package com.techticz.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.app.base.BaseViewModel
import com.techticz.app.model.BrowseFoodResponse

import com.techticz.app.repo.FoodRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class BrowseFoodViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: FoodRepository

    val triggerFoodText = MutableLiveData<String>()
    val liveBrowseFoodsResponse: LiveData<Resource<BrowseFoodResponse>>

    init {
        liveBrowseFoodsResponse = Transformations.switchMap(triggerFoodText) { triggerBrowse ->
            Timber.d("Browse Foods Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseFoodResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchFoodsWithText(triggerBrowse)
            }
        }


    }


}