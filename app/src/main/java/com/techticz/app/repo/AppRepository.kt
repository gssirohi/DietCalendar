package com.techticz.dietcalendar.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.networking.model.AppExecutors
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.networking.util.RateLimiter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.support.annotation.NonNull
import android.util.Log
import android.util.Xml
import com.google.android.gms.common.util.IOUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.techticz.app.model.User

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.model.food.*

import com.techticz.powerkit.utils.JSONUtils
import java.lang.reflect.Type


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class AppRepository @Inject
constructor(private val appExecutors: AppExecutors/*, private val syncPrefDao: SyncPrefDao*/, private val db: FirebaseFirestore,private val context:Context) {
    // Fetch again if the data is older than 48 hours
    private val rateLimiter = RateLimiter<String>(48, TimeUnit.HOURS)

    //HotelParcel.buildHotelParcel(hotelDetailResponseContainer);
    //
    val launcherResponse: LiveData<Resource<LauncherResponse>>
        get() {
            Timber.d("Accessing:" + this + "[launchResponse]")
            return fetchLauncherResponse()
        }

    private fun fetchLauncherResponse(): LiveData<Resource<LauncherResponse>> {

       // getUsers()
       // addFoods()

        var resp = LauncherResponse()
        resp.launchMessage = "This is local launch message not from network or DB"
        var resource = Resource<LauncherResponse>(Status.LOADING, resp, "Loading Data..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<LauncherResponse>> =  MediatorLiveData<Resource<LauncherResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        var resourceS = Resource<LauncherResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

        live.value = resourceS
        return live
    }

    private fun addFoods(){
        var jsonString : String = JSONUtils.readJsonFromFile(context,"food.json")
        var listType = object : TypeToken<List<Food>>(){}.type
        var gson:Gson = Gson()
        var cats:List<Food> = gson.fromJson(jsonString,listType)
        var batch:WriteBatch = db.batch()
        for(cat in  cats){
            var ref:DocumentReference = db.collection("foods").document(cat.id)
            batch.set(ref,cat)
        }
        batch.commit().addOnSuccessListener { task->Log.d("Repo","Food insertion success") }
                .addOnFailureListener(OnFailureListener {task->Log.e("Repo","Food insertion failed")  })

    }



    private fun addFood() {
        var food:Food = Food()
        food.id = "Z0001"
        food.basicInfo = BasicInfo()
        food.basicProperty = BasicProperty()
        food.cost = Cost()
        food.additionalInfo = AdditionalInfo()
        food.nutrition = Nutrition()

        db.collection("foods")
                .document(food.id)
                .set(food)
                .addOnSuccessListener { documentReference -> Log.d("appRepo", "Food added with ID: " ) }
                .addOnFailureListener { e -> Log.w("appRepo", "Error adding food", e) }
    }

    private fun getUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("Repo", document.id + " => " + document.data)
                        }
                    } else {
                        Log.w("Repo", "Error getting documents.", task.exception)
                    }
                }
    }

    private fun addNewUser(){
        // Create a new user with a first and last name
        val user = User("Tony","Stark","ironman@marvel.com")

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference -> Timber.d("appRepo", "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Timber.w("appRepo", "Error adding document", e) }

        user.userMiddleName = "Iron Man"
        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference -> Log.d("Repo", "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w("Repo", "Error adding document", e) }

    }

    init {
        Timber.d("Injecting:" + this)
    }


}
