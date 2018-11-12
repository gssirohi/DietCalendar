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
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.model.food.*

import com.techticz.powerkit.utils.JSONUtils


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
        var resource = Resource<LauncherResponse>(Status.LOADING, resp, "Loading launching data..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<LauncherResponse>> =  MediatorLiveData<Resource<LauncherResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        var resourceS = Resource<LauncherResponse>(Status.SUCCESS, resp, "Loading Success: Launcher data", DataSource.LOCAL)

        live.value = resourceS
        return live
    }


    init {

    }


}
