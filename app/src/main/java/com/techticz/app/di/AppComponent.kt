package com.techticz.dietcalendar.di

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

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
}
