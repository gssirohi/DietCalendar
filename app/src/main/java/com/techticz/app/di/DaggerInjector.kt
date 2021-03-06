package com.techticz.dietcalendar.di


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.app.base.Injectable

import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber

/**
 * Authored by vipulkumar on 19/09/17.
 */

object DaggerInjector {

    fun injectAll(application: DietCalendarApplication): AppComponent {
        Timber.d("...All DI Started...")
        var component:AppComponent = DaggerAppComponent.builder().application(application)
                .build()
                component.inject(application)
        application
                .registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        handleActivity(activity)
                    }

                    override fun onActivityStarted(activity: Activity) {

                    }

                    override fun onActivityResumed(activity: Activity) {

                    }

                    override fun onActivityPaused(activity: Activity) {

                    }

                    override fun onActivityStopped(activity: Activity) {

                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

                    }

                    override fun onActivityDestroyed(activity: Activity) {

                    }
                })
        return component;
    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasSupportFragmentInjector) {
            Timber.d("Injecting activity: " + activity.javaClass.canonicalName)
            AndroidInjection.inject(activity)
        }
        (activity as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
                object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(fm: androidx.fragment.app.FragmentManager, f: androidx.fragment.app.Fragment, savedInstanceState: Bundle?) {
                        if (f is Injectable) {
                            Timber.d("Injecting fragment: " + f.javaClass.canonicalName)
                            AndroidSupportInjection.inject(f)
                        }
                    }

                }, true)
    }
}
