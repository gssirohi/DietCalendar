package com.techticz.app.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.FoodResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.Food
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class DietPlanRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    private fun fetchDietPlans():LiveData<Resource<BrowseDietPlanResponse>>{

        var resp = BrowseDietPlanResponse()
        var resource = Resource<BrowseDietPlanResponse>(Status.LOADING, resp, "Loading dietplans..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowseDietPlanResponse>> =  MediatorLiveData<Resource<BrowseDietPlanResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLANS.collectionName)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var plans :List<DietPlan> = task.result!!.toObjects(DietPlan::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowseDietPlanResponse()
                        resp.plans = plans
                        var resource = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Loading Success- DietPlans", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- DietPlans", task.exception)
                    }
                }
        return live
    }

    init {

    }

    interface Callback {
        fun onPlansFetched(plans:List<DietPlan>)
    }


    val browseDietPlansResponseData: LiveData<Resource<BrowseDietPlanResponse>>
        get() {
            Timber.d("Accessing:" + this + "[BrowseDietPlans]")
            return fetchDietPlans()
        }

    fun fetchDietPlan(dietPlanId: String?): MediatorLiveData<Resource<DietPlanResponse>> {

        var dummyRes = DietPlanResponse()
        var resource = Resource<DietPlanResponse>(Status.LOADING, dummyRes, "Loading DietPlan:"+dietPlanId, DataSource.LOCAL)
        var live : MediatorLiveData<Resource<DietPlanResponse>> = MediatorLiveData<Resource<DietPlanResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLANS.collectionName).document(dietPlanId!!)
                .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
            val dietPlan = documentSnapshot.toObject(DietPlan::class.java!!)
            if(dietPlan != null) {
                var fetchedRes = DietPlanResponse()
                fetchedRes.dietPlan = dietPlan
                var resource = Resource<DietPlanResponse>(Status.SUCCESS, fetchedRes, "Loading Success- DietPlan:"+dietPlanId, DataSource.REMOTE)
                live.value = resource
            } else {
                var resource = Resource<DietPlanResponse>(Status.ERROR, null, "Loading Failed- DietPlan:"+dietPlanId, DataSource.REMOTE)
                live.value = resource
            }
        }).addOnFailureListener { e -> Timber.e("Error fetching DietPlan:" +dietPlanId, e)
            var resource = Resource<DietPlanResponse>(Status.ERROR, null, "Couldn't load- DietPlan:"+dietPlanId, DataSource.REMOTE)
            live.value = resource
        }

        return live
    }
}