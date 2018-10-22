package com.techticz.app.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class DietPlanRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    private fun fetchDietPlans():LiveData<Resource<BrowseMealPlanResponse>>{

        var resp = BrowseMealPlanResponse()
        var resource = Resource<BrowseMealPlanResponse>(Status.LOADING, resp, "Loading dietplans..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowseMealPlanResponse>> =  MediatorLiveData<Resource<BrowseMealPlanResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseMealPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLANS.collectionName)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var plans :List<DietPlan> = task.result.toObjects(DietPlan::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowseMealPlanResponse()
                        resp.plans = plans
                        var resource = Resource<BrowseMealPlanResponse>(Status.SUCCESS, resp, "Loading Success- DietPlans", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- DietPlans", task.exception)
                    }
                }
        return live
    }

    init {
        Timber.d("Injecting:" + this)
    }

    interface Callback {
        fun onPlansFetched(plans:List<DietPlan>)
    }


    val dietPlanResponseData: LiveData<Resource<BrowseMealPlanResponse>>
        get() {
            Timber.d("Accessing:" + this + "[launchResponse]")
            return fetchDietPlans()
        }
}