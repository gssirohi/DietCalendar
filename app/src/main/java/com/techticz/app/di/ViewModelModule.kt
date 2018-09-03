package com.techticz.dietcalendar.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.techticz.dietcalendar.viewmodel.LauncherViewModel
import com.techticz.powerkit.base.BaseViewModelFactory
import com.techticz.powerkit.base.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */


@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    internal abstract fun bindLauncherViewModel(launcherViewModel: LauncherViewModel): ViewModel

    @Binds
    internal abstract fun bindBaseViewModelFactory(factory: BaseViewModelFactory): ViewModelProvider.Factory
}
