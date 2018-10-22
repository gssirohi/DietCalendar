package com.techticz.dietcalendar.ui

import android.app.Activity
import android.app.Application
import com.techticz.dietcalendar.di.AppComponent
import com.techticz.dietcalendar.di.DaggerInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
class DietCalendarApplication : Application(), HasActivityInjector {
    @Inject
    public lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (com.techticz.dietcalendar.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("Diet Calendar Application Injected ...")
        component = DaggerInjector.injectAll(this)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }


    companion object {
        private lateinit var component: AppComponent

        fun getAppComponent() : AppComponent = component
    }
}