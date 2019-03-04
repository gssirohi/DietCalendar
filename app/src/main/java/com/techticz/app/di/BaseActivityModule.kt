package com.techticz.app.di

import dagger.Module
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.techticz.app.base.BaseDIActivity
import dagger.Binds
import javax.inject.Scope


@Module
public abstract class BaseActivityModule{

    @Binds
    internal abstract fun activity(baseActivity: AppCompatActivity): AppCompatActivity

   /* @Binds
    internal abstract fun activityContext(baseActivity: AppCompatActivity): Context*/
}