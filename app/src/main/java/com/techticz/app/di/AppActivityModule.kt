package com.techticz.dietcalendar.di

import com.techticz.app.base.BaseDIActivity
import com.techticz.app.ui.activity.DietPlanActivity
import com.techticz.app.ui.activity.*
import com.techticz.dietcalendar.ui.activity.LauncherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Module
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
}