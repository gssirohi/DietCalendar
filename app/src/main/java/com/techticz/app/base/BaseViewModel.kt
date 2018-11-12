package com.techticz.app.base

import android.arch.lifecycle.ViewModel
import com.techticz.dietcalendar.ui.DietCalendarApplication
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
open class BaseViewModel : ViewModel(){
    init {
        Timber.d("Injecting:" + this)
        DietCalendarApplication.getAppComponent().inject(this as BaseViewModel)
    }
}