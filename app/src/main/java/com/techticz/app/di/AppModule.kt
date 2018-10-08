package com.techticz.dietcalendar.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.ui.Navigator
import com.techticz.dietcalendar.ui.DietCalendarApplication

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

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

    @Provides
    internal fun providesNavigator(context:Context): Navigator {
        return Navigator(context)
    }

    @Singleton
    @Provides
    internal fun provideDb(): FirebaseFirestore {
        Timber.d("Providing :" + "FirebaseFirestore DB")
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        return db
    }

    @Singleton
    @Provides
    internal fun provideContext(app: DietCalendarApplication): Context {
        Timber.d("Providing :" + "App Context")
        return app.baseContext
    }

}