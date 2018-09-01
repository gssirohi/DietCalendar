package com.techticz.dietcalendar.di

import dagger.Module
import dagger.Provides

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module(includes = [
(ViewModelModule::class)
])
class AppModule{
    @Provides
    internal fun providesWelcomeMessage(): String {
        return "Welcome to Dagger 2. You have successfully injected dependency!"
    }

}