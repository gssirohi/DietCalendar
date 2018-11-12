package com.techticz.app.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.repo.*
import com.techticz.dietcalendar.ui.DietCalendarApplication
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module
class ContextModule {

    @Singleton
    @Provides
    internal fun provideAppContext(app: DietCalendarApplication): Context {
        Timber.d("Providing :" + "Context")
        return app.baseContext
    }

}