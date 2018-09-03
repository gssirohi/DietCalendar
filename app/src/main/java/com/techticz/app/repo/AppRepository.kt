package com.techticz.dietcalendar.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.google.firebase.firestore.FirebaseFirestore
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
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.DocumentReference
import com.google.android.gms.tasks.OnSuccessListener
import com.techticz.app.model.User
import com.google.firebase.firestore.QueryDocumentSnapshot

import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener




/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class AppRepository @Inject
constructor(private val appExecutors: AppExecutors/*, private val syncPrefDao: SyncPrefDao*/, private val db: FirebaseFirestore) {
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
        getUsers()
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
