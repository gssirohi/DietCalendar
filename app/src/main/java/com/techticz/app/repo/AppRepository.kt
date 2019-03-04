package com.techticz.dietcalendar.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.food.*
import com.techticz.app.model.launch.Launching

import com.techticz.powerkit.utils.JSONUtils


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class AppRepository @Inject
constructor(private val appExecutors: AppExecutors/*, private val syncPrefDao: SyncPrefDao*/, private val db: FirebaseFirestore,private val context:Context) {
    // Fetch again if the data is older than 48 hours
    private val rateLimiter = RateLimiter<String>(48, TimeUnit.HOURS)

    val launcherResponse: LiveData<Resource<LauncherResponse>>
        get() {
            Timber.d("Accessing:" + this + "[launchResponse]")
            return fetchLauncherResponse()
        }

    private fun fetchLauncherResponse(): LiveData<Resource<LauncherResponse>> {
        var resp = LauncherResponse()
        resp.launching = Launching().apply { launchCode = "local"
        launchMessage = "Local launching"}
        var resource = Resource<LauncherResponse>(Status.LOADING, resp, "Loading launching data..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<LauncherResponse>> =  MediatorLiveData<Resource<LauncherResponse>>()
        live.value = resource

        db.collection(AppCollections.LAUNCHINGS.collectionName).document("android")
                .get()
                /*.addOnSuccessListener { documentSnapshot ->
                    val launching = documentSnapshot.toObject(Launching::class.java!!)
                    if(launching != null) {
                        Log.d("AppRepo","CACHED response received")
                        var fetchedRes = LauncherResponse()
                        fetchedRes.launching = launching
                        var resource = Resource<LauncherResponse>(Status.SUCCESS, fetchedRes, "Loading Success- Launching:"+launching?.id, DataSource.REMOTE)
                        live.value = resource
                    } else {
                        Log.d("AppRepo","CACHED response received as NULL")
                        var resource = Resource<LauncherResponse>(Status.ERROR, null, "Loading Failed- Launching", DataSource.REMOTE)
                        live.value = resource
                    }
                }.addOnFailureListener { e -> Timber.e("Error fetching launching from cache", e)
                    var resource = Resource<LauncherResponse>(Status.ERROR, null, "Couldn't load- launching:android", DataSource.REMOTE)
                    live.value = resource
                }*/.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val launching = task.result?.toObject(Launching::class.java!!)
                        if(launching != null) {
                            Log.d("AppRepo","CACHED response received")
                            var fetchedRes = LauncherResponse()
                            fetchedRes.launching = launching
                            var resource = Resource<LauncherResponse>(Status.SUCCESS, fetchedRes, "Loading Task Success- Launching:"+launching?.id, DataSource.REMOTE)
                            live.value = resource
                        } else {
                            Log.d("AppRepo","CACHED response received as NULL")
                            var resource = Resource<LauncherResponse>(Status.ERROR, null, "Loading Task Success- Launching Null", DataSource.REMOTE)
                            live.value = resource
                        }
                    } else {

                        Log.e("Repo", "Loading Failed- Launching from Cache", task.exception)
                        var resource = Resource<LauncherResponse>(Status.ERROR, null, "Loading Task Failed- Launching", DataSource.REMOTE)
                        live.value = resource

                        db.collection(AppCollections.LAUNCHINGS.collectionName).document("android")
                                .get(Source.SERVER)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val launching = task.result?.toObject(Launching::class.java!!)
                                        if(launching != null) {
                                            Log.d("AppRepo","SERVER response received")
                                            var fetchedRes = LauncherResponse()
                                            fetchedRes.launching = launching
                                            var resource = Resource<LauncherResponse>(Status.SUCCESS, fetchedRes, "Loading Task Success- Launching:"+launching?.id, DataSource.REMOTE)
                                            live.value = resource
                                        } else {
                                            Log.d("AppRepo","SERVER response received as NULL")
                                            var resource = Resource<LauncherResponse>(Status.ERROR, null, "Loading Task Success- Launching Null", DataSource.REMOTE)
                                            live.value = resource
                                        }
                                    } else {

                                        Log.e("Repo", "Loading Failed- Launching from Server", task.exception)
                                        var resource = Resource<LauncherResponse>(Status.ERROR, null, "Loading Task Failed- Launching", DataSource.REMOTE)
                                        live.value = resource
                                    }
                                }
                    }
                }

        return live

    }


    init {
        Timber.d("Injected")
    }

}
