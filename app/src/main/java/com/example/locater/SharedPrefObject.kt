package com.example.locater

import android.content.Context
import android.content.SharedPreferences

object SharedPrefObject {

    private const val NAME = "SpinKotlin"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    //getter
    fun putBooleanLogin(key:String, value:Boolean)
    {
        preferences.edit().putBoolean(key,value).apply()
    }

    fun getBooleanLogin(key: String):Boolean
    {
        return preferences.getBoolean(key,false)
    }

}