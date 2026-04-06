package com.example.ncs3

import android.app.Application
import com.google.firebase.FirebaseApp

class MediCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this)
    }
}