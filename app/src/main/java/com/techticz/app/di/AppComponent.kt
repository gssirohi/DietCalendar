package com.techticz.dietcalendar.di

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

import com.techticz.app.base.BaseViewModel
import com.techticz.app.repo.ImageRepository
import com.techticz.app.viewmodel.*
import com.techticz.dietcalendar.ui.DietCalendarApplication
import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@Singleton
@Component(modules = [(AndroidInjectionModule::class),

(AppActivityModule::class),
(AppModule::class)
])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: DietCalendarApplication): Builder

        fun build(): AppComponent
    }

    fun inject(app: DietCalendarApplication)

    fun inject(viewModel: UserViewModel)
    fun inject(viewModel: ImageViewModel)
    fun inject(viewModel: FoodViewModel)
    fun inject(viewModel: RecipeViewModel)
    fun inject(viewModel: MealPlateViewModel)
    fun inject(viewModel: DietChartViewModel)
    fun inject(viewModel: BaseViewModel)
}
