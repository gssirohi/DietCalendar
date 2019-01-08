package com.techticz.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.text.TextUtils
import android.util.Log
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import timber.log.Timber
import javax.inject.Inject
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.techticz.app.model.BrowsePlateResponse
import com.techticz.app.model.meal.Meal
import com.techticz.auth.utils.LoginUtils
import javax.inject.Singleton


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class MealPlateRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    fun fetchMealPlateResponse(meal: Meal?): LiveData<Resource<MealPlateResponse>> {

        var dummyRes = MealPlateResponse()
        var resource = Resource<MealPlateResponse>(Status.LOADING, dummyRes, "Loading Meal Plate:"+meal?.mealPlateId, DataSource.LOCAL)
        var live : MediatorLiveData<Resource<MealPlateResponse>> = MediatorLiveData<Resource<MealPlateResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        if(!TextUtils.isEmpty(meal?.mealPlateId))
        db.collection(AppCollections.PLATES.collectionName).document(meal?.mealPlateId!!)
                .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
            val mealPlate = documentSnapshot.toObject(MealPlate::class.java!!)
            if(mealPlate != null) {
                var fetchedRes = MealPlateResponse()
                fetchedRes.mealPlate = mealPlate
                var resource = Resource<MealPlateResponse>(Status.SUCCESS, fetchedRes, "Loading Success- MealPlate:"+meal?.mealPlateId, DataSource.REMOTE)
                live.value = resource
            } else {
                var resource = Resource<MealPlateResponse>(Status.ERROR, null, "Loading Failed- MealPlate:"+meal?.mealPlateId, DataSource.REMOTE)
                live.value = resource
            }

        }).addOnFailureListener { e -> Timber.e("Error fetching meal plate:" + meal?.mealPlateId, e)
            var resource = Resource<MealPlateResponse>(Status.ERROR, null, "Couldn't load - MealPlate:"+meal?.mealPlateId, DataSource.REMOTE)
            live.value = resource
        }

    return live
    }

    fun fetchPlatesWithText(triggerBrowse: String?): LiveData<Resource<BrowsePlateResponse>>? {
        var resp = BrowsePlateResponse()
        var resource = Resource<BrowsePlateResponse>(Status.LOADING, resp, "Loading plates..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowsePlateResponse>> =  MediatorLiveData<Resource<BrowsePlateResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLATES.collectionName)
               // .whereArrayContains(FieldPath.of("basicInfo","name","searchArray"),triggerBrowse!!)
                .orderBy(FieldPath.of("basicInfo","name","english"))
                .limit(10)
                .startAt(triggerBrowse)
                .endAt(triggerBrowse + '\uf8ff')
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var plates :List<MealPlate> = task.result!!.toObjects(MealPlate::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowsePlateResponse()
                        resp.plates = plates
                        var resource = Resource<BrowsePlateResponse>(Status.SUCCESS, resp, "Loading Success- Plates", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- Plates", task.exception)
                    }
                }
        return live
    }

    fun fetchPlatesForMealType(mealType: String?): LiveData<Resource<BrowsePlateResponse>>? {
        var resp = BrowsePlateResponse()
        var resource = Resource<BrowsePlateResponse>(Status.LOADING, resp, "Loading plates..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowsePlateResponse>> =  MediatorLiveData<Resource<BrowsePlateResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLATES.collectionName)
                .whereArrayContains(FieldPath.of("basicProperty","prefMeals"),mealType!!)
//                .orderBy(FieldPath.of("basicInfo","name","english"))
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var plates :List<MealPlate> = task.result!!.toObjects(MealPlate::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowsePlateResponse()
                        resp.plates = plates
                        var resource = Resource<BrowsePlateResponse>(Status.SUCCESS, resp, "Loading Success- Plates", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- Featured Plates", task.exception)
                    }
                }
        Log.i("Repo", "Loading featured plates for :"+ mealType)
        return live
    }

    fun fetchPlatesForUser(): LiveData<Resource<BrowsePlateResponse>>? {
        var resp = BrowsePlateResponse()
        var resource = Resource<BrowsePlateResponse>(Status.LOADING, resp, "Loading plates..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowsePlateResponse>> =  MediatorLiveData<Resource<BrowsePlateResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.PLATES.collectionName)
                .whereEqualTo(FieldPath.of("adminInfo","createdBy"),LoginUtils.getUserCredential())
//                .orderBy(FieldPath.of("basicInfo","name","english"))
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var plates :List<MealPlate> = task.result!!.toObjects(MealPlate::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowsePlateResponse()
                        resp.plates = plates
                        var resource = Resource<BrowsePlateResponse>(Status.SUCCESS, resp, "Loading Success- Plates", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- Featured Plates", task.exception)
                    }
                }
        Log.i("Repo", "Loading my plates for :"+ LoginUtils.getUserCredential())
        return live
    }

    fun updatePlate(plate: MealPlate, listner: PlateRepositoryCallback) {
        Timber.d("update plate..:"+plate?.id)
        var batch: WriteBatch = db.batch()
        var ref: DocumentReference = db.collection(AppCollections.PLATES.collectionName).document(plate?.id!!)
        batch.set(ref, plate!!)
        batch.commit()
                .addOnSuccessListener { task ->
                    var message =  AppCollections.PLATES.collectionName.toString()+" update plate success"
                    Timber.d(message)
                    //hideProgress()
                    //showSuccess(message!!)
                    listner.onPlateUpdated(plate)
                }
                .addOnFailureListener { task ->
                    var message =  AppCollections.PLATES.collectionName+" update plate failed"
                    Timber.e(message)
                    //hideProgress()
                    //showError(message!!)
                    listner.onPlateUpdateFailure(message)
                }
    }

    fun createPlate(plate: MealPlate, listner: PlateRepositoryCallback) {
        Timber.d("Creating plate..:"+plate.id)
        var batch: WriteBatch = db.batch()
        var ref: DocumentReference = db.collection(AppCollections.PLATES.collectionName).document(plate.id)
        batch.set(ref, plate)
        batch.commit()
                .addOnSuccessListener { task ->
                    var message =  AppCollections.PLATES.collectionName.toString()+" create plate success"
                    Timber.d(message)
                    //hideProgress()
                    //showSuccess(message!!)
                    listner.onPlateCreated(plate)
                }
                .addOnFailureListener { task ->
                    var message =  AppCollections.PLATES.collectionName+" create plate failed"
                    Timber.e(message)
                    //hideProgress()
                    //showError(message!!)
                    listner.onCreatePlateFailure(message)
                }
    }


    init {
        Timber.d("Injecting:" + this)
    }

    interface PlateRepositoryCallback {
        fun onPlateUpdated(plate: MealPlate)
        fun onPlateUpdateFailure(message: String)
        fun onPlateCreated(plate: MealPlate)
        fun onCreatePlateFailure(message: String)
    }





}