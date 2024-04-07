package com.example.mychessapp

import android.app.Application
import com.example.mychessapp.helpers.Preferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyChessApp: Application() {
    @Inject
    lateinit var preferences: Preferences
}