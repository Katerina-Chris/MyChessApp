package com.example.mychessapp

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Preferences {

    override fun saveInt(name: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(name, value)
        editor.apply()
    }

    override fun loadInt(name: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(name, defaultValue)
    }

    override fun saveString(name: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(name, value)
        editor.apply()
    }

    override fun loadString(name: String, defaultValue: String): String? {
        return sharedPreferences.getString(name, defaultValue)
    }

    override fun removePreference(name: String) {
        sharedPreferences.edit().remove(name).apply()
    }
}