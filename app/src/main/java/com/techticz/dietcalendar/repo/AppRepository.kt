package com.techticz.dietcalendar.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.networking.model.AppExecutors
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.networking.util.RateLimiter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class AppRepository @Inject
constructor(private val appExecutors: AppExecutors/*, private val syncPrefDao: SyncPrefDao, private val baseApi: BaseApi*/) {
    // Fetch again if the data is older than 48 hours
    private val rateLimiter = RateLimiter<String>(48, TimeUnit.HOURS)

    //HotelParcel.buildHotelParcel(hotelDetailResponseContainer);
    //
    val launcherResponse: LiveData<Resource<LauncherResponse>>
        get() {
            Timber.d("Accessing:" + this + "[getSyncedPreferences]")
            return fetchLauncherResponse()
        }

    private fun fetchLauncherResponse(): LiveData<Resource<LauncherResponse>> {
        var resp = LauncherResponse()
        resp.launchMessage = "This is local launch message not from network or DB"
        var resource = Resource<LauncherResponse>(Status.SUCCESS, resp, "Success", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<LauncherResponse>> =  MediatorLiveData<Resource<LauncherResponse>>()
        live.value = resource
        return live
    }


    init {
        Timber.d("Injecting:" + this)
    }


}
