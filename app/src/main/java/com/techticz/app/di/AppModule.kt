package com.techticz.dietcalendar.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.repo.ImageRepository
import com.techticz.app.ui.Navigator
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.networking.model.AppExecutors
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.db.AppDatabase
import com.techticz.app.di.ContextModule
import com.techticz.app.di.RepoModule

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module(includes = [
(ContextModule::class),(ViewModelModule::class),(RepoModule::class)
])
class AppModule{
    @Provides
    internal fun providesWelcomeMessage(): String {
        return "Welcome to Diet Calendar!"
    }

/*
    @Provides
    internal fun providesNavigator(context:Context): Navigator {
        return Navigator(context)
    }
*/

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
    internal fun provideAppDb(application:DietCalendarApplication): AppDatabase {
        Timber.d("Providing :" + "AppDatabase")
        // Access a Cloud Firestore instance from your Activity
        val db = AppDatabase.getAppDataBase(application)

        return db
    }

   /* @Singleton
    @Provides
    internal fun provideImageRepo(context:Context): ImageRepository {
        Timber.d("Providing :" + "Image Repository")
        val repo = ImageRepository(context)

        return repo
    }*/





    @Singleton
    @Provides
    internal fun provideExecutors(): AppExecutors {
        Timber.d("Providing :" + "AppExecutors")
        return AppExecutors()
    }

}