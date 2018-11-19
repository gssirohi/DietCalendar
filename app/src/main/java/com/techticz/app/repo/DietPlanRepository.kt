package com.techticz.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import com.techticz.auth.utils.LoginUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class DietPlanRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    private fun fetchPublishedDietPlans():LiveData<Resource<BrowseDietPlanResponse>>{

        var resp = BrowseDietPlanResponse()
        var resource = Resource<BrowseDietPlanResponse>(Status.LOADING, resp, "Loading dietplans..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowseDietPlanResponse>> =  MediatorLiveData<Resource<BrowseDietPlanResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLANS.collectionName)
                .whereEqualTo(FieldPath.of("adminInfo","published"),true)
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

    private fun fetchMyDietPlans():LiveData<Resource<BrowseDietPlanResponse>>{

        var resp = BrowseDietPlanResponse()
        var resource = Resource<BrowseDietPlanResponse>(Status.LOADING, resp, "Loading my dietplans..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowseDietPlanResponse>> =  MediatorLiveData<Resource<BrowseDietPlanResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLANS.collectionName)
                .whereEqualTo(FieldPath.of("adminInfo","createdBy"),LoginUtils.getUserCredential())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var plans :List<DietPlan> = task.result!!.toObjects(DietPlan::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowseDietPlanResponse()
                        resp.plans = plans
                        var resource = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Loading Success- My DietPlans", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- My DietPlans", task.exception)
                    }
                }
        return live
    }

    init {

    }

    interface Callback {
        fun onPlansFetched(plans:List<DietPlan>)
    }


    val browsePublishedDietPlansResponseData: LiveData<Resource<BrowseDietPlanResponse>>
        get() {
            Timber.d("Accessing:" + this + "[BrowseDietPlans]")
            return fetchPublishedDietPlans()
        }

    val browseMyDietPlansResponseData: LiveData<Resource<BrowseDietPlanResponse>>
        get() {
            Timber.d("Accessing:" + this + "[BrowseDietPlans]")
            return fetchMyDietPlans()
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

    fun createDietPlan(newPlan: DietPlan,listner:DietPlanCallBack) {
        Timber.d("Creating plan..:"+newPlan.id)
        var batch: WriteBatch = db.batch()
        var ref: DocumentReference = db.collection(AppCollections.PLANS.collectionName).document(newPlan.id)
        batch.set(ref, newPlan)
        batch.commit()
                .addOnSuccessListener { task ->
                    var message =  AppCollections.PLANS.collectionName.toString()+" create plan success"
                    Timber.d(message)
                    //hideProgress()
                    //showSuccess(message!!)
                    listner.onPlanCreated(newPlan)
                }
                .addOnFailureListener { task ->
                    var message =  AppCollections.PLANS.collectionName+" create plan failed"
                    Timber.e(message)
                    //hideProgress()
                    //showError(message!!)
                    listner.onCreatePlanFailure()
                }
    }

    fun updatePlan(dietPlan: DietPlan?, listner: DietPlanCallBack) {
        Timber.d("update plan..:"+dietPlan?.id)
        var batch: WriteBatch = db.batch()
        var ref: DocumentReference = db.collection(AppCollections.PLANS.collectionName).document(dietPlan?.id!!)
        batch.set(ref, dietPlan!!)
        batch.commit()
                .addOnSuccessListener { task ->
                    var message =  AppCollections.PLANS.collectionName.toString()+" update plan success"
                    Timber.d(message)
                    //hideProgress()
                    //showSuccess(message!!)
                    listner.onPlanUpdated(dietPlan)
                }
                .addOnFailureListener { task ->
                    var message =  AppCollections.PLANS.collectionName+" update plan failed"
                    Timber.e(message)
                    //hideProgress()
                    //showError(message!!)
                    listner.onPlanUpdateFailure()
                }
    }

    interface DietPlanCallBack{
        fun onPlanCreated(planId: DietPlan)
        fun onCreatePlanFailure()
        fun onPlanUpdated(plan:DietPlan)
        fun onPlanUpdateFailure()
    }
}