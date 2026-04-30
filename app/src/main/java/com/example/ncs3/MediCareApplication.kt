package com.example.ncs3

import android.app.Application
import com.example.ncs3.utils.SharedPrefs

class MediCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefs.init(this)
    }
}