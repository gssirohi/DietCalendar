package com.techticz.dietcalendar.di

import android.arch.persistence.room.Room
import com.google.firebase.firestore.FirebaseFirestore

import com.techticz.powerkit.constant.Environment
import dagger.Module
import dagger.Provides
import timber.log.Timber
import java.io.IOException
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

    @Singleton
    @Provides
    internal fun provideDb(): FirebaseFirestore {
        Timber.d("Providing :" + "FirebaseFirestore DB")
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        return db
    }

}