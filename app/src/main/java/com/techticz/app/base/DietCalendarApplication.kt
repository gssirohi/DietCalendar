package com.techticz.dietcalendar.ui

import android.app.Activity
import android.app.Application
import com.techticz.app.base.DebugTree
import com.techticz.app.base.ReleaseTree
import com.techticz.app.viewmodel.UserViewModel
import com.techticz.dietcalendar.BuildConfig
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
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
        Timber.d("Diet Calendar Application Injected ...")
        component = DaggerInjector.injectAll(this)
        staticUserViewModel = UserViewModel()
        component.inject(staticUserViewModel)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }


    companion object {
        private lateinit var component: AppComponent
        private lateinit var staticUserViewModel: UserViewModel

        fun getAppComponent() : AppComponent = component
        fun getAppUserViewModel() : UserViewModel = staticUserViewModel
    }
}