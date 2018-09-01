package com.techticz.dietcalendar.di

import com.techticz.dietcalendar.ui.activity.LauncherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module
abstract class AppActivityModule {
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeLauncherActivity(): LauncherActivity
}