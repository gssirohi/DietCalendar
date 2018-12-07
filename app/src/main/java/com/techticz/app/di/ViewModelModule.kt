package com.techticz.dietcalendar.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techticz.dietcalendar.viewmodel.LauncherViewModel
import com.techticz.app.base.BaseViewModelFactory
import com.techticz.app.base.ViewModelKey
import com.techticz.app.viewmodel.*
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
    @ViewModelKey(BrowsePlateViewModel::class)
    internal abstract fun bindBrowsePlateViewModel(browserPlateViewModel: BrowsePlateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowseRecipeViewModel::class)
    internal abstract fun bindBrowseRecipeViewModel(browserRecipeViewModel: BrowseRecipeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowseFoodViewModel::class)
    internal abstract fun bindBrowseFoodViewModel(browserRecipeViewModel: BrowseFoodViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MealPlateViewModel::class)
    internal abstract fun bindMealPlateViewModel(mealPlateViewModel: MealPlateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel::class)
    internal abstract fun bindRecipeViewModel(recipeViewModel: RecipeViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(DietChartViewModel::class)
    internal abstract fun bindDietChartViewModel(dietChartViewModel: DietChartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    internal abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    internal abstract fun bindBaseViewModelFactory(factory: BaseViewModelFactory): ViewModelProvider.Factory
}
