package com.example.locater

import android.app.Application

class MyAppInstance: Application() {
    companion object {
        private lateinit var instance: MyAppInstance

        // Access the application instance from anywhere in your code
        fun getInstance(): MyAppInstance {
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}