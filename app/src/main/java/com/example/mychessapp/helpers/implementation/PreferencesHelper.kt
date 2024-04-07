package com.example.mychessapp.helpers.implementation

import android.content.SharedPreferences
import com.example.mychessapp.helpers.Preferences
import javax.inject.Inject

class PreferencesHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Preferences {

    // Saves an Int value to SharedPreferences to a specific name/key
    override fun saveInt(name: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(name, value)
        editor.apply()
    }

    // Loads an Int value from SharedPreferences with a specific name/key
    override fun loadInt(name: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(name, defaultValue)
    }

    // Saves a String value to SharedPreferences to a specific name/key
    override fun saveString(name: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(name, value)
        editor.apply()
    }

    // Loads a String value from SharedPreferences with a specific name/key
    override fun loadString(name: String, defaultValue: String): String? {
        return sharedPreferences.getString(name, defaultValue)
    }

    // Removes a preference value from SharedPreferences
    override fun removePreference(name: String) {
        sharedPreferences.edit().remove(name).apply()
    }
}