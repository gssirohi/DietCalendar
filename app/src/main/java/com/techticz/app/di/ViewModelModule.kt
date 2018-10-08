package com.techticz.dietcalendar.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.viewmodel.BrowseDietPlanViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
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
    @IntoMap
    @ViewModelKey(BrowseDietPlanViewModel::class)
    internal abstract fun bindBrowseDietPlanViewModel(browserPlanViewModel: BrowseDietPlanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MealPlateViewModel::class)
    internal abstract fun bindMealPlateViewModel(mealPlateViewModel: MealPlateViewModel): ViewModel


    @Binds
    internal abstract fun bindBaseViewModelFactory(factory: BaseViewModelFactory): ViewModelProvider.Factory
}
