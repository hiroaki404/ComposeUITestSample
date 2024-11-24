package com.example.composeuitestsample

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        println("Application onCreate")
    }
}
