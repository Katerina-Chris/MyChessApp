package com.example.mychessapp

interface Preferences {
    fun saveInt(name:String, value: Int)
    fun loadInt(name:String, defaultValue: Int): Int
    fun saveString(name:String, value: String)
    fun loadString(name:String, defaultValue: String): String?
    fun removePreference(name:String)
}