package com.techticz.app.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.model.User
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.*
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.networking.model.AppExecutors
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.networking.util.RateLimiter
import com.techticz.powerkit.base.BaseDIRepository
import com.techticz.powerkit.utils.JSONUtils
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class DietPlanRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    private fun fetchDietPlans():LiveData<Resource<BrowseMealPlanResponse>>{

        var resp = BrowseMealPlanResponse()
        var resource = Resource<BrowseMealPlanResponse>(Status.LOADING, resp, "Loading Data..", DataSource.LOCAL)
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
                        var resource = Resource<BrowseMealPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.w("Repo", "Error getting documents.", task.exception)
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