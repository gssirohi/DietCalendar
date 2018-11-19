package com.techticz.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.app.base.BaseViewModel
import com.techticz.app.model.BrowsePlateResponse
import com.techticz.app.repo.MealPlateRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class BrowsePlateViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: MealPlateRepository

    val triggerPlateText = MutableLiveData<String>()
    val liveBrowsePlatesResponse: LiveData<Resource<BrowsePlateResponse>>

    init {
        liveBrowsePlatesResponse = Transformations.switchMap(triggerPlateText) { triggerBrowse ->
            Timber.d("Browse Plates Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowsePlateResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchPlatesWithText(triggerBrowse)
            }
        }


    }


}