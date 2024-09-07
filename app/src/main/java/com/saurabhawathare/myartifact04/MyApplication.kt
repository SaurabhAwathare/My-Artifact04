package com.saurabhawathare.myartifact04

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase when the application is created
        FirebaseApp.initializeApp(this)
    }
}
