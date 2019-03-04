package com.techticz.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.app.base.BaseViewModel
import com.techticz.app.model.BrowseRecipeResponse

import com.techticz.app.repo.RecipeRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class BrowseRecipeViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: RecipeRepository

    val triggerRecipeText = MutableLiveData<String>()
    val triggerFeaturedRecipes = MutableLiveData<Boolean>()
    val triggerMyRecipes = MutableLiveData<Boolean>()
    val liveBrowseRecipesResponse: LiveData<Resource<BrowseRecipeResponse>>
    val liveFeaturedRecipesResponse: LiveData<Resource<BrowseRecipeResponse>>
    val liveMyRecipesResponse: LiveData<Resource<BrowseRecipeResponse>>

    init {
        liveBrowseRecipesResponse = Transformations.switchMap(triggerRecipeText) { triggerBrowse ->
            Timber.d("Browse Recipes Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseRecipeResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchRecipesWithText(triggerBrowse)
            }
        }

        liveFeaturedRecipesResponse = Transformations.switchMap(triggerFeaturedRecipes) { triggerBrowse ->
            Timber.d("Browse Featured Recipes Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseRecipeResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchFeaturedRecipes()
            }
        }

        liveMyRecipesResponse = Transformations.switchMap(triggerMyRecipes) { triggerBrowse ->
            Timber.d("Browse My Recipes Trigger received.")
            if (triggerBrowse == null) {
                return@switchMap AbsentLiveData.create<Resource<BrowseRecipeResponse>>()
            } else {
                return@switchMap injectedRepo?.fetchMyRecipes()
            }
        }

    }


}