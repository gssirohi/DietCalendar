package com.techticz.dietcalendar.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.dietcalendar.repo.AppRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.powerkit.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class LauncherViewModel @Inject
constructor(baseRepository: AppRepository) : BaseViewModel() {
    val triggerLaunch = MutableLiveData<Boolean>()
    val launcherResponse: LiveData<Resource<LauncherResponse>>

    val loadingMessage: String
        get() = /*if (syncResponse.value != null && syncResponse.value!!.data != null)
        syncResponse.value!!.data!!.upgradeBtnMsg else */"Loading Configurations.."


    init {
        Timber.d("Injecting:" + this)
        launcherResponse = Transformations.switchMap(triggerLaunch) { triggerLaunch ->
            Timber.d("Launch Trigger received.")
            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData . create < Resource < LauncherResponse > > ()
            } else {
                return@switchMap baseRepository .launcherResponse
            }
        }
    }


}
