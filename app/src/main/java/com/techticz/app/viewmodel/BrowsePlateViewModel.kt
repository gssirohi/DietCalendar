package com.techticz.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    val triggerSearchPlateText = MutableLiveData<String>()
    val triggerFeaturedPlateMealType = MutableLiveData<String>()
    val triggerMyPlates = MutableLiveData<String>()
    val liveBrowsePlatesResponse: LiveData<Resource<BrowsePlateResponse>>
    val liveFeaturedPlatesResponse: LiveData<Resource<BrowsePlateResponse>>
    val liveMyPlatesResponse: LiveData<Resource<BrowsePlateResponse>>

    init {
        liveBrowsePlatesResponse = Transformations.switchMap(triggerSearchPlateText) { triggerBrowse ->
            Timber.d("Browse Plates Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowsePlateResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchPlatesWithText(triggerBrowse)
            }
        }

        liveFeaturedPlatesResponse = Transformations.switchMap(triggerFeaturedPlateMealType) { triggerMealType ->
            Timber.d("Featured Plates Trigger received.")
            if (triggerMealType == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowsePlateResponse>>()
            } else if(triggerMealType.equals("")){
                return@switchMap injectedRepo?.fetchAllFeaturedPlates()
            } else {
                return@switchMap injectedRepo?.fetchPlatesForMealType(triggerMealType)
            }
        }

        liveMyPlatesResponse = Transformations.switchMap(triggerMyPlates) { trigger ->
            Timber.d("My Plates Trigger received.")
            if (trigger == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowsePlateResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchPlatesForUser()
            }
        }


    }


}