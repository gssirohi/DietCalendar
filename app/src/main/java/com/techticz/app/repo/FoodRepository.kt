package com.techticz.app.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.FoodResponse
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import com.techticz.app.model.BrowseFoodResponse
import timber.log.Timber
import javax.inject.Inject
import com.techticz.app.model.food.Food
import com.techticz.app.model.mealplate.FoodItem
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class FoodRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {


    fun fetchFoodResponse(foodItem: FoodItem?): LiveData<Resource<FoodResponse>> {

        var dummyRes = FoodResponse()
        var resource = Resource<FoodResponse>(Status.LOADING, dummyRes, "Loading Food:"+foodItem?.id, DataSource.LOCAL)
        var live : MediatorLiveData<Resource<FoodResponse>> = MediatorLiveData<Resource<FoodResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.FOODS.collectionName).document(foodItem?.id!!)
                .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
            val food = documentSnapshot.toObject(Food::class.java!!)
            if(food != null) {
                var fetchedRes = FoodResponse()
                fetchedRes.food = food
                var resource = Resource<FoodResponse>(Status.SUCCESS, fetchedRes, "Loading Success- Food:"+foodItem?.id, DataSource.REMOTE)
                live.value = resource
            } else {
                var resource = Resource<FoodResponse>(Status.ERROR, null, "Loading Failed- Food:"+foodItem?.id, DataSource.REMOTE)
                live.value = resource
            }
        }).addOnFailureListener { e -> Timber.e("Error fetching food:" + foodItem?.id, e)
            var resource = Resource<FoodResponse>(Status.ERROR, null, "Couldn't load- Food:"+foodItem?.id, DataSource.REMOTE)
            live.value = resource
        }

    return live
    }

    fun fetchFoodsWithText(triggerBrowse: String?): LiveData<Resource<BrowseFoodResponse>>? {
        var resp = BrowseFoodResponse()
        var resource = Resource<BrowseFoodResponse>(Status.LOADING, resp, "Loading foods..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowseFoodResponse>> =  MediatorLiveData<Resource<BrowseFoodResponse>>()
        live.value = resource

        db.collection(AppCollections.FOODS.collectionName)
                // .whereArrayContains(FieldPath.of("basicInfo","name","searchArray"),triggerBrowse!!)
                .orderBy(FieldPath.of("basicInfo","name","english"))
                .limit(10)
                .startAt(triggerBrowse)
                .endAt(triggerBrowse + '\uf8ff')
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var foods :List<Food> = task.result!!.toObjects(Food::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowseFoodResponse()
                        resp.foods = foods
                        var resource = Resource<BrowseFoodResponse>(Status.SUCCESS, resp, "Loading Success- foods", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- foods", task.exception)
                    }
                }
        return live
    }


    init {
        Timber.d("Injecting:" + this)
    }

    interface Callback {
        fun onFoodFetched(food: Food)
    }





}