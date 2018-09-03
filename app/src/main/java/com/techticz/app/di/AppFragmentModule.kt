package com.techticz.dietcalendar.di

import com.techticz.dietcalendar.ui.frag.LauncherFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

@Module
abstract class AppFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeLauncherFragment(): LauncherFragment

}