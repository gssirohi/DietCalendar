package com.techticz.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
    val triggerFruits = MutableLiveData<String>()
    val triggerVegitables = MutableLiveData<String>()
    val triggerEggOrMeat = MutableLiveData<String>()
    val liveBrowseFoodsResponse: LiveData<Resource<BrowseFoodResponse>>
    val liveFruitsResponse: LiveData<Resource<BrowseFoodResponse>>
    val liveVegitablesResponse: LiveData<Resource<BrowseFoodResponse>>
    val liveEggsOrMeatResponse: LiveData<Resource<BrowseFoodResponse>>

    init {
        liveBrowseFoodsResponse = Transformations.switchMap(triggerFoodText) { triggerBrowse ->
            Timber.d("Browse Foods Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseFoodResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchFoodsWithText(triggerBrowse)
            }
        }

        liveFruitsResponse = Transformations.switchMap(triggerFruits) { triggerCategory ->
            Timber.d("Browse Category Foods Trigger received.")
            if (triggerCategory == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseFoodResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchFoodsWithCategory(triggerCategory)
            }
        }

        liveVegitablesResponse = Transformations.switchMap(triggerVegitables) { triggerCategory ->
            Timber.d("Browse Category Foods Trigger received.")
            if (triggerCategory == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseFoodResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchFoodsWithCategory(triggerCategory)
            }
        }

        liveEggsOrMeatResponse = Transformations.switchMap(triggerEggOrMeat) { triggerCategory ->
            Timber.d("Browse Category Foods Trigger received.")
            if (triggerCategory == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseFoodResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchFoodsWithCategory(triggerCategory)
            }
        }


    }


}