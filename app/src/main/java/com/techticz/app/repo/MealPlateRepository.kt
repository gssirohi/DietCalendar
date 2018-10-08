package com.techticz.app.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIRepository
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.OnSuccessListener



/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class MealPlateRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    fun fetchMealPlateResponse(triggerMealPlateID: String?): LiveData<Resource<MealPlateResponse>> {

        var dummyRes = MealPlateResponse()
        var resource = Resource<MealPlateResponse>(Status.LOADING, dummyRes, "Loading Data..", DataSource.LOCAL)
        var live : MediatorLiveData<Resource<MealPlateResponse>> = MediatorLiveData<Resource<MealPlateResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseMealPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLATES.collectionName).document(triggerMealPlateID!!)
                .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
            val mealPlate = documentSnapshot.toObject(MealPlate::class.java!!)
            var fetchedRes = MealPlateResponse()
            fetchedRes.mealPlate = mealPlate
            var resource = Resource<MealPlateResponse>(Status.SUCCESS, fetchedRes, "Data Loading Success", DataSource.REMOTE)
            live.value = resource
        }).addOnFailureListener { e -> Timber.e("Error fetching meal plate:" + triggerMealPlateID, e) }

    return live
    }

    init {
        Timber.d("Injecting:" + this)
    }

    interface Callback {
        fun onPlansFetched(plans:List<DietPlan>)
    }





}