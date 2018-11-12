package com.techticz.app.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.repo.*
import com.techticz.app.ui.Navigator
import com.techticz.dietcalendar.di.ViewModelModule
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.networking.model.AppExecutors
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module
class RepoModule {

    @Singleton
    @Provides
    internal fun provideImageRepo(context: Context): ImageRepository {
        Timber.d("Providing :" + "ImageRepository")
        val repo = ImageRepository(context)

        return repo
    }

    @Singleton
    @Provides
    internal fun provideUserRepo(db: FirebaseFirestore): UserRepository {
        Timber.d("Providing :" + "UserRepository")
        val repo = UserRepository(db)

        return repo
    }

    @Singleton
    @Provides
    internal fun provideFoodRepo(db: FirebaseFirestore): FoodRepository {
        Timber.d("Providing :" + "FoodRepository")
        val repo = FoodRepository(db)

        return repo
    }

    @Singleton
    @Provides
    internal fun provideRecipeRepo(db: FirebaseFirestore): RecipeRepository {
        Timber.d("Providing :" + "RecipeRepository")
        val repo = RecipeRepository(db)

        return repo
    }

    @Singleton
    @Provides
    internal fun provideMealPlateRepo(db: FirebaseFirestore): MealPlateRepository {
        Timber.d("Providing :" + "MealPlateRepository")
        val repo = MealPlateRepository(db)

        return repo
    }

    @Singleton
    @Provides
    internal fun provideDietPlanRepo(db: FirebaseFirestore): DietPlanRepository {
        Timber.d("Providing :" + "DietPlanRepository")
        val repo = DietPlanRepository(db)

        return repo
    }

}