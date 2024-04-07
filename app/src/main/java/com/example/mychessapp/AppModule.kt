package com.example.mychessapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.mychessapp.helpers.FindSolution
import com.example.mychessapp.helpers.implementation.FindSolutionHelper
import com.example.mychessapp.helpers.Preferences
import com.example.mychessapp.helpers.implementation.PreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences {
        return app.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferences(sharedPreferences: SharedPreferences): Preferences {
        return PreferencesHelper(sharedPreferences)
    }

    @Provides
    fun provideFindSolution(): FindSolution {
        return FindSolutionHelper()
    }
}