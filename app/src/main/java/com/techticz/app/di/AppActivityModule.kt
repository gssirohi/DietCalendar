package com.techticz.dietcalendar.di

import android.content.Context
import com.supercilex.poiandroid.demo.ToolActivity
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.di.BaseActivityModule
import com.techticz.app.ui.activity.DietPlanActivity
import com.techticz.app.ui.activity.*
import com.techticz.dietcalendar.ui.activity.LauncherActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.Provides
import javax.inject.Named


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module(includes = [BaseActivityModule::class])
abstract class AppActivityModule {
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeLauncherActivity(): LauncherActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeDashboardActivity(): DashboardActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeFoodDetailsActivity(): FoodDetailsActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeRecipeDetailsActivity(): RecipeDetailsActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeDeveloperActivity(): DeveloperActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeBrowseDietPlanActivity(): BrowseDietPlansActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeBrowsePlateActivity(): BrowsePlateActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeBrowseRecipeActivity(): BrowseRecipeActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeBrowseFoodActivity(): BrowseFoodActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeDietChartActivity(): DietChartActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeDietPlanActivity(): DietPlanActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeMealPlateActivity(): MealPlateActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeUserProfileActivity(): UserProfileActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeBaseDIActivity(): BaseDIActivity
    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeOnboardingActivity(): OnboardingActivity

    @ContributesAndroidInjector(modules = arrayOf(AppFragmentModule::class))
    abstract fun contributeToolActivity(): ToolActivity

}