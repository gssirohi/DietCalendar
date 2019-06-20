package com.techticz.app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.app.base.BaseViewModel
import com.techticz.app.model.BrowseRecipeResponse
import com.techticz.app.model.kitchen.SyncKitchenRequest
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.MealPlateRepository

import com.techticz.app.repo.RecipeRepository
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Status
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class KitchenViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedFoodRepo: FoodRepository
    @Inject
    lateinit var injectedRecipeRepo: RecipeRepository
    @Inject
    lateinit var injectedPlateRepo: MealPlateRepository
    @Inject
    lateinit var injectedPlanRepo: DietPlanRepository

    val triggerSyncKitchen = MutableLiveData<SyncKitchenRequest>()
    val liveSyncKitchenResponse: LiveData<Resource<String>>

    init {
        liveSyncKitchenResponse = Transformations.switchMap(triggerSyncKitchen) { triggerSync ->
            Timber.d("Browse Recipes Trigger received.")
            if (triggerSync == null || lifeCycleOwner == null) {
                return@switchMap AbsentLiveData.create<Resource<String>>()
            } else {
                return@switchMap syncKitchen(triggerSync,lifeCycleOwner)
            }
        }

    }

    fun syncKitchen(sync:SyncKitchenRequest,lifecycleOwner: LifecycleOwner):LiveData<Resource<String>>?{
        var resp = "all"
        var resource = Resource<String>(Status.LOADING, resp, "Syncing documents..", DataSource.LOCAL)
        var live : MediatorLiveData<Resource<String>> =  MediatorLiveData<Resource<String>>()
        live.value = resource
        var foodSynced = !sync.food
        var recipeSynced = !sync.recipe
        var plateSynced = !sync.plate
        var planSynced = !sync.plan

        if(sync.food) {
           var foodStatus  = injectedFoodRepo.sync()
            foodStatus?.observe(lifecycleOwner, Observer { result->
                when(result.status){
                    Status.SUCCESS->{
                        foodSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else if(result.data.equals("synced",true)){
                            Timber.d("Kitchen","food sync completed!")
                            var resource = Resource<String>(Status.SUCCESS, "food", result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                    Status.ERROR->{
                        foodSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else {
                            var resource = Resource<String>(Status.ERROR, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                }


            })
        }

        if(sync.recipe) {
            var recipeStatus  = injectedRecipeRepo.sync()
            recipeStatus?.observe(lifecycleOwner, Observer { result->
                when(result.status){
                    Status.SUCCESS->{
                        recipeSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else if(result.data.equals("synced",true)){
                            Timber.d("Kitchen","recipe sync completed!")
                            var resource = Resource<String>(Status.SUCCESS, "recipe", result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                    Status.ERROR->{
                        recipeSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else {
                            var resource = Resource<String>(Status.ERROR, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                }


            })
        }
        if(sync.plate) {
            var plateStatus  = injectedPlateRepo.sync()
            plateStatus?.observe(lifecycleOwner, Observer { result->
                when(result.status){
                    Status.SUCCESS->{
                        plateSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else if(result.data.equals("synced",true)){
                            Timber.d("Kitchen","plate sync completed!")
                            var resource = Resource<String>(Status.SUCCESS, "plate", result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                    Status.ERROR->{
                        plateSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else {
                            var resource = Resource<String>(Status.ERROR, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                }

            })
        }
        if(sync.plan) {
            var planStatus  = injectedPlanRepo.sync()
            planStatus?.observe(lifecycleOwner, Observer { result->
                when(result.status){
                    Status.SUCCESS->{
                        planSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            Timber.d("Kitchen","all doc sync completed!")
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else if(result.data.equals("synced",true)){
                            Timber.d("Kitchen","plan sync completed!")
                            var resource = Resource<String>(Status.SUCCESS, "plan", result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                    Status.ERROR->{
                        planSynced = true

                        if(foodSynced && recipeSynced && plateSynced && planSynced){
                            var resource = Resource<String>(Status.COMPLETE, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        } else {
                            var resource = Resource<String>(Status.ERROR, resp, result.message, DataSource.LOCAL)
                            live.value = resource
                        }
                    }
                }


            })
        }

        return live

    }

    private lateinit var lifeCycleOwner: LifecycleOwner

    fun triggerKitchenSync(syncRequest:SyncKitchenRequest, lifecycleOwner: LifecycleOwner){
        this.lifeCycleOwner = lifecycleOwner
        triggerSyncKitchen.value = syncRequest
    }
}